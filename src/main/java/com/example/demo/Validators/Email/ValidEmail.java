package com.example.demo.Validators.Email;

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
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
    String message() default "validation.email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
