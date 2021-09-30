package com.example.demo.RequestForm;

import com.example.demo.Validators.Birthday.ValidBirthday;
import com.example.demo.Validators.Email.ValidEmail;
import com.example.demo.Validators.LangCode.ValidLangCode;
import com.example.demo.Validators.Name.ValidName;
import com.example.demo.Validators.Password.ValidPassword;
import lombok.Data;

import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
public class RegisterUserWithEmailReqForm {
    @ValidName
//    @Pattern(regexp = "\\p{L}")
    private String name;

    @ValidEmail
    private String email;

    @ValidBirthday
    private String birthday;

    @ValidLangCode
    private String languageCode;

    @ValidPassword
    private String password;
}
