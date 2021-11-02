package com.example.demo.Service.PhotoInChore;

import com.example.demo.Repo.PhotoInChoreRepo;
import com.example.demo.domain.PhotoInChore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PhotoInChoreService {
    @Autowired
    private PhotoInChoreRepo photoInChoreRepo;

    public PhotoInChore savePhotoInChore(PhotoInChore photoInChore){
        return photoInChoreRepo.save(photoInChore);
    }

    public ArrayList<PhotoInChore> findAllByChoreAlbumId(int albumId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return photoInChoreRepo.findAllByChoreAlbumId(albumId, pageable);
    }

    public ArrayList<PhotoInChore> findAllByChoreAlbumId(int albumId){
        return photoInChoreRepo.findAllByChoreAlbumId(albumId);
    }

    public PhotoInChore getPhotoInChoreByAlbumIdAndPhotoId(int choreAlbumId, int photoId){
        return photoInChoreRepo.getPhotoInChoreByAlbumIdAndPhotoId(choreAlbumId, photoId);
    }

    public void deletePhotosInChoreAlbumByFamilyId(int familyId){
        photoInChoreRepo.deletePhotosINChoreAlbumByFamilyId(familyId);
    }

    public int countPhotosNumInChore(int choreAlbumId){
        return photoInChoreRepo.countPhotosNumInChore(choreAlbumId);
    }
}
