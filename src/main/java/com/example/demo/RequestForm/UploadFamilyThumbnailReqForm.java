package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;
import com.example.demo.domain.Image;

public class UploadFamilyThumbnailReqForm {
    @ValidFamilyId
    public int familyId;

    public Image thumbnail;
}
