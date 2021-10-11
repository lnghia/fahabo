package com.example.demo.Validators.LangCode;

import com.example.demo.Service.LanguageCode.LanguageCodeService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LangCodeValidator implements ConstraintValidator<ValidLangCode, String> {
    @Autowired
    private LanguageCodeService languageCodeService;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isBlank() || s.isEmpty()) return true;

        return languageCodeService.isLangCodeSupported(s);
    }
}
