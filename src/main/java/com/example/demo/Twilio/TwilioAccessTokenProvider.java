package com.example.demo.Twilio;

import com.twilio.rest.video.v1.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class TwilioAccessTokenProvider {
    @Value("${TWILIO_ACCOUNT_SID}")
    private String TWILIO_ACCOUNT_SID;

    @Value("${TWILIO_API_KEY}")
    private String TWILIO_API_KEY;

    @Value("${TWILIO_API_SECRET}")
    private String TWILIO_API_SECRET;

    @Value("${TWILIO_TIME_TO_LIVE}")
    private int TWILIO_TIME_TO_LIVE;

    public String generateAccessToken(String identity, String room) {
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

    public void endRoom(String roomName) {
        log.info("Complete room " + roomName + " ...");
        Room room = Room.updater(roomName, Room.RoomStatus.COMPLETED).update();
    }
}
