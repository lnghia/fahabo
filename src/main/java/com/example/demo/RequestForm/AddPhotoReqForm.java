package com.example.demo.RequestForm;

import com.example.demo.Validators.AlbumId.AlbumIdExist;
import com.example.demo.domain.Image;

import java.util.ArrayList;

public class AddPhotoReqForm {
    public ArrayList<Image> photos;

    @AlbumIdExist
    public int albumId;
}
