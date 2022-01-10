package com.example.demo.Validators.FamilyId;

import com.example.demo.Family.Service.Family.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FamilyIdValidator implements ConstraintValidator<ValidFamilyId, Integer> {
    @Autowired
    private FamilyService familyService;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        if(id == null) return false;

        return familyService.findById(id) != null;
    }
}
