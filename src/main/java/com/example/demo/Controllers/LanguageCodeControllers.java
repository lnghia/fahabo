package com.example.demo.Controllers;

import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.LanguageCode.LanguageCodeService;
import com.example.demo.domain.LanguageCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "${URL_PREFIX}")
@Slf4j
public class LanguageCodeControllers {
    @Autowired
    private LanguageCodeService languageCodeService;

    @GetMapping("/lang_code")
    public ResponseEntity<Response> getLangCodes(){
        List<LanguageCode> langCodes = languageCodeService.getLangCodes();

        for(var item : langCodes){
            item.setId(item.getId().trim());
        }

        return ResponseEntity.ok(new Response(langCodes, new ArrayList<>()));
    }
}
