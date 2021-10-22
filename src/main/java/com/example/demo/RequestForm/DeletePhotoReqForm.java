package com.example.demo.RequestForm;

import com.example.demo.Validators.PhotoId.PhotoIdExists;

public class DeletePhotoReqForm {
    @PhotoIdExists
    public int photoId;
}
