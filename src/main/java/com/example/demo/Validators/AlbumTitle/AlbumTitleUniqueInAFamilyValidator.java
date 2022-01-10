package com.example.demo.Validators.AlbumTitle;

import com.example.demo.Album.Service.Album.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AlbumTitleUniqueInAFamilyValidator implements ConstraintValidator<AlbumTitleUniqueInAFamily, String> {
    @Autowired
    private AlbumService albumService;

    @Override
    public boolean isValid(String title, ConstraintValidatorContext constraintValidatorContext) {
//        return albumService.findByFamilyIdAndTitle()
//
//        return
        return false;
    }
}
