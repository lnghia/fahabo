package com.example.demo.Repo;

import com.example.demo.domain.AlbumsPhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumsPhotosRepo extends JpaRepository<AlbumsPhotos, Integer> {
    @Query(value = "SELECT album_id FROM photos_in_albums WHERE photo_id=:photoId AND is_deleted=FALSE", nativeQuery = true)
    Integer getAlbumIdByPhotoId(@Param("photoId") int photoId);

    @Query(value = "SELECT * FROM photos_in_albums WHERE photo_id=:photoId AND is_deleted=FALSE", nativeQuery = true)
    AlbumsPhotos getByPhotoId(@Param("photoId") int photoId);
}


// SELECT ALBUMID FROM PHOTOSALBUMS WHERE PHOTO_ID=...

// SELECT FAMILYID FROM ALBUMFAMILY WHERE ALBUMID=...

//SELECT