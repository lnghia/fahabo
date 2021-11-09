package com.example.demo.Firebase;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FirebaseMessageHelper {
    private static FirebaseMessageHelper instance;

    private final String ICON_URL = "https://www.google.com/url?sa=i&url=https%3A%2F%2Ftoppng.com%2Ffacebook-bell-notification-icon-facebook-notification-icon-PNG-free-PNG-Images_125458&psig=AOvVaw3w3mEjgAsAIujeJ6KjDHTy&ust=1636452942072000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCLCeuLvEiPQCFQAAAAAdAAAAABAD";

    public static FirebaseMessageHelper getInstance(){
        if(instance == null) instance = new FirebaseMessageHelper();
        return instance;
    }

    public void notifyDevices(List<String> tokens, String title, String body){
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage(ICON_URL)
                        .build())
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        // The order of responses corresponds to the order of the registration tokens.
                        failedTokens.add(tokens.get(i));
                        log.error(responses.get(i).getException().getMessage());
                    }
                }

                log.info("List of tokens that caused failures: " + failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.error("Couldn't push notifications.", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendNotifications(List<String> tokens, String title, String body){
        new Thread(() -> {
            notifyDevices(tokens, title, body);
        }).start();
    }
}
