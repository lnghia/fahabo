package com.example.demo.RequestForm;

import com.example.demo.Validators.Birthday.ValidBirthday;
import com.example.demo.Validators.Email.ValidEmailFormat;
import com.example.demo.Validators.LangCode.ValidLangCode;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileReqForm {
    private String name;

    @ValidBirthday
    private String birthday;

    private String phoneNumber;

    @ValidEmailFormat
    private String email;

    @ValidLangCode
    private String languageCode;
}
