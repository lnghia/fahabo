package com.example.demo.Validators.TitleRequired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TitleRequiredValidator implements ConstraintValidator<TitleRequired, String> {
    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintValidatorContext) {
        return !(object == null || object.toString().isEmpty() || object.toString().isBlank());
    }
}
