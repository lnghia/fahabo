package com.example.demo.Service.ChoreAlbum;

import com.example.demo.Repo.ChoreAlbumRepo;
import com.example.demo.domain.ChoreAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChoreAlbumService {
    @Autowired
    private ChoreAlbumRepo choreAlbumRepo;

    public ChoreAlbum saveChoreAlbum(ChoreAlbum choreAlbum){
        return choreAlbumRepo.save(choreAlbum);
    }
}
