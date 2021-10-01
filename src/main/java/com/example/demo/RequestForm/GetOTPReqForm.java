package com.example.demo.RequestForm;

import com.example.demo.Validators.Email.ValidEmail;
import lombok.Data;

@Data
public class GetOTPReqForm {
    @ValidEmail
    private String email;
}
