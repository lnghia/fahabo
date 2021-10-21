package com.example.demo.Repo;

import com.example.demo.domain.AlbumsPhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumsPhotosRepo extends JpaRepository<AlbumsPhotos, Integer> {
}
