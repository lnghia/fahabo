package com.example.demo.RequestForm;

import com.example.demo.Validators.Password.PasswordRequired;
import com.example.demo.Validators.Password.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordReqForm {
    @ValidPassword
    private String password;

    @PasswordRequired
    private String repeatPassword;
}
