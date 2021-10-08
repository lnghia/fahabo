package com.example.demo.Validators.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NewPasswordValidator implements ConstraintValidator<NewPasswordRequired, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isBlank() || s.isEmpty())
            return false;

        return true;
    }
}
