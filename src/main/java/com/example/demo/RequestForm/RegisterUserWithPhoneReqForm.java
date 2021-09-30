package com.example.demo.RequestForm;

import com.example.demo.Validators.Name.ValidName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RegisterUserWithPhoneReqForm {
    @ValidName
//    @Pattern(regexp = "\\p{L}")
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String birthday;

    @NotNull
    private String languageCode;

    @NotNull
    private String password;
}
