package com.example.demo.Service.Album;

import com.example.demo.Repo.AlbumRepo;
import com.example.demo.domain.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumServiceImpl implements AlbumService{
    @Autowired
    private AlbumRepo albumRepo;

    @Override
    public Album saveAlbum(Album album) {
        return albumRepo.save(album);
    }

    @Override
    public Album findById(int id) {
        return albumRepo.getById(id);
    }

    @Override
    public Album findByFamilyIdAndTitle(int familyId, String title) {
        return albumRepo.findByFamilyIdAndTitle(familyId, title);
    }

    @Override
    public boolean checkIfAlbumExistsInFamily(int albumId, int familyId) {
        int tmp = albumRepo.countByAlbumIdAndFamilyId(albumId, familyId);

        return (albumRepo.countByAlbumIdAndFamilyId(albumId, familyId) > 0);
    }
}
