package com.example.demo.RequestForm;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VerificationOTPReqForm {

    private String otp;
}
