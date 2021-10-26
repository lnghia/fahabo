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
}
