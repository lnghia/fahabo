package com.example.demo.RequestForm;

import com.example.demo.Validators.PhotoId.PhotoIdExists;

public class UpdatePhotoReqForm {
    @PhotoIdExists
    public int photoId;

    public String base64Data;
}
