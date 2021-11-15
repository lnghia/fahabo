package com.example.demo.Controllers;

import Messages.ResponseMsg;
import com.example.demo.EmailSender.EmailSenderProvider;
import com.example.demo.Exceptions.OTPGenerationCoolDownHasNotMet;
import com.example.demo.Helpers.Helper;
import com.example.demo.Helpers.UserHelper;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.SecurityProvider.JwtTokenProvider;
import com.example.demo.SecurityProvider.OTPTokenProvider;
import com.example.demo.Service.SocialAccountType.SocialAccountTypeService;
import com.example.demo.Service.UserService;
import com.example.demo.Stringee.StringeeAccessTokenProvider;
import com.example.demo.Stringee.StringeeHelper;
import com.example.demo.Twilio.TwilioAccessTokenProvider;
import com.example.demo.UserFirebaseToken.Entity.UserFirebaseToken;
import com.example.demo.UserFirebaseToken.Helper.UserFirebaseTokenHelper;
import com.example.demo.UserFirebaseToken.Service.UserFirebaseTokenService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserHelper userHelper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OTPTokenProvider otpTokenProvider;

    @Autowired
    private SocialAccountTypeService socialAccountTypeService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSenderProvider emailSender;

    @Autowired
    private UserFirebaseTokenHelper userFirebaseTokenHelper;

    @Autowired
    private UserFirebaseTokenService userFirebaseTokenService;

    @Autowired
    private StringeeAccessTokenProvider stringeeAccessTokenProvider;

    @Autowired
    private TwilioAccessTokenProvider twilioAccessTokenProvider;

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
                    put("accessToken", access_token);
                    put("refreshToken", refresh_token);
                    put("isValidEmail", true);
                    put("user", userHelper.UserToJson(user));
                }};
            } else {
                data = new HashMap<>() {{
                    put("isValidEmail", false);
                }};
            }

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("", new ArrayList<>(List.of(ResponseMsg.Authentication.SignIn.fail.toString()))));
    }

    @PostMapping("/add_user_firebase_token")
    public ResponseEntity<Response> addUserFirebaseToken(@RequestBody AddUserFirebaseTokenReqForm reqForm) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        String firebaseToken = reqForm.firebaseToken;

        if (!userFirebaseTokenHelper.doesUserContainToken(user.getId(), firebaseToken)) {
            UserFirebaseToken userFirebaseToken = userFirebaseTokenHelper.createUserFirebaseToken(user, firebaseToken);
            user.getFirebaseTokenSet().add(userFirebaseToken);
        }

        return ResponseEntity.ok(new Response());
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

        Map<String, Object> data;

        if (((CustomUserDetails) authentication.getPrincipal()).getUser().getValidEmail()) {
            data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
                put("isValidEmail", true);
                put("user", userHelper.UserToJson(((CustomUserDetails) authentication.getPrincipal()).getUser()).toString());
            }};
        } else {
            data = new HashMap<>() {{
                put("isValidEmail", false);
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

            Map<String, Object> data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
                put("isValidEmail", user.getValidEmail());
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

        newuser.setSocialAccountType(socialAccountTypeService.getById(requestBody.getAuthType()));

        if (userService.getUserByEmail(requestBody.getEmail()) == null) {
            newuser.setEmail(requestBody.getEmail());
            newuser.setUsername(requestBody.getEmail());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.SignUp.emailExists.toString()))));
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

        newuser.setAvatar(userHelper.DEFAULT_AVATAR);
        userService.saveUser(newuser);

        return ResponseEntity.ok(new Response(userHelper.UserToJson(newuser), new ArrayList<>()));
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

        return ResponseEntity.ok(new Response(userHelper.UserToJson(newuser), new ArrayList<>()));
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

