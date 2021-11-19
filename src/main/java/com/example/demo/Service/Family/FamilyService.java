package com.example.demo.Service.Family;

import com.example.demo.domain.Family.Family;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface FamilyService {
    public Family saveFamily(Family family);
    Family findById(int id);
    String generateImgUploadId(int familyId);
    Family findByName(String name);
    void deleteFamilyById(int familyId);
    boolean isHostInFamily(int userId, int familyId);
    ArrayList<Family> findAllFamily();
}
