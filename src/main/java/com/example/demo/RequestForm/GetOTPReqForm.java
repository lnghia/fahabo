package com.example.demo.RequestForm;

import com.example.demo.Validators.Email.ValidEmail;
import com.example.demo.Validators.Password.PasswordRequired;
import com.example.demo.Validators.Username.ValidUsername;
import lombok.Data;

@Data
public class GetOTPReqForm {
    @ValidUsername
    private String username;
}
