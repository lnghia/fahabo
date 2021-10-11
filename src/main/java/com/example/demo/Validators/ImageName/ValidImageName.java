package com.example.demo.Validators.ImageName;

import com.example.demo.Validators.Email.EmailFormatValidator;

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
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ValidImageName {
    String message() default "validation.imageNameInvalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
