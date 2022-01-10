package com.example.demo.Validators.AlbumId;

import com.example.demo.Album.Service.Album.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AlbumIdValidator implements ConstraintValidator<AlbumIdExist, Integer> {
    @Autowired
    private AlbumService albumService;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        return albumService.findById(id) != null;
    }
}
