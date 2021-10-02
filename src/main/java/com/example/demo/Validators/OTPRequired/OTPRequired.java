package com.example.demo.Validators.OTPRequired;

import com.example.demo.Validators.Username.UsernameValidator;

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
@Constraint(validatedBy = UsernameValidator.class)
public @interface OTPRequired {
    String message() default "verification.fail";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
