package com.example.demo.Firebase;

import com.example.demo.Helpers.Helper;
import com.example.demo.Notification.Helper.NotificationHelper;
import com.example.demo.Notification.Service.NotificationService;
import com.example.demo.UserFirebaseToken.Helper.UserFirebaseTokenHelper;
import com.example.demo.UserFirebaseToken.Service.UserFirebaseTokenService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FirebaseMessageHelper {
    @Autowired
    private UserFirebaseTokenService userFirebaseTokenService;

    @Autowired
    private NotificationHelper notificationHelper;

    private final String ICON_URL = "https://www.google.com/url?sa=i&url=https%3A%2F%2Ftoppng.com%2Ffacebook-bell-notification-icon-facebook-notification-icon-PNG-free-PNG-Images_125458&psig=AOvVaw3w3mEjgAsAIujeJ6KjDHTy&ust=1636452942072000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCLCeuLvEiPQCFQAAAAAdAAAAABAD";

    public void notifyDevices(List<String> tokens, String title, String body) {
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
                        if (responses.get(i).getException().getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED) ||
                                responses.get(i).getException().getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                            userFirebaseTokenService.deleteToken(tokens.get(i));
                        }
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

    public void notifyDevices(List<String> tokens, String title, String body, HashMap<String, String> data) {
        MulticastMessage message = MulticastMessage.builder()
                .putAllData(data)
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
                        if (responses.get(i).getException().getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED) ||
                                responses.get(i).getException().getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                            userFirebaseTokenService.deleteToken(tokens.get(i));
                        }
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

    public void notifyAllUsersInFamily(Family family, String title, String body, HashMap<String, String> data) {
        List<String> tokens = new ArrayList<>();
        Date now = Helper.getInstance().getNowAsTimeZone(family.getTimezone());

        List<User> users = family.getUsersInFamily().stream().map(userInFamily -> {
            notificationHelper.createNotification(
                    userInFamily.getUser(),
                    family,
                    notificationHelper.getTypeBasedOnNavigate(data.get("navigate")),
                    title,
                    body,
                    now,
                    data.get("navigate"),
                    data.get("id")
            );
            return userInFamily.getUser();
        }).collect(Collectors.toList());

        for (var user : users) {
            for (var token : user.getFirebaseTokenSet()) {
                if (!token.isDeleted()) tokens.add(token.getToken());
            }
        }
        sendNotifications(tokens, title, body, data);
    }

    public void notifyAllUsersInFamilyExceptUser(Family family, User user, String title, String body, HashMap<String, String> data) {
        List<String> tokens = new ArrayList<>();
        Date now = Helper.getInstance().getNowAsTimeZone(family.getTimezone());

        List<User> users = family.getUsersInFamily().stream().filter(userInFamily -> userInFamily.getUser().getId() != user.getId()).map(userInFamily -> {
            notificationHelper.createNotification(
                    userInFamily.getUser(),
                    family,
                    notificationHelper.getTypeBasedOnNavigate(data.get("navigate")),
                    title,
                    body,
                    now,
                    data.get("navigate"),
                    data.get("id")
            );
            return userInFamily.getUser();
        }).collect(Collectors.toList());

        for (var _user : users) {
            for (var token : _user.getFirebaseTokenSet()) {
                if (!token.isDeleted()) tokens.add(token.getToken());
            }
        }
        sendNotifications(tokens, title, body, data);
    }

    public void notifyAllDevicesOfUser(User user, Family family, String title, String body, HashMap<String, String> data) {
        List<String> tokens = new ArrayList<>();
        Date now = Helper.getInstance().getNowAsTimeZone(family.getTimezone());

        notificationHelper.createNotification(
                user,
                family,
                notificationHelper.getTypeBasedOnNavigate(data.get("navigate")),
                title,
                body,
                now,
                data.get("navigate"),
                data.get("id")
        );
        for (var userToken : user.getFirebaseTokenSet()) {
            if (!userToken.isDeleted()) tokens.add(userToken.getToken());
        }
        sendNotifications(tokens, title, body, data);
    }

    public void notifyUsers(List<User> users, Family family, String title, String body, HashMap<String, String> data) {
        List<String> tokens = new ArrayList<>();
        Date now = Helper.getInstance().getNowAsTimeZone(family.getTimezone());

        for (var user : users) {
            notificationHelper.createNotification(
                    user,
                    family,
                    notificationHelper.getTypeBasedOnNavigate(data.get("navigate")),
                    title,
                    body,
                    now,
                    data.get("navigate"),
                    data.get("id")
            );
            for (var userToken : user.getFirebaseTokenSet()) {
                if (!userToken.isDeleted()) tokens.add(userToken.getToken());
            }
        }
        sendNotifications(tokens, title, body, data);
    }

    public void sendNotifications(List<String> tokens, String title, String body) {
        new Thread(() -> {
            notifyDevices(tokens, title, body);
        }).start();
    }

    public void sendNotifications(List<String> tokens, String title, String body, HashMap<String, String> data) {
        new Thread(() -> {
            notifyDevices(tokens, title, body, data);
        }).start();
    }
}
