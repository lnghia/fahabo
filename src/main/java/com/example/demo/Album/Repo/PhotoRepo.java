package com.example.demo.Album.Repo;

import com.example.demo.Album.Entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoRepo extends JpaRepository<Photo, Integer> {
    @Query(value = "SELECT * FROM photos WHERE name=:name AND is_deleted=FALSE", nativeQuery = true)
    Photo getByName(@Param("name") String name);

    @Query(value = "SELECT * FROM photos WHERE id=:id AND is_deleted=FALSE", nativeQuery = true)
    Photo getById(@Param("id") int id);

    @Query(value = "SELECT id FROM photos WHERE id=:id AND is_deleted=FALSE", nativeQuery = true)
    Optional<Integer> checkIfPhotoExistById(@Param("id") int photoId);
}
