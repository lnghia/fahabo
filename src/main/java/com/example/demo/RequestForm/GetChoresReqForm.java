package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

import java.util.List;

public class GetChoresReqForm {
    @ValidFamilyId
    public int familyId;

    public String searchText;

    public String from;

    public String to;

    public List<Integer> assigneeIds;

    public List<String> statuses;

    public String sortBy;
}
