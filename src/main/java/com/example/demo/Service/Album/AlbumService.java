package com.example.demo.Service.Album;

import com.example.demo.domain.Album;

public interface AlbumService {
    Album saveAlbum(Album album);
    Album findById(int id);
    Album findByFamilyIdAndTitle(int familyId, String title);
    boolean checkIfAlbumExistsInFamily(int albumId, int familyId);
}
