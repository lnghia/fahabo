package com.example.demo.Album.Repo;

import com.example.demo.Album.Entity.PhotoInChore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PhotoInChoreRepo extends JpaRepository<PhotoInChore, Integer> {
    @Query(value = "SELECT * FROM photos_in_chore_albums WHERE is_deleted=FALSE AND chore_album_id=:choreAlbumId", nativeQuery = true)
    ArrayList<PhotoInChore> findAllByChoreAlbumId(@Param("choreAlbumId") int albumId, Pageable pageable);

    @Query(value = "SELECT * FROM photos_in_chore_albums WHERE is_deleted=FALSE AND chore_album_id=:choreAlbumId", nativeQuery = true)
    ArrayList<PhotoInChore> findAllByChoreAlbumId(@Param("choreAlbumId") int albumId);

    @Query(value = "SELECT * FROM photos_in_chore_albums WHERE is_deleted=FALSE AND chore_album_id=:choreAlbumId AND photo_id=:photoId", nativeQuery = true)
    PhotoInChore getPhotoInChoreByAlbumIdAndPhotoId(@Param("choreAlbumId") int choreAlbumId,
                                                    @Param("photoId") int photoId);

    @Modifying
    @Query(value = "UPDATE photos_in_chore_albums SET is_deleted=TRUE " +
            "WHERE is_deleted=FALSE " +
            "AND chore_albums IN (SELECT tmp.id FROM (SELECT a.id, a.is_deleted, b.family_id FROM chore_album AS a INNER JOIN chores AS b ON a.chore_id=b.id) AS tmp WHERE tmp.is_deleted=FALSE AND tmp.family_id=:familyId)",
            nativeQuery = true)
    void deletePhotosINChoreAlbumByFamilyId(@Param("familyId") int familyId);

    @Query(value = "SELECT COUNT(photos) FROM photos_in_chore_albums WHERE is_deleted=FALSE AND chore_album_id=:choreAlbumId", nativeQuery = true)
    int countPhotosNumInChore(@Param("choreAlbumId") int choreAlbumId);
}
