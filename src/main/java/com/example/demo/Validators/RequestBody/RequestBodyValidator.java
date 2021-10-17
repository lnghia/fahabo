package com.example.demo.Validators.RequestBody;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestBodyValidator implements ConstraintValidator<RequestBodyRequired, Object> {
    @Override
    public boolean isValid(Object body, ConstraintValidatorContext constraintValidatorContext) {
        return !(body == null);
    }
}
