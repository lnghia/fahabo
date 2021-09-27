package com.example.demo.Controllers;

import com.example.demo.EmailSender.EmailSenderProvider;
import com.example.demo.Exceptions.OTPGenerationCoolDownHasNotMet;
import com.example.demo.Helpers.Helper;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.SecurityProvider.JwtTokenProvider;
import com.example.demo.SecurityProvider.OTPTokenProvider;
import com.example.demo.Service.UserService;
import com.example.demo.Stringee.StringeeHelper;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(path = "${URL_PREFIX}")
@Slf4j
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OTPTokenProvider otpTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmailSenderProvider emailSender;

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginReqForm loginReqForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReqForm.getUsername(),
                        loginReqForm.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String access_token = tokenProvider.generateAccessToken((CustomUserDetails) authentication.getPrincipal());
        String refresh_token = tokenProvider.generateRefreshToken((CustomUserDetails) authentication.getPrincipal());

        Map<String, String> data = new HashMap<>() {{
            put("accessToken", access_token);
            put("refreshToken", refresh_token);
        }};

        Response response = new Response(data, new ArrayList<>());

        return ResponseEntity.ok(response);

//        if(userService.authenticate(loginReqForm.getUsername(), loginReqForm.getPassword())){
//            User user = userService.getUserByUsername(loginReqForm.getUsername());
//            CustomUserDetails customUserDetails = new CustomUserDetails(user);
//            String access_token = tokenProvider.generateAccessToken(customUserDetails);
//            String refresh_token = tokenProvider.generateRefreshToken(customUserDetails);
//
//            Map<String, String> data = new HashMap<>() {{
//                put("access_token", access_token);
//                put("refresh_token", refresh_token);
//            }};
//
//            Response response = new Response(data, new ArrayList<>());
//
//            return ResponseEntity.ok(response);
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("", new ArrayList<>(List.of("user credentials invalid."))));
    }

    @PostMapping("/token")
    public ResponseEntity<Response> refreshToken(@Valid @NotNull @RequestBody RefreshAccessTokenReqForm refreshTokenReq) {
        if (tokenProvider.validateToken(refreshTokenReq.getRefreshToken())) {
            int userId = tokenProvider.getUserIdFromJWT(refreshTokenReq.getRefreshToken());
            User user = userService.getUserById(userId);
            CustomUserDetails userDetails = new CustomUserDetails(user);

            String access_token = tokenProvider.generateAccessToken(userDetails);
            String refresh_token = tokenProvider.generateRefreshToken(userDetails);

            Map<String, String> data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
            }};

            Response response = new Response(data, new ArrayList<>());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(null, new ArrayList<>(List.of("refresh token invalid or might have expired."))));
    }

    @PostMapping("/register_with_email")
    public ResponseEntity<Response> registerWithEmail(@Valid @RequestBody RegisterUserWithEmailReqForm requestBody) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        User newuser = new User(requestBody.getName(), formatter.parse(requestBody.getBirthday()), requestBody.getLanguageCode(), requestBody.getPassword());
        Date now = new Date();

        if (userService.getUserByEmail(requestBody.getEmail()) == null) {
            newuser.setEmail(requestBody.getEmail());
            newuser.setUsername(requestBody.getEmail());
        } else {
            return ResponseEntity.ok(new Response(null, new ArrayList<>(List.of("Email already exists."))));
        }

        try {
            String otp = otpTokenProvider.generateOTP(now, true);

            log.info("[OTP]: " + otp);
            newuser.setLastSentVerification(now);
            newuser.setOneTimePassword(otp);
            emailSender.sendOTPEmail(otp, requestBody.getEmail());
        } catch (OTPGenerationCoolDownHasNotMet ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("Server error."))));
        }

        userService.saveUser(newuser);

        return ResponseEntity.ok(new Response(newuser.getJson(), new ArrayList<>()));
    }

    @PostMapping("/register_with_phone")
    public ResponseEntity<Response> registerWithPhone(@Valid @RequestBody RegisterUserWithPhoneReqForm requestBody) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        User newuser = new User(requestBody.getName(), formatter.parse(requestBody.getBirthday()), requestBody.getLanguageCode(), requestBody.getPassword());
        Date now = new Date();

        if (userService.getUserByEmail(requestBody.getPhoneNumber()) == null) {
            newuser.setEmail(requestBody.getPhoneNumber());
            newuser.setUsername(requestBody.getPhoneNumber());
        } else {
            return ResponseEntity.ok(new Response(null, new ArrayList<>(List.of("Phone number already exists."))));
        }

//        try {
//            String otp = otpTokenProvider.generateOTP(now, true);
//
//            log.info("[OTP]: " + otp);
//            newuser.setLastSentVerification(now);
//            newuser.setOneTimePassword(otp);
//            StringeeHelper.getInstance().sendOTPSMS(otp, requestBody.getPhoneNumber());
//        } catch (OTPGenerationCoolDownHasNotMet ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("Server error."))));
//        }

        userService.saveUser(newuser);

        return ResponseEntity.ok(new Response(newuser.getJson(), new ArrayList<>()));
    }

    @PostMapping("/verify")
    public ResponseEntity<Response> verify(@Valid @RequestBody VerificationOTPReqForm requestBody){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String otp = requestBody.getOtp();

        if(otpTokenProvider.validateOTP(otp, userDetails.getUser())){
            userDetails.getUser().setValidEmail(true);
            userService.saveUser(userDetails.getUser());
            return ResponseEntity.ok(new Response("Verify successfully.", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("Verify unsuccessfully."))));
    }
//
    @GetMapping("/getOTP")
    public ResponseEntity<Response> sendOTP(){
        try{
            User user = ((CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser();
            String otp = otpTokenProvider.generateOTP(user.getLastSentVerification(), false);
            Date now = new Date();

            log.info("[OTP]: " + otp);
            user.setLastSentVerification(now);
            user.setOneTimePassword(otp);
            userService.saveUser(user);
            emailSender.sendOTPEmail(otp, user.getEmail());

            return ResponseEntity.ok(new Response("success", new ArrayList<>()));
        }
        catch (OTPGenerationCoolDownHasNotMet ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("Try again after 5 minutes."))));
        }
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<Response> forgotPassword(@Valid @RequestBody ForgotPasswordReqForm requestBody){
        User user = ((CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser();

        if(requestBody.getPassword().equals(requestBody.getRepeatPassword())){
            user.setPassword(requestBody.getPassword());
            userService.saveUser(user);
            return ResponseEntity.ok(new Response("Changed password successfully.", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("Password and repeat password must match."))));
    }
}
