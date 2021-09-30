package com.example.demo.Repo;

import com.example.demo.domain.LanguageCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageCodeRepo extends JpaRepository<LanguageCode, String> {
    Optional<LanguageCode> findById(String id);
}
