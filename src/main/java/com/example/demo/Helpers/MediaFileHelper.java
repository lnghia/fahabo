package com.example.demo.Helpers;

import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.DropBox.UploadResult;
import com.example.demo.FileUploader.FileUploader;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.domain.Image;
import com.example.demo.domain.Photo;
import liquibase.pro.packaged.T;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class MediaFileHelper {
    private String IMAGE_DIR_PATH = "/home/nghiale/images";

    @Autowired
    private PhotoService photoService;

    public void saveImg(String fileName, String fileExt, InputStream inputStream) throws IOException {
        String fileAbsPath = String.format("%s/%s.%s", IMAGE_DIR_PATH, fileName, fileExt);
        File file = new File(fileAbsPath);
        OutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(outputStream);
    }

    public FileInputStream readImg(String fileName) throws FileNotFoundException {
        File file = new File(IMAGE_DIR_PATH + "/" + fileName);
        return new FileInputStream(file);
    }

    public ArrayList<ArrayList<Image>> saveImagesWithImgInStr(ArrayList<String> imagesInByte, ArrayList<Photo> photos) throws ExecutionException, InterruptedException {
        Date now = new Date();
        ArrayList<Image> rs = new ArrayList<>();
        ItemToUpload[] itemToUploads = new ItemToUpload[imagesInByte.size()];

        for (int i = 0; i < photos.size(); ++i) {
            Photo p = photos.get(i);
            String byteData = imagesInByte.get(i);
            itemToUploads[i] = new ItemToUpload(Long.toString(p.getId() + now.getTime()), byteData);
        }

        FileUploader fileUploader = new FileUploader();
        UploadExecutionResult uploadResult = fileUploader.uploadItems(itemToUploads);

        ArrayList<Image> successUploads = new ArrayList<>();
        ArrayList<Image> failUploads = new ArrayList<>();
        uploadResult.getCreationResults().forEach((k, v) -> {
            if (v.isOk()) {
                successUploads.add(new Image(k, v.uri.get()));
            } else {
                failUploads.add(new Image(k, (String) null));
            }
        });

        return new ArrayList<>(List.of(successUploads, failUploads));
    }

    public UploadResult saveImages(ArrayList<Image> images, ArrayList<Photo> photos, int albumId, int familyId) throws ExecutionException, InterruptedException {
        Date now = new Date();
        ArrayList<Image> rs = new ArrayList<>();
        ItemToUpload[] itemToUploads = Helper.getInstance().listOfImagesToArrOfItemToUploadWithGeneratedName(images, photos, albumId, familyId);

        for (int i = 0; i < photos.size(); ++i) {
            Photo p = photos.get(i);
            photoService.savePhoto(p);
            String byteData = images.get(i).getBase64Data();
            itemToUploads[i] = new ItemToUpload(p.getName(), byteData);
        }

        FileUploader fileUploader = new FileUploader();
        UploadExecutionResult uploadResult = fileUploader.uploadItems(itemToUploads);

        ArrayList<Image> successUploads = new ArrayList<>();
        ArrayList<Image> failUploads = new ArrayList<>();
        uploadResult.getCreationResults().forEach((k, v) -> {
            if (v.isOk()) {
                successUploads.add(new Image(k, v.uri.get()));
            } else {
                failUploads.add(new Image(k, (String) null));
            }
        });

        return new UploadResult(successUploads, failUploads);
    }

    public String updatePhoto(Image newImgInByte, Photo photo, int albumId, int familyId) throws ExecutionException, InterruptedException {
        FileUploader fileUploader = new FileUploader();

        ItemToUpload[] itemToUploads = Helper.getInstance().listOfImagesToArrOfItemToUploadWithGeneratedName(
                List.of(newImgInByte),
                List.of(photo),
                albumId,
                familyId
        );

        UploadExecutionResult uploadResult = fileUploader.uploadItems(itemToUploads);
        UploadResult rs = fileUploader.getSuccessesAndFails(uploadResult);
        ArrayList<Image> success = rs.getSuccessUploads();
        ArrayList<Image> fail = rs.getFailUploads();
        String photoUri = success.get(0).getUri();

        return photoUri;
    }
}
