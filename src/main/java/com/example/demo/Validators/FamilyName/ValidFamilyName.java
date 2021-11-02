package com.example.demo.Validators.FamilyName;

import com.example.demo.Validators.FamilyId.FamilyIdValidator;
import com.example.demo.Validators.IdValidator.IdValidator;

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
@Constraint(validatedBy = FamilyNameValidator.class)
public @interface ValidFamilyName {
    String message() default "validation.familyNameInvalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
