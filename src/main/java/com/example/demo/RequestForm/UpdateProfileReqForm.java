package com.example.demo.RequestForm;

import com.example.demo.Validators.Birthday.ValidBirthday;
import com.example.demo.Validators.Email.ValidEmailFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileReqForm {
    private String name;

    @ValidBirthday
    private String birthDay;

    private String phoneNumber;

    @ValidEmailFormat
    private String email;
}
