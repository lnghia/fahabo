package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyName.ValidFamilyName;
import com.example.demo.Album.Entity.Image;

import java.util.List;

public class CreateFamilyReqForm {
    @ValidFamilyName
    public String familyName;

    public Image thumbnail;

    public List<Integer> ids;

    public String timezone;
}
