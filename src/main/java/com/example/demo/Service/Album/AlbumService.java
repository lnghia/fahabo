package com.example.demo.Service.Album;

import com.example.demo.domain.Album;
import com.example.demo.domain.Photo;

import javax.persistence.criteria.CriteriaBuilder;
import java.awt.print.Pageable;
import java.util.List;

public interface AlbumService {
    Album saveAlbum(Album album);
    Album findById(int id);
    Album findByFamilyIdAndTitle(int familyId, String title);
    boolean checkIfAlbumExistsInFamily(int albumId, int familyId);
    int countAllByAlbum(int albumId);
    int getFamilyIdByAlbumId(int albumId);
    List<Album> findAllByFamilyIdWithPagination(int familyId, int defaultAlbumId, int page, int size);
    List<Integer> findAllPhotoIdsByAlbumIdWithPagination(int albumId, int page, int size);
    List<Integer> get9LatestPhotosFromAlbum(int albumId);
}
