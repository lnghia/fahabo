package com.example.demo.DropBox;

import lombok.Data;

import java.io.InputStream;
import java.util.HashMap;

@Data
public class UploadExecutionResult {
//    private HashMap<String, ByteUploadTask.ByteUploadResult> successfulUploads;
//
//    private HashMap<String, ByteUploadTask.ByteUploadResult> failedUploads;

    private HashMap<String, ItemCreationTask.ItemCreationResult> creationResults;

//    public UploadExecutionResult(HashMap<String, ByteUploadTask.ByteUploadResult> successfulUploads, HashMap<String, ByteUploadTask.ByteUploadResult> failedUploads) {
//        this.successfulUploads = successfulUploads;
//        this.failedUploads = failedUploads;
//    }


    public UploadExecutionResult(HashMap<String, ItemCreationTask.ItemCreationResult> creationResult) {
        this.creationResults = creationResult;
    }
}
