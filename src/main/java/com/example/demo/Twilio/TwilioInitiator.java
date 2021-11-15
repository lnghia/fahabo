package com.example.demo.Twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Slf4j
public class TwilioInitiator {
    @Value("${TWILIO_ACCOUNT_SID}")
    private String TWILIO_ACCOUNT_SID;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String TWILIO_AUTH_TOKEN;

    @Bean
    public void initiateTwilio(){
        log.info("Initiate Twilio ...");
        if(TWILIO_ACCOUNT_SID == null || TWILIO_ACCOUNT_SID.isEmpty() || TWILIO_AUTH_TOKEN == null || TWILIO_AUTH_TOKEN.isEmpty()){
            log.info("Could not initiate Twilio, TWILIO_ACCOUNT_SID and TWILIO_AUTH_TOKEN are required!");
        }
        else{
            Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
            log.info("Initiated Twilio successfully!");
        }
    }
}