//            if(!userFirebaseTokenHelper.doesUserContainToken(user.getId(), firebaseToken)){
//                UserFirebaseToken userFirebaseToken = userFirebaseTokenHelper.createUserFirebaseToken(user, firebaseToken);
//                user.getFirebaseTokenSet().add(userFirebaseToken);
//            }

            Map<String, Object> data = new HashMap<>() {{
                put("accessToken", access_token);
                put("refreshToken", refresh_token);
                put("isValidEmail", user.getValidEmail());
                put("user", userHelper.UserToJson(user));
            }};

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.Verification.fail.toString()))));
    }

    //
    @PostMapping("/getOTP")
    public ResponseEntity<Response> sendOTP(@Valid @RequestBody GetOTPReqForm requestBody) {
        User user = userService.getUserByUsername(requestBody.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.ForgotPassword.accountNotExist.toString()))));
        }
        if (user.getValidEmail()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("verification.hasBeenVerified"))));
        }

        try {

            String otp = otpTokenProvider.generateOTP(user.getLastSentVerification(), false);
            Date now = new Date();

            log.info("[OTP]: " + otp);
            user.setLastSentVerification(now);
            user.setOneTimePassword(otp);
            userService.updateUser(user);
            emailSender.sendOTPEmail(otp, user.getEmail());

            return ResponseEntity.ok(new Response("success", new ArrayList<>()));
        } catch (OTPGenerationCoolDownHasNotMet ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.FetchOTP.fail.toString()))));
        }
    }

    @PostMapping("/get_reset_password_otp")
    public ResponseEntity<Response> getResetPwOTP(@Valid @RequestBody GetOTPReqForm requestBody) {
        User user = userService.getUserByUsername(requestBody.getUsername());

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.ForgotPassword.accountNotExist.toString()))));

        if (!user.getSocialAccountType().equals(socialAccountTypeService.getBySocialName("Manual"))) {
            HashMap<String, Object> data = new HashMap<>() {{
                put("authType", user.getSocialAccountType().getId());
                put("resetPasswordLink", user.getSocialAccountType().getChangePasswordUrl());
            }};

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        try {
            String otp = otpTokenProvider.generateResetPwOTP(user.getResetPasswordOTPIssuedAt());
            Date now = new Date();

            log.info("[OTP]: " + otp);
            user.setResetPasswordOTPIssuedAt(now);
            user.setResetPasswordOTP(otp);
            userService.updateUser(user);
            emailSender.sendResetPwOTPEmail(otp, user.getEmail());

            return ResponseEntity.ok(new Response("success", new ArrayList<>()));

        } catch (OTPGenerationCoolDownHasNotMet e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.FetchOTP.fail.toString()))));
        }
    }

    @PostMapping("/verify_reset_password")
    public ResponseEntity<Response> verifyResetPassword(@Valid @RequestBody VerificationOTPReqForm requestBody) {
        String otp = requestBody.getOtp();
        User user = userService.getUserByUsername(requestBody.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.ForgotPassword.accountNotExist.toString()))));
        }

        if (otp != null && otpTokenProvider.validateResetPasswordOTP(otp, user)) {
            return ResponseEntity.ok(new Response("success", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.Verification.fail.toString()))));
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Response> resetPassword(@Valid @RequestBody ForgotPasswordReqForm requestBody) {
        User user = userService.getUserByUsername(requestBody.getUsername());
        String otp = requestBody.getOtp();

        if (!otpTokenProvider.validateResetPasswordOTP(otp, user))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.Verification.fail.toString()))));

        if (requestBody.getPassword().equals(requestBody.getRepeatPassword())) {
            user.setPassword(requestBody.getPassword());
            user.setResetPasswordOTP("");
            userService.saveUser(user);

            emailSender.sendPasswordHasChangedEmail(user.getEmail());

            return ResponseEntity.ok(new Response("Your password has been changed successfully!", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("Password and repeat password must match."))));
    }

    @PostMapping("/change_password")
    public ResponseEntity<Response> changePassword(@Valid @RequestBody ChangePasswordReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (!passwordEncoder.matches(requestBody.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(ResponseMsg.Authentication.SignIn.fail.toString()))));
        }

        if (requestBody.getNewPassword().equals(requestBody.getConfirmNewPassword())) {
            user.setPassword(requestBody.getNewPassword());
            userService.saveUser(user);

            emailSender.sendPasswordHasChangedEmail(user.getEmail());

            return ResponseEntity.ok(new Response("Your password has been changed successfully!", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.confirmNewPasswordMustMatch"))));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestBody LogoutReqForm reqForm) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (userFirebaseTokenHelper.doesUserContainToken(user.getId(), reqForm.firebaseToken)) {
            UserFirebaseToken userFirebaseToken = userFirebaseTokenHelper.findUserFirebaseTokenByToken(user.getId(), reqForm.firebaseToken);
            userFirebaseToken.setDeleted(true);
            userFirebaseTokenService.saveUserFirebaseToken(userFirebaseToken);
        }

        return ResponseEntity.ok(new Response(null, new ArrayList<>()));
    }

    @PostMapping("/communication_access_token")
    public ResponseEntity<Response> getTwilioAccessToken(@RequestBody GetTwilioAccessTokenReqForm reqForm) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        String token = new StringBuilder(
                Integer.toString(user.getId()) + '_' + Integer.toString(reqForm.familyId) + '_' + Long.toString(new Date().getTime())
        ).toString();

        if(reqForm.roomCallId != null && !reqForm.roomCallId.isEmpty() && !reqForm.roomCallId.isBlank()){
            token = reqForm.roomCallId;
        }

        String accessToken = twilioAccessTokenProvider.generateAccessToken(
                Integer.toString(user.getId()),
                token
        );

        String finalToken = token;
        return ResponseEntity.ok(new Response(new HashMap<String, String>() {{
            put("twilioAccessToken", accessToken);
            put("roomCallId", finalToken);
        }}, new ArrayList<>()));
    }
}
