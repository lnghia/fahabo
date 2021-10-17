package com.example.demo.RequestForm;

import com.example.demo.Validators.Password.NewPasswordRequired;
import com.example.demo.Validators.Password.PasswordRequired;
import lombok.Data;

@Data
public class ChangePasswordReqForm {
    @PasswordRequired
    private String currentPassword;

    @NewPasswordRequired
    private String newPassword;

    private String confirmNewPassword;
}
