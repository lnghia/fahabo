package com.example.demo.Event.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

import java.util.Date;

public class FindDatesContainEventsReqBody {
    @ValidFamilyId
    public int familyId;

    public String from;

    public String to;
}
