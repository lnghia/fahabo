package com.example.demo.RequestForm;

import com.example.demo.Album.Entity.Image;
import lombok.Data;

import java.util.ArrayList;

@Data
public class UploadImagesReqForm {
    private ArrayList<Image> images;
}
