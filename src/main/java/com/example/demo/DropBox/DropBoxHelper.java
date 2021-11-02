package com.example.demo.DropBox;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Image;
import com.sun.mail.imap.protocol.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class DropBoxHelper {
    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

    public String generateNameToUpload(int albumId, int albumType){
        return String.format("%d_%d_%d.jpg", albumId, albumType, new Date().getTime());
    }

    public ItemToUpload[] arrayOfBase64StrToArrayOfItemsToUpload(String[] images, int albumId, int albumType){
        return Arrays.stream(images).map(image -> {
            return new ItemToUpload(generateNameToUpload(albumId, albumType), image);
        }).toArray(size -> new ItemToUpload[size]);
    }

    public UploadResult uploadImages(ItemToUpload[] images, int albumId, int albumType) throws ExecutionException, InterruptedException {
//        ItemToUpload[] itemToUploads = arrayOfBase64StrToArrayOfItemsToUpload(images, albumId, albumType);

        DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();
        DropBoxUploader uploader = new DropBoxUploader(clientV2);

        UploadExecutionResult executionResult = uploader.uploadItems(images);

        ArrayList<Image> successUploads = new ArrayList<>();
        ArrayList<Image> failUploads = new ArrayList<>();

        executionResult.getCreationResults().forEach((k, v) -> {
            if (v.isOk()) {
                successUploads.add(new Image(k, v.metadata, v.uri.get()));
            } else {
                failUploads.add(new Image(k, v.metadata));
            }
        });

        return new UploadResult(successUploads, failUploads);
    }
}
