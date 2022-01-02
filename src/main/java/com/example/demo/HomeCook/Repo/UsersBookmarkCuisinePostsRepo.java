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
    @Query(value = "SELECT * FROM ((SELECT * FROM users_bookmark_cuisinepost ubc WHERE is_deleted=false and users=:userId) as a LEFT JOIN cuisine_posts cp ON a.posts=cp.id) as c " +
            "WHERE (:searchText IS NULL OR :searchText='' OR LOWER(c.title) LIKE %:searchText%) " +
            "ORDER BY created_date DESC",
            countQuery = "SELECT * FROM ((SELECT * FROM users_bookmark_cuisinepost ubc WHERE is_deleted=false and users=:userId) as a LEFT JOIN cuisine_posts cp ON a.posts=cp.id) as c " +
                    "WHERE (:searchText IS NULL OR :searchText='' OR LOWER(c.title) LIKE %:searchText%) ",
            nativeQuery = true)
    ArrayList<UserBookmarkCuisinePost> findAllByUserSortByCreatedDate(@Param("userId") int userId,
                                                                      @Param("searchText") String searchText,
                                                                      Pageable pageable);

    @Query(value = "SELECT * FROM users_bookmark_cuisinepost WHERE users=:userId AND is_deleted=FALSE AND posts=:postId", nativeQuery = true)
    UserBookmarkCuisinePost findByUserAndPost(@Param("userId") int userId,
                                              @Param("postId") int postId);
}
