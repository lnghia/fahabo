package com.example.demo.FileUploader;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.Helpers.Helper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.concurrent.Callable;

import static com.example.demo.FileUploader.FileUploadTask.ByteUploadResult.createFailureResultWithThrowable;

@Slf4j
public class FileUploadTask implements Callable<FileUploadTask.ByteUploadResult> {

    private DbxClientV2 dbxClientV2;

    private final InputStream fileStreamToUpload;

    private final String fileName;

    public FileUploadTask(InputStream fileStream, String fileName, DbxClientV2 clientV2) {
        this.fileStreamToUpload = fileStream;
        this.fileName = fileName;
        dbxClientV2 = clientV2;
    }

    public InputStream getFileStreamToUpload() {
        return fileStreamToUpload;
    }

    @Override
    public ByteUploadResult call() {
        try {
            String fileExt = "png";
            String[] tokens = fileName.split("\\.");
            if (tokens.length > 1) {
                fileExt = "";
            }
            Helper.getInstance().writeImgFile(fileName, fileExt, fileStreamToUpload);

            return FileUploadTask.ByteUploadResult.createSuccessResult(fileStreamToUpload, String.format("%s/%s", "/api/v1/photos", fileName), fileName);
        } catch (Exception ex) {
            return FileUploadTask.ByteUploadResult.createFailureResultWithThrowable(fileStreamToUpload, ex, fileName);
        }
    }

    @Data
    public static class ByteUploadResult {
        public final InputStream fileStreamToUpload;

        public final String name;

        public final String uri;

        public final Throwable error;

        public ByteUploadResult(InputStream fileStreamToUpload, Throwable error, String uri, String name) {
            this.fileStreamToUpload = fileStreamToUpload;
            this.uri = uri;
            this.error = error;
            this.name = name;
        }

        public ByteUploadResult(InputStream fileStreamToUpload, Throwable error, String name) {
            this.fileStreamToUpload = fileStreamToUpload;
            this.error = error;
            this.name = name;
            this.uri = "";
        }

        public boolean isOk() {
            return error == null;
        }

        public static FileUploadTask.ByteUploadResult createSuccessResult(InputStream file, String uri, String name) {
            return new FileUploadTask.ByteUploadResult(file, null, uri, name);
        }

        public static FileUploadTask.ByteUploadResult createFailureResultWithThrowable(InputStream file, Throwable error, String name) {
            return new FileUploadTask.ByteUploadResult(file, error, null, name);
        }
    }
}
