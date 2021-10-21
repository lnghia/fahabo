package com.example.demo.RequestForm;

import com.example.demo.Validators.AlbumId.AlbumIdExist;

public class DeleteAlbumReqForm {
    @AlbumIdExist
    public int albumId;
}
