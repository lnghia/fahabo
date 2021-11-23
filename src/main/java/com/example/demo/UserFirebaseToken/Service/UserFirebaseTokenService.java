package com.example.demo.UserFirebaseToken.Service;

import com.example.demo.UserFirebaseToken.Entity.UserFirebaseToken;
import com.example.demo.UserFirebaseToken.Repo.UserFirebaseTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public UserFirebaseToken findUserFirebaseTokenByToken(int userId, String token){
        return userFirebaseTokenRepo.findTokenInUser(userId, token);
    }

    @Transactional
    public void deleteToken(String token){
        userFirebaseTokenRepo.disableToken(token);
    }

    public List<UserFirebaseToken> findAllUserFirebaseTokenByUser(int userId){
        return userFirebaseTokenRepo.findAllUserFirebaseTokenByUser(userId);
    }
}
