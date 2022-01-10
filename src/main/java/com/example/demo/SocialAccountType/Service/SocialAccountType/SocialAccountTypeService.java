package com.example.demo.SocialAccountType.Service.SocialAccountType;

import com.example.demo.SocialAccountType.Entity.SocialAccountType;

public interface SocialAccountTypeService {
    SocialAccountType getBySocialName(String name);
    SocialAccountType getById(String id);
}
