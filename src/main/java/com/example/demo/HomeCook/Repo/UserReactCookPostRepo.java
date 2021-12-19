package com.example.demo.HomeCook.Repo;

import com.example.demo.HomeCook.Entity.UserReactCookPost;
import com.example.demo.HomeCook.IdClass.UserReactCookPostIdClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserReactCookPostRepo extends JpaRepository<UserReactCookPost, UserReactCookPostIdClass> {
    @Query(value = "SELECT * FROM users_react_cookposts WHERE is_deleted=FALSE AND NOT reaction=4 AND user_id=:userId AND cookpost_id=:postId", nativeQuery = true)
    UserReactCookPost findByUserAndPost(@Param("userId") int userId,
                                        @Param("postId") int postId);

    @Query(value = "SELECT * FROM users_react_cookposts WHERE is_deleted=FALSE AND cookpost_id=:postId", nativeQuery = true)
    ArrayList<UserReactCookPost> findByPost(@Param("postId") int postId);
}
