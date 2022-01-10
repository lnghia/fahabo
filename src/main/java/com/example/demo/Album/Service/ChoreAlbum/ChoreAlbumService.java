package com.example.demo.Album.Service.ChoreAlbum;

import com.example.demo.Album.Repo.ChoreAlbumRepo;
import com.example.demo.Album.Entity.ChoreAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChoreAlbumService {
    @Autowired
    private ChoreAlbumRepo choreAlbumRepo;

    public ChoreAlbum saveChoreAlbum(ChoreAlbum choreAlbum){
        return choreAlbumRepo.save(choreAlbum);
    }

    @Transactional
    public void deleteChoreAlbumByFamilyId(int familyId){
        choreAlbumRepo.deleteChoreAlbumByFamily(familyId);
    }
}
