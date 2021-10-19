package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class UpdateFamilyReqForm {
    public String name;

    @ValidFamilyId
    public int familyId;
}
