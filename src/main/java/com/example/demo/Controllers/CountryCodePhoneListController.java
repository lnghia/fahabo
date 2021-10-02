package com.example.demo.Controllers;

import com.example.demo.Helpers.Helper;
import com.example.demo.ResponseFormat.Response;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping(path = "${URL_PREFIX}")
@Slf4j
public class CountryCodePhoneListController {

    @GetMapping("/country_code_list")
    public ResponseEntity<Response> getCountryCodes(){
        Set<String> set = PhoneNumberUtil.getInstance().getSupportedRegions();

        String[] arr = set.toArray(new String[set.size()]);
        HashMap<String, String> data = new HashMap<>();

        for (int i = 0; i < arr.length; i++) {
            Locale locale = new Locale("en", arr[i]);
            data.put(locale.getDisplayCountry(), Integer.toString(PhoneNumberUtil.getInstance().getCountryCodeForRegion(arr[i])));
//            Log.d(TAG, "lib country:" + arr[i] + "  "+ locale.getDisplayCountry());
        }

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }
}
