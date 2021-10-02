package com.example.demo.RequestForm;

import com.example.demo.Validators.OTPRequired.OTPRequired;
import com.example.demo.Validators.Password.PasswordRequired;
import com.example.demo.Validators.Username.ValidUsername;
import lombok.Data;

@Data
public class VerificationOTPReqForm {
    @OTPRequired
    private String otp;

    @ValidUsername
    private String username;
}
