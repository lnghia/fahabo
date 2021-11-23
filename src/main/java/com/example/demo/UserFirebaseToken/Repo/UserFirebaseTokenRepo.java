package com.example.demo.UserFirebaseToken.Repo;

import com.example.demo.UserFirebaseToken.Entity.UserFirebaseToken;
import com.example.demo.UserFirebaseToken.IdClass.UserFirebaseTokenIdClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFirebaseTokenRepo extends JpaRepository<UserFirebaseToken, UserFirebaseTokenIdClass> {
    @Modifying
    @Query(value = "UPDATE user_firebase_token SET is_deleted=TRUE WHERE user_id=:userId AND token=:token", nativeQuery = true)
    int disableToken(@Param("userId") int userId,
                     @Param("token") String token);

    @Modifying
    @Query(value = "UPDATE user_firebase_token SET is_deleted=TRUE WHERE token=:token AND is_deleted=FALSE", nativeQuery = true)
    int disableToken(@Param("token") String token);

    @Query(value = "SELECT token FROM user_firebase_token WHERE is_deleted=FALSE AND user_id=:userId", nativeQuery = true)
    List<String> getUserTokens(@Param("userId") int userId);

    @Query(value = "SELECT token FROM user_firebase_token WHERE is_deleted=FALSE AND user_id=:userId AND token=:token LIMIT 1", nativeQuery = true)
    String searchTokenInUser(@Param("userId") int userId,
                             @Param("token") String token);

    @Query(value = "SELECT * FROM user_firebase_token WHERE is_deleted=FALSE AND user_id=:userId AND token=:token", nativeQuery = true)
    UserFirebaseToken findTokenInUser(@Param("userId") int userId,
                           @Param("token") String token);

    @Query(value = "SELECT * FROM user_firebase_token WHERE user_id=:userId ORDER BY updated_at DESC", nativeQuery = true)
    List<UserFirebaseToken> findAllUserFirebaseTokenByUser(@Param("userId") int userId);
}
