package com.example.demo.DropBox;

import com.example.demo.domain.Image;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

@Slf4j
public class DropBoxRedirectedLinkGetter implements AutoCloseable {

    private int THREAD_NUM = 4;

    private ExecutorService executor;

    private CompletionService<GetRedirectedLinkTask.GetRedirectedLinkResult> redirectedLinkGetterService;

    private HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> successfulGet;

    private HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> failedGet;

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
        scheduleTask(itemsToGet);

        log.info("All redirected image link gets tasks have been scheduled.");

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
