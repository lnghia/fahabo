package com.example.demo.Validators.ImageName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageNameValidator implements ConstraintValidator<ValidImageName, String> {
    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        if(name == null || name.isEmpty() || name.isBlank()){
            return false;
        }

        return true;
    }
}
