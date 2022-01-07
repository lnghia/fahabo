package com.example.demo.HomeCook.Helper;

import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.DropBox.UploadResult;
import com.example.demo.FileUploader.FileUploader;
import com.example.demo.Helpers.MediaFileHelper;
import com.example.demo.HomeCook.Entity.CookPost;
import com.example.demo.HomeCook.Service.CookPostService;
import com.example.demo.domain.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Component
public class CookPostHelper {
    @Autowired
    private MediaFileHelper mediaFileHelper;

    @Autowired
    private CookPostService cookPostService;

    public String saveThumbnail(String thumbnailInBytes, CookPost cookPost) throws ExecutionException, InterruptedException {
        Date now = new Date();
        ItemToUpload[] items = new ItemToUpload[1];
        items[0] = new ItemToUpload(cookPost.getId() + "_cuisine" + "_" + now.getTime() + ".jpg", thumbnailInBytes);
        String thumbnailUri = null;

        FileUploader fileUploader = new FileUploader();

        UploadExecutionResult result = fileUploader.uploadItems(items);
        UploadResult rs = fileUploader.getSuccessesAndFails(result);
        ArrayList<Image> success = rs.getSuccessUploads();

        if (!success.isEmpty()) {
            thumbnailUri = success.get(0).getUri();
            cookPost.setThumbnail(success.get(0).getUri());
            cookPostService.save(cookPost);
        }

        return thumbnailUri;
    }
}

