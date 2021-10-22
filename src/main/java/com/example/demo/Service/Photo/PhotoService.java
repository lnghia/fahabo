package com.example.demo.Service.Photo;

import com.example.demo.domain.Photo;

import java.util.Optional;

public interface PhotoService {
    Photo savePhoto(Photo photo);
    Photo getByName(String name);
    Photo getById(int id);
    boolean checkIfPhotoExistById(int photoId);
}
