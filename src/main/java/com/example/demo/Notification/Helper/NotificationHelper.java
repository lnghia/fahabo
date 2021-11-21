package com.example.demo.Notification.Helper;

import com.example.demo.Notification.Entity.Notification;
import com.example.demo.Notification.Service.NotificationService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class NotificationHelper {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInFamilyService userInFamilyService;

    public Notification createNotification(User user,
                                           Family family,
                                           String type,
                                           String title,
                                           String description,
                                           Date createdAt,
                                           String navigate,
                                           String subId) {
        Notification notification = new Notification(type, title, description, createdAt, user, family, navigate, subId);

        if (type.equals("CHAT")) {
            UserInFamily userInFamily = userInFamilyService.findByUserIdAndFamilyId(user.getId(), family.getId());
            userInFamily.setCountChat(userInFamily.getCountChat() + 1);
            userInFamilyService.saveUserInFamily(userInFamily);
        } else {
            user.setCountNoti(user.getCountNoti() + 1);
            userService.saveUser(user);
        }

        return notificationService.saveNotification(notification);
    }

    public String getTypeBasedOnNavigate(String navigate) {
        if (navigate.contains("EVENT")) {
            return "EVENT";
        } else if (navigate.contains("CHORE")) {
            return "CHORE";
        } else if (navigate.contains("CALL")) {
            return "CALL";
        } else if (navigate.contains("CHAT")) {
            return "CHAT";
        }

        return "";
    }
}
