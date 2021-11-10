package com.example.demo.DropBox;

import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Image;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

@Slf4j
public class DropBoxRedirectedLinkGetter implements AutoCloseable {

    private int THREAD_NUM = 15;

    private ExecutorService executor;

    private CompletionService<GetRedirectedLinkTask.GetRedirectedLinkResult> redirectedLinkGetterService;

    private HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> successfulGet;

    private HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> failedGet;

    private ArrayList<Image> retryGet = new ArrayList<>();

    public DropBoxRedirectedLinkGetter() {
        executor = Executors.newFixedThreadPool(THREAD_NUM);
        redirectedLinkGetterService = new ExecutorCompletionService<>(executor);
        successfulGet = new HashMap<>();
        failedGet = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public GetRedirectedLinkExecutionResult getRedirectedLinks(ArrayList<Image> itemsToGet) throws InterruptedException, ExecutionException {
        ArrayList<Image> validItemsToGet = new ArrayList<>();
        for(var img : itemsToGet){
            if(!Helper.getInstance().isDropboxUri(img.getUri())){
                successfulGet.put(img.getName(), new GetRedirectedLinkTask.GetRedirectedLinkResult(img.getName(), img.getUri(), null));
            }
            else {
                validItemsToGet.add(img);
            }
        }

        scheduleTask(validItemsToGet);

        log.info("All redirected image link gets tasks have been scheduled.");

        HashMap<String, Image> items = new HashMap<>();
        for(var img : validItemsToGet){
            items.put(img.getName(), img);
        }

        for (int i = 0; i < validItemsToGet.size(); ++i) {
            Future<GetRedirectedLinkTask.GetRedirectedLinkResult> resultFuture = redirectedLinkGetterService.take();
            GetRedirectedLinkTask.GetRedirectedLinkResult result = resultFuture.get();

            if (result.isOk()) {
                successfulGet.put(result.name, result);
            }
            else {
                log.error(result.name, result.error);
                retryGet.add(items.get(result.name));
            }
        }

        if(retryGet.size() > 0){
            retryRedirectingLinks(retryGet);
        }

        log.info("Execution complete.");

        return new GetRedirectedLinkExecutionResult(successfulGet, failedGet);
    }

    public GetRedirectedLinkExecutionResult retryRedirectingLinks(ArrayList<Image> itemsToGet) throws InterruptedException, ExecutionException {
        log.info("Retrying to get redirect links: %d", itemsToGet.size());

        scheduleTask(itemsToGet);

        log.info("All redirected image link retry tasks have been scheduled.");

        HashMap<String, Image> items = new HashMap<>();
        for(var img : itemsToGet){
            items.put(img.getName(), img);
        }

        for (int i = 0; i < itemsToGet.size(); ++i) {
            Future<GetRedirectedLinkTask.GetRedirectedLinkResult> resultFuture = redirectedLinkGetterService.take();
            GetRedirectedLinkTask.GetRedirectedLinkResult result = resultFuture.get();

            if (result.isOk()) {
                successfulGet.put(result.name, result);
            }
            else {
                log.error(result.name, result.error);
                failedGet.put(result.name, result);
            }
        }

        log.info("Execution complete.");

        return new GetRedirectedLinkExecutionResult(successfulGet, failedGet);
    }

    public void scheduleTask(ArrayList<Image> items) {
        items.forEach(item -> {
            log.info("Scheduling getting redirected link for: " + item.getName());
            GetRedirectedLinkTask task = new GetRedirectedLinkTask(item.getName(), item.getUri());
            redirectedLinkGetterService.submit(task);
        });
    }
}
