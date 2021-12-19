package com.example.demo.Notification.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class ClearChatNotiReqBody {
    @ValidFamilyId
    public int familyId;
}
