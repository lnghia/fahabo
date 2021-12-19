package com.example.demo.Validators.PhotoId;

import com.example.demo.Validators.RequestBody.RequestBodyValidator;

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
@Constraint(validatedBy = PhotoIdValidator.class)
public @interface PhotoIdExists {
    String message() default "validation.photoIdNotExist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
