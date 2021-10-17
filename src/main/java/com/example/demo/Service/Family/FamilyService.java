package com.example.demo.Service.Family;

import com.example.demo.RequestForm.CreateFamilyReqForm;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;

import java.util.ArrayList;

public interface FamilyService {
    public Family saveFamily(Family family);
    Family findById(int id);
    String generateImgUploadId(int familyId);
    Family findByName(String name);
}
