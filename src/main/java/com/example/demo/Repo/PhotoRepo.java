package com.example.demo.Repo;

import com.example.demo.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepo extends JpaRepository<Photo, Integer> {

}
