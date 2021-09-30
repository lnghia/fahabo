package com.example.demo.RequestForm;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.example.demo.Validators.Password.PasswordRequired;
import com.example.demo.Validators.Username.ValidUsername;
import lombok.Data;

@Data
public class LoginReqForm {
    @ValidUsername
    private String username;

    @PasswordRequired
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
