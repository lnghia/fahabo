package com.example.demo.Service.LanguageCode;

import com.example.demo.domain.LanguageCode;

import java.util.List;

public interface LanguageCodeService {
    List<LanguageCode> getLangCodes();
    boolean isLangCodeSupported(String code);
}
