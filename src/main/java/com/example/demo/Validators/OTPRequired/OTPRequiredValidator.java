package com.example.demo.Validators.OTPRequired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OTPRequiredValidator implements ConstraintValidator<OTPRequired, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isEmpty() || s.isBlank())
            return false;

        return true;
    }
}
