package com.example.demo.Validators.Name;

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
@Constraint(validatedBy = NameValidator.class)
public @interface ValidName {
    String message() default "validation.name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
