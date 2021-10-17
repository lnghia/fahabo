package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class KickMemberReqForm {
    public int userIdToKick;

    @ValidFamilyId
    public int familyId;
}
