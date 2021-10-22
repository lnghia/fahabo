package com.example.demo.RequestForm;

import com.example.demo.Validators.AlbumId.AlbumIdExist;

public class GetPhotosReqForm {
    @AlbumIdExist
    public int albumId;
}
