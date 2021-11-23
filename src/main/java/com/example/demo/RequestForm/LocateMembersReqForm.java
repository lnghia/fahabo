package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class LocateMembersReqForm {
    @ValidFamilyId
    public int familyId;
}
