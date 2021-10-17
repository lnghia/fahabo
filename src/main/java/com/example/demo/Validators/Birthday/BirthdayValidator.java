package com.example.demo.Validators.Birthday;

import com.example.demo.Helpers.Helper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BirthdayValidator implements ConstraintValidator<ValidBirthday, String> {
    String regex = "^\\d{2}-\\d{2}-\\d{4}$";

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        if(date == null || date.isEmpty() || date.isBlank()) return true;

        Pattern pattern = Pattern.compile(regex);

        String[] tokens = date.split("-");

        int dd = Integer.parseInt(tokens[0]);
        int mm = Integer.parseInt(tokens[1]);
        int yyyy = Integer.parseInt(tokens[2]);

        return pattern.matcher(date).matches()
                && Helper.getInstance().isValidDate(dd, mm, yyyy);
    }
}
