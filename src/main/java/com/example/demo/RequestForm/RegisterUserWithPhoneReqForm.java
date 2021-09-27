package com.example.demo.RequestForm;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RegisterUserWithPhoneReqForm {
    @NotNull
//    @Pattern(regexp = "\\p{L}")
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String birthday;

    @NotNull
    private int languageCode;

    @NotNull
    private String password;
}
