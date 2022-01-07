package com.example.demo.Service.AlbumsPhotos;

import com.example.demo.Repo.AlbumsPhotosRepo;
import com.example.demo.domain.AlbumsPhotos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

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
    public ArrayList<Integer> getAlbumIdByPhotoId(int photoId) {
        return albumsPhotosRepo.getAlbumIdByPhotoId(photoId);
    }

    @Override
    public ArrayList<AlbumsPhotos> getByPhotoId(int photoId) {
        return albumsPhotosRepo.getByPhotoId(photoId);
    }

    @Override
    @Transactional
    public void deletePhotosAlbumsRelationByFamilyId(int familyId) {
        albumsPhotosRepo.deletePhotosAlbumsRelationByFamilyId(familyId);
    }

    @Override
    @Transactional
    public void deletePhotosInFamilyAlbums(int familyId) {
        albumsPhotosRepo.deletePhotosInFamilyAlbums(familyId);
    }
}
