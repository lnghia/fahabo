package com.example.demo.Event.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class CreateEventReqBody {
    @ValidFamilyId
    public int familyId;

    public String title;

    public String description;

    public String from;

    public String to;

    public int[] assigneeIds;

    public String[] photos;

    public String repeatType;

    public Integer occurrences;
}
