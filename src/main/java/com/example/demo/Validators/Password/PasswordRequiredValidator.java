package com.example.demo.Validators.Password;

import org.hibernate.validator.cfg.context.Constrainable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordRequiredValidator implements ConstraintValidator<PasswordRequired, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isBlank() || s.isEmpty())
            return false;

        return true;
    }
}
