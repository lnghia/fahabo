package com.example.demo.Service.Family;

import com.example.demo.RequestForm.CreateFamilyReqForm;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;

import java.util.ArrayList;

public interface FamilyService {
    boolean setHost(User user, Family family);
    void addUser(ArrayList<User> users, Family family);
    void updateFamily(Family family);
    Family createFamily(User creator, Family createdFamily, CreateFamilyReqForm requestBody);
    void addMember(User user, Family family);
    Family findByName(String name);
    Family findById(int id);
    Integer findMemberById(int id);
}
