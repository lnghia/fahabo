package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class UpdateChoreReqForm {
    public int choreId;

    public String status;

    public String title;

    public String description;

    public String deadline;

    public int[] assigneeIds;

    public String[] photos;

    public int[] deletePhotos;

    public String repeatType;
}
