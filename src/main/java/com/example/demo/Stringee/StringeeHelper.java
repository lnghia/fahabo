package com.example.demo.Stringee;

import com.example.demo.Helpers.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StringeeHelper {
//    private static StringeeHelper instance;
//
//    @Value("${STRINGEE_SEND_SMS_API}")
//    private String SEND_SMS_API;
//
//    public static StringeeHelper getInstance() {
//        if (instance == null) instance = new StringeeHelper();
//        return instance;
//    }
//
//    public void sendSMS(String content, List<String> recipients) throws Exception {
//        List<HashMap<Object, Object>> data = new ArrayList<>();
//        String stringeeJWT = StringeeAccessTokenProvider.getInstance().genAccessToken();
//
//        for (var recipient : recipients) {
//            data.add(new HashMap<Object, Object>() {{
//                put("from", "Stringee");
//                put("to", recipient);
//                put("text", content);
//            }});
//        }
//
//        HttpHelper.getInstance().makePost("https://api.stringee.com/v1/sms",
//                new HashMap<>() {{
//                    put("sms", data);
//                }},
//                new HashMap<>() {{
//                    put("X-STRINGEE-AUTH", stringeeJWT);
//                    put("Content-Type", "application/json");
//                    put("Accept", "application/json");
//                }});
//    }
//
//    public void sendOTPSMS(String otp, String recipient) {
//        Thread executor = new Thread(() -> {
//            try {
//                sendSMS("Welcome to fahabo, your verification code is: " + otp, List.of(recipient));
//            } catch (Exception e) {
//                log.error("Could not send OTP through sms");
//                e.printStackTrace();
//            }
//        });
//        executor.start();
//    }
}
