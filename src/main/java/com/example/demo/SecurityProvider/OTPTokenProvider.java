package com.example.demo.SecurityProvider;

import com.example.demo.Exceptions.OTPGenerationCoolDownHasNotMet;
import com.example.demo.Service.UserService;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
@Slf4j
public class OTPTokenProvider {
    @Value("${OTP_LENGTH}")
    private int OTP_LENGTH;

    @Value("${OTP_LIFESPAN}")
    private int OTP_LIFESPAN;

    @Autowired
    private JavaMailSender javaMailSender;

    public String generateOTP(Date lastSentVerification, boolean firstGet) throws OTPGenerationCoolDownHasNotMet {
        Date now = new Date();

        if ((now.getTime() - lastSentVerification.getTime()) < OTP_LIFESPAN && !firstGet)
            throw new OTPGenerationCoolDownHasNotMet("Attempt to get an OTP before 5 minutes.");

        StringBuilder builder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < OTP_LENGTH; ++i) {
            char ch = (char) (random.nextInt(10) + '0');
            builder.append(ch);
        }

        return builder.toString();
    }

    public boolean validateOTP(String otp, User user) {
        log.info(String.valueOf(OTP_LIFESPAN));
        Date now = new Date();
        return (otp.equals(user.getOneTimePassword()) && (now.getTime() - user.getLastSentVerification().getTime()) < (long) OTP_LIFESPAN);
    }
}
