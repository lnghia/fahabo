package com.example.demo.HomeCook.Repo;

import com.example.demo.HomeCook.Entity.CookPostPool;
import com.example.demo.HomeCook.IdClass.UserReactCookPostIdClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CookPostPoolRepo extends JpaRepository<CookPostPool, UserReactCookPostIdClass> {
    @Query(value = "SELECT * FROM cook_post_pool WHERE cook_post_id IN " +
            "(SELECT a.cook_post_id FROM (cook_post_pool AS a INNER JOIN cuisine_posts AS b ON b.id=a.cook_post_id) WHERE " +
            "a.user_id=:userId AND b.is_deleted=FALSE AND (:searchText IS NULL OR :searchText='' OR LOWER(b.title) LIKE %:searchText%)) " +
            "ORDER BY added_date",
            countQuery = "SELECT COUNT(*) FROM cook_post_pool WHERE cook_post_id IN " +
                    "(SELECT a.cook_post_id FROM (cook_post_pool AS a INNER JOIN cuisine_posts AS b ON b.id=a.cook_post_id) WHERE " +
                    "a.user_id=:userId AND b.is_deleted=FALSE AND (:searchText IS NULL OR :searchText='' OR LOWER(b.title) LIKE %:searchText%)) " +
                    "ORDER BY added_date",
            nativeQuery = true)
    ArrayList<CookPostPool> findAllByUser(@Param("userId") int userId,
                                          @Param("searchText") String searchText,
                                          Pageable pageable);
}
