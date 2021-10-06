package com.example.demo.Service.SocialAccountType;

import com.example.demo.domain.SocialAccountType;

public interface SocialAccountTypeService {
    SocialAccountType getBySocialName(String name);
    SocialAccountType getById(String id);
}
