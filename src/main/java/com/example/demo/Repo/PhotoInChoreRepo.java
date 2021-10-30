package com.example.demo.Repo;

import com.example.demo.domain.PhotoInChore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
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

    @Query("UPDATE photos_in_chore_albums SET is_deleted=TRUE " +
            "WHERE is_deleted=FALSE " +
            "AND chore_albums IN (SELECT a.id FROM (chore_album AS a INNER JOIN chores AS b ON a.chore_id=b.id) WHERE is_deleted=FALSE AND family_id=:familyId)")
    void deletePhotosINChoreAlbumByFamilyId(@Param("familyId") int familyId);
}
