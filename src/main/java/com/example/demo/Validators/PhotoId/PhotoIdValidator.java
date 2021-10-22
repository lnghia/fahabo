package com.example.demo.Validators.PhotoId;

import com.example.demo.Service.Photo.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhotoIdValidator implements ConstraintValidator<PhotoIdExists, Integer> {
    @Autowired
    private PhotoService photoService;

    @Override
    public boolean isValid(Integer i, ConstraintValidatorContext constraintValidatorContext) {
        return !(i == null || photoService.getById(i) == null);
    }
}
