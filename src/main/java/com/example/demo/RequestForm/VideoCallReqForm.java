package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class VideoCallReqForm {
    @ValidFamilyId
    public int familyId;

    public int[] participantIds;

    public String roomCallId;
}
