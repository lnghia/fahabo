package com.example.demo.RequestForm;

import lombok.Data;

import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
public class RegisterUserWithEmailReqForm {
    @NotNull
//    @Pattern(regexp = "\\p{L}")
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String birthday;

    @NotNull
    private int languageCode;

    @NotNull
    private String password;
}
