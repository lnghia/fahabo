package com.example.demo.Validators.Password;

import Messages.ResponseMsg;

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
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "validation.passwordInvalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
