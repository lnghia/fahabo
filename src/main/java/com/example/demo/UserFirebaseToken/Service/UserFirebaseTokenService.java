package com.example.demo.UserFirebaseToken.Service;

import com.example.demo.UserFirebaseToken.Entity.UserFirebaseToken;
import com.example.demo.UserFirebaseToken.Repo.UserFirebaseTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFirebaseTokenService {
    @Autowired
    private UserFirebaseTokenRepo userFirebaseTokenRepo;

    public UserFirebaseToken saveUserFirebaseToken(UserFirebaseToken userFirebaseToken){
        return userFirebaseTokenRepo.save(userFirebaseToken);
    }

    public boolean doesUserContainToken(int userId, String token){
        return userFirebaseTokenRepo.searchTokenInUser(userId, token) != null;
    }
}
