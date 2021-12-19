package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyName.ValidFamilyName;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;

import java.util.List;

public class CreateFamilyReqForm {
    @ValidFamilyName
    public String familyName;

    public Image thumbnail;

    public List<Integer> ids;

    public String timezone;
}
