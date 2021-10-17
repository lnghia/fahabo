package com.example.demo.Validators.AuthType;

import com.example.demo.Validators.Birthday.BirthdayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE, TYPE_USE })
@Constraint(validatedBy = AuthTypeValidator.class)
public @interface ValidAuthType {
    String message() default "validation.authTypeInvalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
