package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class JoinFamilyReqForm {
    @ValidFamilyId
    public int familyId;
}
