package com.example.demo.Album.Service.Album;

import com.example.demo.Album.Entity.Album;

import java.util.List;

public interface AlbumService {
    Album saveAlbum(Album album);
    Album findById(int id);
    Album findByFamilyIdAndTitle(int familyId, String title);
    boolean checkIfAlbumExistsInFamily(int albumId, int familyId);
    int countAllByAlbum(int albumId);
    int getFamilyIdByAlbumId(int albumId);
    List<Album> findAllByFamilyIdWithPagination(int familyId, int defaultAlbumId, String searchText, int page, int size);
    List<Integer> findAllPhotoIdsByAlbumIdWithPagination(int albumId, int page, int size);
    List<Integer> get9LatestPhotosFromAlbum(int albumId);
    void deleteAlbumsInFamily(int familyId);
    String getMostRecentImageUriInAlbum(int albumId);
}
