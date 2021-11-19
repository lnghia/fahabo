package com.example.demo.Communication.Chat.Controller;

import com.example.demo.Communication.Chat.RequestBody.NotifyNewMessageReqBody;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/chat")
@Slf4j
public class ChatController {
    @Autowired
    private FirebaseMessageHelper firebaseMessageHelper;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    public ResponseEntity<Response> notifyAboutNewMess(@Valid @RequestBody NotifyNewMessageReqBody reqBody){
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);
        Helper helper = Helper.getInstance();
        String langCode = helper.getLangCode(family);

        if(family.checkIfUserExist(user)){
            firebaseMessageHelper.notifyAllUsersInFamilyExceptUser(
                    family,
                    user,
                    helper.getMessageInLanguage("newMessageTitle", langCode),
                    String.format(helper.getMessageInLanguage("newMessageBody", langCode), family.getFamilyName()),
                    new HashMap<String, String>(){{
                        put("navigate", "CHAT");
                        put("id", Integer.toString(family.getId()));
                    }}
            );

            return ResponseEntity.ok(new Response(null, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                new HashMap<String, String>(){{ put("familyName", family.getFamilyName()); }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
