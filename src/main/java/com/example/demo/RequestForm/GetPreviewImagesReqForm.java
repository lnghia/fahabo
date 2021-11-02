package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class GetPreviewImagesReqForm {
    @ValidFamilyId
    public int familyId;
}
