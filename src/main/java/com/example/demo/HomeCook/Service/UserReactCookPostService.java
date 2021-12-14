package com.example.demo.HomeCook.Service;

import com.example.demo.HomeCook.Entity.UserReactCookPost;
import com.example.demo.HomeCook.Repo.UserReactCookPostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserReactCookPostService {
    @Autowired
    private UserReactCookPostRepo userReactCookPostRepo;

    public UserReactCookPost save(UserReactCookPost userReactCookPost) {
        return userReactCookPostRepo.save(userReactCookPost);
    }

    public UserReactCookPost findByUserAndPost(int userId, int postId) {
        return userReactCookPostRepo.findByUserAndPost(userId, postId);
    }

    public ArrayList<UserReactCookPost> findByPost(int postId) {
        return userReactCookPostRepo.findByPost(postId);
    }
}
