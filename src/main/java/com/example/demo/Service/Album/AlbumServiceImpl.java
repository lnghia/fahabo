package com.example.demo.Service.Album;

import com.example.demo.Repo.AlbumRepo;
import com.example.demo.Repo.AlbumsPhotosRepo;
import com.example.demo.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.domain.Album;
import com.example.demo.domain.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
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

    @Override
    public int countAllByAlbum(int albumId) {
        return albumRepo.countAllByAlbum(albumId);
    }

    @Override
    public int getFamilyIdByAlbumId(int albumId) {
        return albumRepo.getFamilyIdByAlbumId(albumId);
    }

    @Override
    public List<Album> findAllByFamilyIdWithPagination(int familyId, int defaultAlbumId, String searchText, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return albumRepo.findAllByFamilyIdWithPagination(familyId, defaultAlbumId, searchText, pageable);
    }

    @Override
    public List<Integer> findAllPhotoIdsByAlbumIdWithPagination(int albumId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return albumRepo.getPhotoIdsByAlbumIdWithPagination(albumId, pageable);
    }

    @Override
    public List<Integer> get9LatestPhotosFromAlbum(int albumId) {
        return albumRepo.get9LatestPhotosFromAlbum(albumId);
    }

    @Override
    @Transactional
    public void deleteAlbumsInFamily(int familyId) {
        albumRepo.deleteAlbumsInFamily(familyId);
    }

    @Override
    public String getMostRecentImageUriInAlbum(int albumId) {
        return albumRepo.getMostRecentImageUriInAlbum(albumId);
    }


}
