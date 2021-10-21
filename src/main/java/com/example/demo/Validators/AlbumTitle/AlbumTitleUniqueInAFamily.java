package com.example.demo.Validators.AlbumTitle;

import com.example.demo.Validators.AuthType.AuthTypeValidator;

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
@Constraint(validatedBy = AlbumTitleUniqueInAFamilyValidator.class)
public @interface AlbumTitleUniqueInAFamily {
    String message() default "validation.albumTitleExists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
