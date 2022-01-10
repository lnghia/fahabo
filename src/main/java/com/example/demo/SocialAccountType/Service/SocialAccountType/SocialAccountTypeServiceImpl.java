package com.example.demo.SocialAccountType.Service.SocialAccountType;

import com.example.demo.SocialAccountType.Repo.SocialAccountTypeRepo;
import com.example.demo.SocialAccountType.Entity.SocialAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialAccountTypeServiceImpl implements SocialAccountTypeService{
    @Autowired
    private SocialAccountTypeRepo socialAccountTypeRepo;

    @Override
    public SocialAccountType getBySocialName(String name) {
        return socialAccountTypeRepo.findBySocialName(name);
    }

    @Override
    public SocialAccountType getById(String id) {
        return socialAccountTypeRepo.getById(id);
    }
}
