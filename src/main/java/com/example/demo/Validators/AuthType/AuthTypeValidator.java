package com.example.demo.Validators.AuthType;


import com.example.demo.SocialAccountType.Service.SocialAccountType.SocialAccountTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthTypeValidator implements ConstraintValidator<ValidAuthType, String> {
    @Autowired
    private SocialAccountTypeService socialAccountTypeService;

    @Override
    public boolean isValid(String authType, ConstraintValidatorContext constraintValidatorContext) {
        if(authType == null || authType.isBlank() || authType.isEmpty()) return false;

        return socialAccountTypeService.getById(authType) != null;
    }
}
