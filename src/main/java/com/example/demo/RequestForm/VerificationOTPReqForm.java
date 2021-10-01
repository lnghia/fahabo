package com.example.demo.RequestForm;

import com.example.demo.Validators.Email.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VerificationOTPReqForm {
    private String otp;

    @ValidEmail
    private String email;
}
