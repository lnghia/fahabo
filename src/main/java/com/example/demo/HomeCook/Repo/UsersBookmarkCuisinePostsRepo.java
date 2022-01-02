package com.example.demo.HomeCook.Repo;

import com.example.demo.HomeCook.Entity.UserBookmarkCuisinePost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UsersBookmarkCuisinePostsRepo extends JpaRepository<UserBookmarkCuisinePost, Integer> {
    @Query(value = "SELECT * FROM users_bookmark_cuisinepost WHERE users=:userId AND is_deleted=FALSE ORDER BY created_date DESC",
            countQuery = "SELECT * FROM users_bookmark_cuisinepost WHERE users=:userId AND is_deleted=FALSE",
            nativeQuery = true)
    ArrayList<UserBookmarkCuisinePost> findAllByUserSortByCreatedDate(@Param("userId") int userId,
                                                                            Pageable pageable);
}
