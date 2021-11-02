package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class FamilyDetailReqForm {
    @ValidFamilyId
    public int familyId;
}
