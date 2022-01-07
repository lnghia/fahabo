package com.example.demo.Service.AlbumsPhotos;

import com.example.demo.domain.AlbumsPhotos;

import java.util.ArrayList;

public interface AlbumsPhotosService {
    AlbumsPhotos saveAlbumsPhotos(AlbumsPhotos albumsPhotos);
    ArrayList<Integer> getAlbumIdByPhotoId(int photoId);
    ArrayList<AlbumsPhotos> getByPhotoId(int photoId);
    void deletePhotosAlbumsRelationByFamilyId(int familyId);
    void deletePhotosInFamilyAlbums(int familyId);
}
