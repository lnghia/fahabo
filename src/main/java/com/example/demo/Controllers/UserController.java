package com.example.demo.Controllers;

import com.example.demo.RequestForm.LoginReqForm;
import com.example.demo.RequestForm.TempReqForm;
import com.example.demo.RequestForm.UpdateProfileReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/users")
@Slf4j
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

    @PostMapping("/update_profile")
    private ResponseEntity<Response> updateProfile(@Valid @RequestBody UpdateProfileReqForm requestBody) throws ParseException {
        log.debug(requestBody.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if(requestBody.getEmail() != null){
            user.setEmail(requestBody.getEmail());
            user.setUsername(requestBody.getEmail());
        }
        if(requestBody.getBirthDay() != null){
            user.setBirthday(formatter.parse(requestBody.getBirthDay()));
        }
        if(requestBody.getPhoneNumber() != null){
            user.setPhoneNumber(requestBody.getPhoneNumber());
        }
        if(requestBody.getName() != null){
            user.setName(requestBody.getName());
        }

        userService.updateUser(user);

        return ResponseEntity.ok(new Response("Updated profile successfully.", new ArrayList<>()));
    }

    @GetMapping("/preview_images")
    public ResponseEntity<Response> getPreviewImages(){

        HashMap<String, Object> i1 = new HashMap<>(){{
            put("uri", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLPjPYsrtUfxX0XgfwkMgiS-blYlIe4tyIRg&usqp=CAU");
        }};
        HashMap<String, Object> i2 = new HashMap<>(){{
            put("uri", "https://truyenhinh.fpt.vn/wp-content/uploads/575.jpg");
        }};
        HashMap<String, Object> i3 = new HashMap<>(){{
            put("uri", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9XG3cRUCoISQMWH8abr4aFRjkyvX6jecILmMwXbMQUZmBWdDYSnZecaZZg3STAcW2rwc&usqp=CAU");
        }};
        HashMap<String, Object> i4 = new HashMap<>(){{
            put("uri", "https://songmoi.vn/public/upload_editor/posts/images/anh-dong-vat-hoang-da-2019-1.jpg");
        }};
        HashMap<String, Object> i5 = new HashMap<>(){{
            put("uri", "https://lh3.googleusercontent.com/proxy/SJkOfQVHbu5fOYRPBwXff72DXcGbajxUwaFLfFXHBMm2rX5nFf5BXLZLCAzFIFrGDeGYzq9dx5pCM9eLCingXNzQGJtiyNcCiDHrCKvReeZoVPpZPmDpsraBpuii18K-YW4Z");
        }};
        HashMap<String, Object> i6 = new HashMap<>(){{
            put("uri", "https://lh3.googleusercontent.com/proxy/rhOnJmrhm582ZYEJhcv002SNScrd1sDDGFfUl9Y_7GPnPcoY4DaxYpkdhMi0GTDVFwjGj1eMocch0mYI7wYWjVkYxiPx70rPH7pbdk2PXNVgTkB2GbBlzpHlJq6MfS9RvbvDUq1zps8");
        }};
        HashMap<String, Object> i7 = new HashMap<>(){{
            put("uri", "https://khoahocphattrien.vn/Images/Uploaded/Share/2016/09/19/122.jpg");
        }};
        HashMap<String, Object> i8 = new HashMap<>(){{
            put("uri", "https://www.nepalitimes.com/wp-content/uploads/2019/08/page-12.jpg");
        }};
        HashMap<String, Object> i9 = new HashMap<>(){{
            put("uri", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTomc9c6mwRxduhj3ryOVXVoBNIO2AwGlZyHA&usqp=CAU");
        }};

        return ResponseEntity.ok(new Response(new ArrayList<>(List.of(i1, i2, i3, i4, i5, i6, i7, i8, i9)), new ArrayList<>()));
    }

    @GetMapping("/avatar")
    public ResponseEntity<Response> getAvatar(){
        HashMap<String, Object> data = new HashMap<>(){{
            put("uri", "https://media.kidadl.com/animal_planet_coloring_pages_lg_25c609767f.png");
        }};
        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }
}
