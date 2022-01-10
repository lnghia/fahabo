package com.example.demo.Family.Service.Family;

import com.example.demo.Family.Entity.Family;

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
