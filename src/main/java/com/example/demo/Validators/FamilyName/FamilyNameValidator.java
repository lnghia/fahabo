package com.example.demo.Validators.FamilyName;

import com.example.demo.Helpers.FamilyHelper;
import com.example.demo.Service.Family.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FamilyNameValidator implements ConstraintValidator<ValidFamilyName, String> {
    @Autowired
    private FamilyService familyService;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isBlank() || s.isEmpty()) return false;

        return true;
    }
}
