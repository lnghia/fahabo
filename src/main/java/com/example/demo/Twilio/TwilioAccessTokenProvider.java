package com.example.demo.Twilio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;

@Component
public class TwilioAccessTokenProvider {
    @Value("${TWILIO_ACCOUNT_SID}")
    private String TWILIO_ACCOUNT_SID;

    @Value("${TWILIO_API_KEY}")
    private String TWILIO_API_KEY;

    @Value("${TWILIO_API_SECRET}")
    private String TWILIO_API_SECRET;

    @Value("${TWILIO_TIME_TO_LIVE}")
    private int TWILIO_TIME_TO_LIVE;

    public String generateAccessToken(String identity, String room){
        VideoGrant grant = new VideoGrant();
        grant.setRoom(room);

        // Create access token
        AccessToken token = new AccessToken.Builder(
                TWILIO_ACCOUNT_SID,
                TWILIO_API_KEY,
                TWILIO_API_SECRET
        ).identity(identity).grant(grant).ttl(TWILIO_TIME_TO_LIVE * 60).build();

        return token.toJwt();
    }
}
