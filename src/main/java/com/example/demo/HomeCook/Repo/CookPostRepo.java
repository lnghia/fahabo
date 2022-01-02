package com.example.demo.HomeCook.Repo;

import com.example.demo.HomeCook.Entity.CookPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CookPostRepo extends JpaRepository<CookPost, Integer> {
    @Query(value = "SELECT * FROM cuisine_posts WHERE is_deleted=FALSE", nativeQuery = true)
    ArrayList<CookPost> findAll();

    @Query(value = "SELECT * FROM cuisine_posts WHERE is_deleted=FALSE AND id=:id", nativeQuery = true)
    CookPost findById(@Param("id") int id);

    @Query(value = "SELECT * FROM cuisine_posts WHERE is_deleted=FALSE AND author=:userId ORDER BY created_at DESC",
            countQuery = "SELECT * FROM cuisine_posts WHERE is_deleted=FALSE AND author=:userId", nativeQuery = true)
    ArrayList<CookPost> findAllByAuthor(@Param("userId") int userId,
                                        Pageable pageable);

    @Query(value = "SELECT * FROM cuisine_posts WHERE is_deleted=FALSE " +
            "AND (:searchText IS NULL OR :searchText='' OR LOWER(title) LIKE %:searchText%) ORDER BY ratings",
            countQuery = "SELECT COUNT(*) FROM cuisine_posts WHERE is_deleted=FALSE " +
                    "AND (:searchText IS NULL OR :searchText='' OR LOWER(title) LIKE %:searchText%) ORDER BY ratings",
            nativeQuery = true)
    ArrayList<CookPost> findAll(@Param("searchText") String searchText,
                                Pageable pageable);
}
