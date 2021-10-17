package com.example.demo.Service.SocialAccountType;

import com.example.demo.Repo.SocialAccountTypeRepo;
import com.example.demo.domain.SocialAccountType;
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
