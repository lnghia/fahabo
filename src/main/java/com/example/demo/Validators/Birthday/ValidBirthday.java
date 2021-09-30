package com.example.demo.Validators.Birthday;

import com.example.demo.Validators.Email.EmailValidator;

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
@Constraint(validatedBy = BirthdayValidator.class)
public @interface ValidBirthday {
    String message() default "validation.birthday";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
