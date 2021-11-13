package com.example.demo.Stringee;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class StringeeAccessTokenProvider {
    @Value("${STRINGEE_SECRET_KEY}")
    private String STRINGEE_SECRET_KEY;

    @Value("${STRINGEE_KEY_SID}")
    private String STRINGEE_KEY_SID;

    @Value("${STRINGEE_ACCESS_TOKEN_LIFESPAN}")
    private int STRINGEE_ACCESS_TOKEN_LIFESPAN;

    public String genAccessToken(int userId) {
        try {
            Map<String, Object> headerClaims = new HashMap<String, Object>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = (long) (System.currentTimeMillis()) + STRINGEE_ACCESS_TOKEN_LIFESPAN * 1000;

            String token = Jwts.builder()
                    .setHeader(headerClaims)
                    .claim("jti", STRINGEE_KEY_SID + "-" + System.currentTimeMillis())
                    .claim("iss", STRINGEE_KEY_SID)
//                    .claim("rest_api", true)
                    .claim("userId", userId)
//                    .claim("exp", new Date(exp))
                    .setExpiration(new Date(exp))
                    .signWith(SignatureAlgorithm.HS256, STRINGEE_SECRET_KEY)
                    .compact();

            return token;
        } catch (Exception ex) {
            log.error("Can not generate stringee access token.");
            ex.printStackTrace();
        }

        return null;
    }
}
