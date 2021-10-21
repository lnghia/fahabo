package com.example.demo.Service.AlbumsPhotos;

import com.example.demo.Repo.AlbumsPhotosRepo;
import com.example.demo.domain.AlbumsPhotos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumsPhotosServiceImpl implements AlbumsPhotosService {
    @Autowired
    private AlbumsPhotosRepo albumsPhotosRepo;

    @Override
    public AlbumsPhotos saveAlbumsPhotos(AlbumsPhotos albumsPhotos) {
        return albumsPhotosRepo.save(albumsPhotos);
    }
}
