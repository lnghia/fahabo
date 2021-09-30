package com.example.demo.Service.LanguageCode;

import com.example.demo.Repo.LanguageCodeRepo;
import com.example.demo.domain.LanguageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class LanguageCodeServiceIml implements LanguageCodeService{
    @Autowired
    private LanguageCodeRepo languageCodeRepo;

    @Override
    public List<LanguageCode> getLangCodes() {
        return languageCodeRepo.findAll();
    }

    @Override
    public boolean isLangCodeSupported(String code) {
//        Optional<LanguageCode> languageCodeOption = languageCodeRepo.findById(code);
        return !languageCodeRepo.findById(code).isEmpty();
    }
}
