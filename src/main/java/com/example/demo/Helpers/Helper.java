package com.example.demo.Helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Helper {
    private static Helper instance;

    public static Helper getInstance() {
        if (instance == null) instance = new Helper();
        return instance;
    }

    public HashSet<String> getUnauthenticatedEndpoints() {
        return new HashSet<>(
                List.of("/api/v1/login",
                        "/api/v1/users",
                        "/api/v1/token",
                        "/api/v1/register_with_email",
                        "/api/v1/register_with_phone",
                        "/api/v1/register_with_phone"));
    }

    public String mapToJsonString(HashMap<Object, Object> map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }
}
