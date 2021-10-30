package com.example.demo.Service.AlbumsPhotos;

import com.example.demo.domain.AlbumsPhotos;

public interface AlbumsPhotosService {
    AlbumsPhotos saveAlbumsPhotos(AlbumsPhotos albumsPhotos);
    int getAlbumIdByPhotoId(int photoId);
    AlbumsPhotos getByPhotoId(int photoId);
    void deletePhotosAlbumsRelationByFamilyId(int familyId);
    void deletePhotosInFamilyAlbums(int familyId);
}
