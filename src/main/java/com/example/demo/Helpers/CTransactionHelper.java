package com.example.demo.Helpers;

import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.DropBox.UploadResult;
import com.example.demo.FileUploader.FileUploader;
import com.example.demo.Album.Entity.Image;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Component
@Data
public class CTransactionHelper {
    @Autowired
    private MediaFileHelper mediaFileHelper;

    public String addIconToCategory(String iconInBytes, String name, int familyId) throws ExecutionException, InterruptedException {
        Date now = new Date();
        ItemToUpload[] items = new ItemToUpload[1];
        items[0] = new ItemToUpload(name + "_" + Integer.toString(familyId) + "_" + now.getTime() + ".jpg", iconInBytes);

        FileUploader fileUploader = new FileUploader();
        UploadExecutionResult result = fileUploader.uploadItems(items);
        UploadResult rs = fileUploader.getSuccessesAndFails(result);
        ArrayList<Image> success = rs.getSuccessUploads();

        if (success.isEmpty()) return null;

        return success.get(0).getUri();
    }
}
