package com.example.demo.Controllers;

import Messages.ResponseMsg;
import com.example.demo.EmailSender.EmailSenderProvider;
import com.example.demo.Exceptions.OTPGenerationCoolDownHasNotMet;
import com.example.demo.Helpers.Helper;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.SecurityProvider.JwtTokenProvider;
import com.example.demo.SecurityProvider.OTPTokenProvider;
import com.example.demo.Service.UserService;
import com.example.demo.Stringee.StringeeHelper;
import com.example.demo.Validators.RequestBody.RequestBodyRequired;
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
        Map<String, Object> data = new HashMap<>();

        if (userService.authenticate(loginReqForm.getUsername(), loginReqForm.getPassword())) {
            User user = userService.getUserByUsername(loginReqForm.getUsername());
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            String access_token = tokenProvider.generateAccessToken(customUserDetails);
            String refresh_token = tokenProvider.generateRefreshToken(customUserDetails);

            if (user.getValidEmail()) {
                data = new HashMap<>() {{
                    put("access_token", access_token);
                    put("refresh_token", refresh_token);
                    put("isValidEmail", "true");
                    put("user", user.getJson());
                }};
            } else {
                data = new HashMap<>() {{
                    put("isValidEmail", "false");
                }};
            }

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("", new ArrayList<>(List.of(ResponseMsg.Authentication.SignIn.fail.toString()))));
    }

    @PostMapping("/aaa")
    public ResponseEntity<Response> lll(@Valid @RequestBody LoginReqForm loginReqForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReqForm.getUsername(),
                        loginReqForm.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String access_token = tokenProvider.generateAccessToken((CustomUserDetails) authentication.getPrincipal());
        String refresh_token = tokenProvider.generateRefreshToken((CustomUserDetails) authentication.getPrincipal());

        Map<String, String> data;

        if (((CustomUserDetails) authentication.getPrincipal()).getUser().getValidEmail()) {
            data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
                put("isValidEmail", "true");
                put("user", ((CustomUserDetails) authentication.getPrincipal()).getUser().getJson().toString());
            }};
        } else {
            data = new HashMap<>() {{
                put("isValidEmail", "false");
            }};
        }

        Response response = new Response(data, new ArrayList<>());

        return ResponseEntity.ok(response);

//        return ResponseEntity.ok(new Response("", new ArrayList<>()));

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
    public ResponseEntity<Response> refreshToken(@Valid @NotNull @RequestBody RefreshAccessTokenReqForm
                                                         refreshTokenReq) {
        if (refreshTokenReq.getRefreshToken() != null && tokenProvider.validateToken(refreshTokenReq.getRefreshToken())) {
            int userId = tokenProvider.getUserIdFromJWT(refreshTokenReq.getRefreshToken());
            User user = userService.getUserById(userId);
            CustomUserDetails userDetails = new CustomUserDetails(user);

            String access_token = tokenProvider.generateAccessToken(userDetails);
            String refresh_token = tokenProvider.generateRefreshToken(userDetails);

            Map<String, String> data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
                put("isValidEmail", user.getValidEmail().toString());
            }};

            Response response = new Response(data, new ArrayList<>());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.RefreshToken.fail.toString()))));
    }

    @PostMapping("/register_with_email")
    public ResponseEntity<Response> registerWithEmail(@Valid @RequestBody RegisterUserWithEmailReqForm requestBody) throws
            ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        User newuser = new User(requestBody.getName(),
                ((requestBody.getBirthday() != null) ? formatter.parse(requestBody.getBirthday()) : null),
                ((requestBody.getLanguageCode() != null) ? requestBody.getLanguageCode() : null),
                requestBody.getPassword());

        if (userService.getUserByEmail(requestBody.getEmail()) == null) {
            newuser.setEmail(requestBody.getEmail());
            newuser.setUsername(requestBody.getEmail());
        } else {
            return ResponseEntity.ok(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.SignUp.emailExists.toString()))));
        }

        try {
            Date now = new Date();
            String otp = otpTokenProvider.generateOTP(now, true);

            log.info("[OTP]: " + otp);
            newuser.setLastSentVerification(now);
            newuser.setOneTimePassword(otp);
            emailSender.sendOTPEmail(otp, requestBody.getEmail());
        } catch (OTPGenerationCoolDownHasNotMet ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.System.fail.toString()))));
        }

        userService.saveUser(newuser);

        return ResponseEntity.ok(new Response(newuser.getJson(), new ArrayList<>()));
    }

    @PostMapping("/register_with_phone")
    public ResponseEntity<Response> registerWithPhone(@Valid @RequestBody RegisterUserWithPhoneReqForm requestBody) throws
            ParseException {
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
    public ResponseEntity<Response> verify(@Valid @RequestBody VerificationOTPReqForm requestBody) {
        String otp = requestBody.getOtp();
        User user = userService.getUserByUsername(requestBody.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.ForgotPassword.accountNotExist.toString()))));
        }

        if (otp != null && otpTokenProvider.validateOTP(otp, user)) {
            user.setValidEmail(true);
            userService.updateUser(user);

            CustomUserDetails userDetails = new CustomUserDetails(user);

            String access_token = tokenProvider.generateAccessToken(userDetails);
            String refresh_token = tokenProvider.generateRefreshToken(userDetails);

            Map<String, Object> data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
                put("isValidEmail", user.getValidEmail().toString());
                put("user", user.getJson());
            }};

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.Verification.fail.toString()))));
    }

    //
    @PostMapping("/getOTP")
    public ResponseEntity<Response> sendOTP(@Valid @RequestBody GetOTPReqForm requestBody) {
        try {
            User user = userService.getUserByUsername(requestBody.getUsername());
            String otp = otpTokenProvider.generateOTP(user.getLastSentVerification(), false);
            Date now = new Date();

            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.ForgotPassword.accountNotExist.toString()))));
            }

            log.info("[OTP]: " + otp);
            user.setLastSentVerification(now);
            user.setOneTimePassword(otp);
            userService.saveUser(user);
            emailSender.sendOTPEmail(otp, user.getEmail());

            return ResponseEntity.ok(new Response("success", new ArrayList<>()));
        } catch (OTPGenerationCoolDownHasNotMet ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.FetchOTP.fail.toString()))));
        }
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<Response> forgotPassword(@Valid @RequestBody ForgotPasswordReqForm requestBody) {
        User user = userService.getUserByUsername(requestBody.getUsername());
        CustomUserDetails userDetails = new CustomUserDetails(user);

        String access_token = tokenProvider.generateAccessToken(userDetails);

        if (requestBody.getPassword().equals(requestBody.getRepeatPassword())) {
            user.setPassword(requestBody.getPassword());
            userService.saveUser(user);
            return ResponseEntity.ok(new Response("Changed password successfully.", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("Password and repeat password must match."))));
    }
}
