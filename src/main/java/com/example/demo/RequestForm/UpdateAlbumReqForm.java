package com.example.demo.RequestForm;

import com.example.demo.Validators.AlbumId.AlbumIdExist;

public class UpdateAlbumReqForm {
    @AlbumIdExist
    public int albumId;

    public String title;

    public String description;
}
