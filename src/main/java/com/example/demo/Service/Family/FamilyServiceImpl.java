package com.example.demo.Service.Family;

import com.example.demo.Repo.FamilyRepo;
import com.example.demo.RequestForm.CreateFamilyReqForm;
import com.example.demo.Service.Chore.ChoreService;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FamilyServiceImpl implements FamilyService{
    @Autowired
    private FamilyRepo familyRepo;

    @Override
    public Family saveFamily(Family family) {
        return familyRepo.save(family);
    }

    @Override
    public Family findById(int id) {
        return familyRepo.findById(id);
    }

    @Override
    public String generateImgUploadId(int familyId) {
        return String.format("thumbnail_%s_%s.png", familyId, new Date().getTime());
    }

    @Override
    public Family findByName(String name) {
        return familyRepo.findByFamilyName(name);
    }

    @Override
    public void deleteFamilyById(int familyId) {
        Family family = familyRepo.getById(familyId);
        familyRepo.delete(family);
    }

    @Override
    public boolean isHostInFamily(int userId, int familyId) {
        return familyRepo.isHostInFamily(userId, familyId) > 0;
    }
}
