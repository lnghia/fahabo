package com.example.demo.Album.Service.Photo;

import com.example.demo.Album.Repo.PhotoRepo;
import com.example.demo.Album.Entity.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private PhotoRepo photoRepo;

    @Override
    public Photo savePhoto(Photo photo) {
        return  photoRepo.save(photo);
    }

    @Override
    public Photo getByName(String name) {
        return photoRepo.getByName(name);
    }

    @Override
    public Photo getById(int id) {
        return photoRepo.getById(id);
    }

    @Override
    public boolean checkIfPhotoExistById(int photoId) {
        return photoRepo.checkIfPhotoExistById(photoId).isPresent();
    }
}
