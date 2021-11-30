package com.example.demo.Notification.Controller;

import com.example.demo.Helpers.Helper;
import com.example.demo.Notification.Entity.Notification;
import com.example.demo.Notification.RequestBody.ClearChatNotiReqBody;
import com.example.demo.Notification.RequestBody.ClickNotificationReqBody;
import com.example.demo.Notification.RequestBody.GetNotificationsReqBody;
import com.example.demo.Notification.Service.NotificationService;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/notifications")
@Slf4j
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInFamilyService userInFamilyService;

    @PostMapping
    public ResponseEntity<Response> getNotifications(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "5") Integer size) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
//        Family family = familyService.findById(reqBody.familyId);

//        if (family.checkIfUserExist(user)) {
        ArrayList<Notification> notifications = notificationService.getNotificationsByUserId(user.getId(), page, size);

//        Helper.getInstance().reverseArrayList(notifications);

        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        for (var notification : notifications) {
            data.add(notification.getJson());
        }

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
//        }

//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/count_notification")
    public ResponseEntity<Response> getNotiCount(@Valid @RequestBody GetNotificationsReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserInFamily userInFamily = userInFamilyService.findByUserIdAndFamilyId(user.getId(), reqBody.familyId);

        if (familyService.findById(reqBody.familyId).checkIfUserExist(user)) {
            return ResponseEntity.ok(new Response(
                    new HashMap<String, Object>() {{
                        put("countChat", userInFamily.getCountChat());
                        put("countNoti", user.getCountNoti());
                    }},
                    new ArrayList<>()
            ));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/clear_noti")
    public ResponseEntity<Response> getNotiCount() {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        user.setCountNoti(0);
        userService.updateUser(user);

        return ResponseEntity.ok(new Response("Notification count has been set to 0", new ArrayList<>()));
    }

    @PostMapping("/clear_chat_noti")
    public ResponseEntity<Response> getNotiCount(@Valid @RequestBody ClearChatNotiReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);

        if (family.checkIfUserExist(user)) {
            UserInFamily userInFamily = userInFamilyService.findByUserIdAndFamilyId(user.getId(), reqBody.familyId);
            userInFamily.setCountChat(0);
            userInFamilyService.saveUserInFamily(userInFamily);

            return ResponseEntity.ok(new Response("Chat notification count has been set to 0", new ArrayList<>()));
        }

        return ResponseEntity.ok(new Response("Notification count has been set to 0", new ArrayList<>()));
    }

    @PostMapping("/click")
    public ResponseEntity<Response> clickNotification(@RequestBody ClickNotificationReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Notification notification = notificationService.getByUserIdAndId(user.getId(), reqBody.id);

        if (notification != null) {
            notification.setClicked(true);
            notificationService.saveNotification(notification);

            return ResponseEntity.ok(new Response("Notification has been clicked", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(""))));
    }
}
