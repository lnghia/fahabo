package com.example.demo.Service.AlbumsPhotos;

import com.example.demo.Repo.AlbumsPhotosRepo;
import com.example.demo.domain.AlbumsPhotos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AlbumsPhotosServiceImpl implements AlbumsPhotosService {
    @Autowired
    private AlbumsPhotosRepo albumsPhotosRepo;

    @Override
    public AlbumsPhotos saveAlbumsPhotos(AlbumsPhotos albumsPhotos) {
        return albumsPhotosRepo.save(albumsPhotos);
    }

    @Override
    public int getAlbumIdByPhotoId(int photoId) {
        return albumsPhotosRepo.getAlbumIdByPhotoId(photoId);
    }

    @Override
    public AlbumsPhotos getByPhotoId(int photoId) {
        return albumsPhotosRepo.getByPhotoId(photoId);
    }
}
