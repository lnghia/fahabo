package com.example.demo.RequestForm;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordReqForm {
    @NotNull
    private String password;

    @NotNull
    private String repeatPassword;
}
