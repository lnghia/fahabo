package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

import java.util.ArrayList;

public class CreateChoreReqForm {
    @ValidFamilyId
    public int familyId;

    public String status;

    public String title;

    public String description;

    public String deadline;

    public int[] assigneeIds;

    public String[] photos;

    public String repeatType;
}
