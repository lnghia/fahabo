package com.example.demo.UserFirebaseToken.Helper;

import com.example.demo.UserFirebaseToken.Entity.UserFirebaseToken;
import com.example.demo.UserFirebaseToken.Service.UserFirebaseTokenService;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFirebaseTokenHelper {
    @Autowired
    private UserFirebaseTokenService userFirebaseTokenService;

    public UserFirebaseToken createUserFirebaseToken(User user, String token){
        UserFirebaseToken tmp = new UserFirebaseToken(token, user);
        return userFirebaseTokenService.saveUserFirebaseToken(tmp);
    }

    public boolean doesUserContainToken(int userId, String token){
        return userFirebaseTokenService.doesUserContainToken(userId, token);
    }

    public UserFirebaseToken findUserFirebaseTokenByToken(int userId, String token) {
        return userFirebaseTokenService.findUserFirebaseTokenByToken(userId, token);
    }
}
