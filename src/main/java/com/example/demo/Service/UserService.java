package com.example.demo.Service;

import com.example.demo.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User updateUser(User user);
    User getUserByEmail(String email);
    User getUserByPhoneNumber(String phoneNumber);
    List<User> getUsers();
    User getUserByUsername(String username);
    User getUserById(int id);
    boolean authenticate(String username, String password);
    String generateImgUploadId(User user);
}
