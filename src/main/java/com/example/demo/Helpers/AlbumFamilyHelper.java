package com.example.demo.Helpers;

import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.Family.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlbumFamilyHelper {
    @Autowired
    private FamilyService familyService;

    @Autowired
    private AlbumService albumService;

    public boolean isAlbumTitleUniqueInFamily(int familyId, String title){
        return albumService.findByFamilyIdAndTitle(familyId, title) == null;
    }

    public boolean doesAlbumExistInFamily(int albumId, int familyId){
        return albumService.checkIfAlbumExistsInFamily(albumId, familyId);
    }
}
