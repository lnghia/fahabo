package com.example.demo.Validators.RequestBody;

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
public @interface RequestBodyRequired {
    String message() default "validation.requestBodyRequired";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
