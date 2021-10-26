package com.example.demo.Repo;

import com.example.demo.domain.ChoreAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoreAlbumRepo extends JpaRepository<ChoreAlbum, Integer> {

}
