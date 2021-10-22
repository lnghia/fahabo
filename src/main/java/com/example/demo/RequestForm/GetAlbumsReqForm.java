package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class GetAlbumsReqForm {
    @ValidFamilyId
    public int familyId;
}
