package com.example.demo.FileUploader;

import com.example.demo.DropBox.*;
import com.example.demo.Album.Entity.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class FileUploader implements AutoCloseable {
    //    @Value("${THREAD_NUM}")
    private int THREAD_NUM = 15;

    private ExecutorService uploadExecutor;

    private ExecutorService redirectExecutor;

    private CompletionService<FileUploadTask.ByteUploadResult> uploadService;

    private CompletionService<ItemCreationTask.ItemCreationResult> redirectService;

    private HashMap<String, FileUploadTask.ByteUploadResult> successfulUploads;

    private final List<FileUploadTask.ByteUploadResult> creationQueue;

    Map<String, ItemCreationTask.ItemCreationResult> creationResult = new HashMap<>();

    private HashMap<String, ItemCreationTask.ItemCreationResult> itemCreationResults;

    private List<FileUploadTask.ByteUploadResult> creationToRetry = new ArrayList<>();

    private ArrayList<ItemToUpload> uploadToRetry = new ArrayList<>();

    private final HashMap<String, FileUploadTask.ByteUploadResult> failedUploads = new HashMap<>();


    public FileUploader() {
        uploadExecutor = Executors.newFixedThreadPool(THREAD_NUM);
        redirectExecutor = Executors.newFixedThreadPool(THREAD_NUM);
        uploadService = new ExecutorCompletionService<>(uploadExecutor);
        redirectService = new ExecutorCompletionService<>(redirectExecutor);
        creationQueue = new ArrayList<>(40);
        itemCreationResults = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        if (uploadExecutor != null) {
            uploadExecutor.shutdown();
        }
    }

    public UploadExecutionResult uploadItems(ItemToUpload[] items) throws InterruptedException, ExecutionException {
        uploadToRetry = new ArrayList<>();
        successfulUploads = new HashMap<>(items.length);

        scheduleUploadItemBytes(items);

        log.info("All byte uploads tasks have been scheduled.");

        for (int finished = 0; finished < items.length; ++finished) {
            Future<FileUploadTask.ByteUploadResult> resultFuture = uploadService.take();
            FileUploadTask.ByteUploadResult byteUploadResult = resultFuture.get();

            if (byteUploadResult.isOk()) {
                successfulUploads.put(byteUploadResult.name, byteUploadResult);
                creationQueue.add(byteUploadResult);
            } else {
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

        if (!uploadToRetry.isEmpty()) {
            retryUploadItems();
        }

        if (creationQueue.size() >= 40 || successfulUploads.size() + failedUploads.size() >= items.length) {
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

        for (int finished = 0; finished < uploadToRetry.size(); ++finished) {
            Future<FileUploadTask.ByteUploadResult> resultFuture = uploadService.take();
            FileUploadTask.ByteUploadResult byteUploadResult = resultFuture.get();

            if (byteUploadResult.isOk()) {
                successfulUploads.put(byteUploadResult.name, byteUploadResult);
                creationQueue.add(byteUploadResult);
            } else {
                log.error(byteUploadResult.getName(), byteUploadResult.getError());
                failedUploads.put(byteUploadResult.name, byteUploadResult);
            }
        }

        log.info("Execution complete.");
    }

    private void createSharedLinks() throws InterruptedException, ExecutionException {
        List<FileUploadTask.ByteUploadResult> itemsToCreate = new ArrayList<>(creationQueue);
        creationQueue.clear();

        for (var item : itemsToCreate) {
            creationResult.put(item.name, ItemCreationTask.ItemCreationResult.createSuccessResult(item.name, null, item.uri));
        }

        itemCreationResults.putAll(creationResult);
    }

    private void scheduleUploadItemBytes(ItemToUpload[] items) {
        for (var item : items) {
            log.info("Scheduling byte upload for: " + item.getName());
            FileUploadTask task = new FileUploadTask(item.getInputStream(), item.getName(), null);
            uploadService.submit(task);
        }
    }

    public void printState() {
        log.error("The following " + failedUploads.size() + " files could not be uploaded:");

        for (FileUploadTask.ByteUploadResult uploadResult : failedUploads.values()) {
            // Print the error that lead to this failure.
            //  If it was an ApiException there may be some additional details that could be examined
            // before retrying it as
            //  needed. Here it is just printed out.
            log.error(uploadResult.error.toString());
        }
    }

    public UploadResult getSuccessesAndFails(UploadExecutionResult result) {
        ArrayList<Image> successUploads = new ArrayList<>();
        ArrayList<Image> failUploads = new ArrayList<>();

        result.getCreationResults().forEach((k, v) -> {
            if (v.isOk()) {
                successUploads.add(new Image(k, v.uri.get()));
            } else {
                failUploads.add(new Image(k, (String) null));
            }
        });

        return new UploadResult(successUploads, failUploads);
    }
}
