package com.example.demo.RequestForm;

import com.example.demo.Validators.FamilyId.ValidFamilyId;
import com.example.demo.Validators.TitleRequired.TitleRequired;

public class CreateAlbumReqForm {
    @TitleRequired
    public String title;

    public String description;

    @ValidFamilyId
    public int familyId;
}
