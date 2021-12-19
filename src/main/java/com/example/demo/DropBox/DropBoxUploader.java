package com.example.demo.DropBox;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.sharing.PathLinkMetadata;
import javassist.compiler.ast.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class DropBoxUploader implements AutoCloseable{
//    @Value("${THREAD_NUM}")
    private int THREAD_NUM = 15;

//    @Autowired
//    private DropBoxConfig dropBoxConfig;

    private DbxClientV2 dbxClientV2;

    private ExecutorService uploadExecutor;

    private ExecutorService redirectExecutor;

    private CompletionService<ByteUploadTask.ByteUploadResult> uploadService;

    private CompletionService<ItemCreationTask.ItemCreationResult> redirectService;

    private HashMap<String, ByteUploadTask.ByteUploadResult> successfulUploads;

    private final List<ByteUploadTask.ByteUploadResult> creationQueue;

    Map<String, ItemCreationTask.ItemCreationResult> creationResult = new HashMap<>();

    private HashMap<String, ItemCreationTask.ItemCreationResult> itemCreationResults;

    private List<ByteUploadTask.ByteUploadResult> creationToRetry = new ArrayList<>();

    private ArrayList<ItemToUpload> uploadToRetry = new ArrayList<>();

    private final HashMap<String, ByteUploadTask.ByteUploadResult> failedUploads = new HashMap<>();


    public DropBoxUploader(){
        uploadExecutor = Executors.newFixedThreadPool(THREAD_NUM);
        uploadService = new ExecutorCompletionService<>(uploadExecutor);
        creationQueue = new ArrayList<>(40);
        itemCreationResults = new HashMap<>();
    }

    public DropBoxUploader(DbxClientV2 client){
        dbxClientV2 = client;
        uploadExecutor = Executors.newFixedThreadPool(THREAD_NUM);
        redirectExecutor = Executors.newFixedThreadPool(THREAD_NUM);
        uploadService = new ExecutorCompletionService<>(uploadExecutor);
        redirectService = new ExecutorCompletionService<>(redirectExecutor);
        creationQueue = new ArrayList<>(40);
        itemCreationResults = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        if(uploadExecutor != null){
            uploadExecutor.shutdown();
        }
    }

    public UploadExecutionResult uploadItems(ItemToUpload[] items) throws InterruptedException, ExecutionException {
        if(dbxClientV2 == null){
            log.error("Dropbox client was not initialized.");
            return null;
        }

        uploadToRetry = new ArrayList<>();
        successfulUploads = new HashMap<>(items.length);

        scheduleUploadItemBytes(items);

        log.info("All byte uploads tasks have been scheduled.");

        for(int finished = 0; finished < items.length; ++finished){
            Future<ByteUploadTask.ByteUploadResult> resultFuture = uploadService.take();
            ByteUploadTask.ByteUploadResult byteUploadResult = resultFuture.get();

            if(byteUploadResult.isOk()){
                successfulUploads.put(byteUploadResult.name, byteUploadResult);
                creationQueue.add(byteUploadResult);
            }
            else{
//                log.info(byteUploadResult.getError().getMessage());
                log.error(byteUploadResult.getName(), byteUploadResult.getError());
                log.info("Preparing to retry: ", byteUploadResult.getName());
                try {
                    byteUploadResult.getFileStreamToUpload().reset();
                    uploadToRetry.add(new ItemToUpload(byteUploadResult.name, byteUploadResult.fileStreamToUpload));
                } catch (IOException e) {
                    failedUploads.put(byteUploadResult.name, byteUploadResult);
                    e.printStackTrace();
                }
            }
        }

        if(!uploadToRetry.isEmpty()){
            retryUploadItems();
        }

        if(creationQueue.size() >= 40 || successfulUploads.size() + failedUploads.size() >= items.length){
            log.info("Starting getSharedLink call.");
            createSharedLinks();
        }

        log.info("Execution complete.");

        return new UploadExecutionResult(itemCreationResults);
    }

    public void retryUploadItems() throws InterruptedException, ExecutionException {
        ItemToUpload[] tmp = uploadToRetry.toArray(new ItemToUpload[uploadToRetry.size()]);

        scheduleUploadItemBytes(tmp);

        log.info("All byte upload retry tasks have been scheduled.");

        for(int finished = 0; finished < uploadToRetry.size(); ++finished){
            Future<ByteUploadTask.ByteUploadResult> resultFuture = uploadService.take();
            ByteUploadTask.ByteUploadResult byteUploadResult = resultFuture.get();

            if(byteUploadResult.isOk()){
                successfulUploads.put(byteUploadResult.name, byteUploadResult);
                creationQueue.add(byteUploadResult);
            }
            else{
//                log.info(byteUploadResult.getError().getMessage());
                log.error(byteUploadResult.getName(), byteUploadResult.getError());
                failedUploads.put(byteUploadResult.name, byteUploadResult);
            }
        }

        log.info("Execution complete.");
    }

    public String createSharedLink(String uri){
        if(uri == null || uri.isEmpty() || uri.isBlank()){
            log.info("No item to create.");
            return null;
        }

        URLConnection con = null;
        try {
            con = new URL(uri.replace("dl=0", "raw=1")).openConnection();
            con.connect();
            InputStream in = con.getInputStream();
            in.close();

            return con.getURL().toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Error while trying to get redirected uri for: " + uri);
        }

        return null;
    }

    private void createSharedLinks() throws InterruptedException, ExecutionException {
        if(creationQueue.isEmpty()){
            log.info("No items to create.");
            return;
        }

        Map<String, ByteUploadTask.ByteUploadResult> initialItems = new HashMap<>();
        List<ByteUploadTask.ByteUploadResult> itemsToCreate = new ArrayList<>(creationQueue);
        creationQueue.clear();

        for(var item : itemsToCreate){
            initialItems.put(item.name, item);
        }

        scheduleRedirects(itemsToCreate);

        log.info("All redirect tasks have been scheduled.");

        for(int finished = 0; finished < itemsToCreate.size(); ++finished){
            Future<ItemCreationTask.ItemCreationResult> resultFuture = redirectService.take();
            ItemCreationTask.ItemCreationResult redirectResult = resultFuture.get();

            if(redirectResult.isOk()){
                creationResult.put(redirectResult.name, redirectResult);
            }
            else{
//                log.info(byteUploadResult.getError().getMessage());
                log.error(redirectResult.name, redirectResult.error);
                log.info("Preparing to retry: ", redirectResult.name);
                creationToRetry.add(initialItems.get(redirectResult.name));
//                failedUploads.put(byteUploadResult.name, byteUploadResult);

            }
        }

        if(creationToRetry.size() > 0){
            retryCreateSharedLinks();
        }

        itemCreationResults.putAll(creationResult);
    }

    private void retryCreateSharedLinks() throws InterruptedException, ExecutionException {
        if(creationToRetry.isEmpty()){
            log.info("No items to create.");
            return;
        }

        Map<String, ByteUploadTask.ByteUploadResult> initialItems = new HashMap<>();

        for(var item : creationToRetry){
            initialItems.put(item.name, item);
        }

        scheduleRedirects(creationToRetry);

        log.info("All redirect retry tasks have been scheduled.");

        for(int finished = 0; finished < creationToRetry.size(); ++finished){
            Future<ItemCreationTask.ItemCreationResult> resultFuture = redirectService.take();
            ItemCreationTask.ItemCreationResult redirectResult = resultFuture.get();

            if(redirectResult.isOk()){
                creationResult.put(redirectResult.name, redirectResult);
            }
            else{
//                log.info(byteUploadResult.getError().getMessage());
                log.error(redirectResult.name, redirectResult.error);
//                failedUploads.put(redirectResult.name, initialItems.get(redirectResult.name));
            }
        }
    }

    private void scheduleUploadItemBytes(ItemToUpload[] items){
        for(var item : items){
            log.info("Scheduling byte upload for: " + item.getName());
            ByteUploadTask task = new ByteUploadTask(item.getInputStream(), item.getName(), dbxClientV2);
            uploadService.submit(task);
        }
    }

    private void scheduleRedirects(List<ByteUploadTask.ByteUploadResult> itemsToCreate){
        for(var uploadResult : itemsToCreate){
            log.info("Scheduling redirect for: " + uploadResult.name);
            ItemCreationTask itemCreationTask = new ItemCreationTask(dbxClientV2, uploadResult);
            redirectService.submit(itemCreationTask);
        }
    }

    public void printState(){
        log.error("The following " + failedUploads.size() + " files could not be uploaded:");

        for (ByteUploadTask.ByteUploadResult uploadResult : failedUploads.values()) {
            // Print the error that lead to this failure.
            //  If it was an ApiException there may be some additional details that could be examined
            // before retrying it as
            //  needed. Here it is just printed out.
            log.error(uploadResult.error.toString());
        }
    }
}
