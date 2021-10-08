package com.example.demo.Controllers;

import com.example.demo.RequestForm.LoginReqForm;
import com.example.demo.RequestForm.TempReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    private ResponseEntity<Response> getUsers(){
        List<User> users = userService.getUsers();
        List<Object> data = users.stream().map(User::getJson).collect(Collectors.toList());

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }

//    @GetMapping
//    private ResponseEntity<Response> saveUser(@RequestBody String name, @RequestBody String username, @RequestBody String password){
//        User newUser = new User(name, username, password);
//
//        return ResponseEntity.ok(new Response(userService.saveUser(newUser), ""));
//    }

    @GetMapping("/newuser")
    private ResponseEntity<Response> saveUser(){
//        User newUser = new User("t", "t", "12345");
        User user = new User();
        User user1 = new User();

        user.setPassword("123");
        user.setEmail("jkl@gmail.com");
        user.setLanguageCode("vi");
        user.setDeleted(false);

        user1.setDeleted(false);
        user1.setPassword("123");
        user.setPhoneNumber("000");
        user1.setLanguageCode("vi");

        userService.saveUser(user);

        return ResponseEntity.ok(new Response(userService.saveUser(user1), new ArrayList<>(List.of("user information invalid."))));
    }

    @PostMapping("/temp")
    private ResponseEntity<Response> temp(@Valid @RequestBody TempReqForm temp){
        return ResponseEntity.ok(new Response(temp, new ArrayList<>()));
    }
}
