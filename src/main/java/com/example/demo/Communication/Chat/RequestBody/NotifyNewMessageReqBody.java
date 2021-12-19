package com.example.demo.Communication.Chat.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class NotifyNewMessageReqBody {
    @ValidFamilyId
    public int familyId;
}
