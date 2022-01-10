package com.example.demo.Album.Service.Photo;

import com.example.demo.Album.Entity.Photo;

public interface PhotoService {
    Photo savePhoto(Photo photo);
    Photo getByName(String name);
    Photo getById(int id);
    boolean checkIfPhotoExistById(int photoId);
}
