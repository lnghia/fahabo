package com.example.demo.RequestForm;

import com.example.demo.Validators.Password.PasswordRequired;
import com.example.demo.Validators.Password.ValidPassword;
import com.example.demo.Validators.Username.ValidUsername;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordReqForm {
    @ValidUsername
    private String username;

    @ValidPassword
    private String password;

    @PasswordRequired
    private String repeatPassword;
}
