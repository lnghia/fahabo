package com.example.demo.Notification.Repo;

import com.example.demo.Notification.Entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    @Query(value = "SELECT * FROM notifications WHERE user_id=:userId AND family_id=:familyId ORDER BY created_at DESC",
            countQuery = "SELECT * FROM notifications WHERE user_id=:userId AND family_id=:familyId",
            nativeQuery = true)
    ArrayList<Notification> getNotificationsByUserIdAndFamilyId(@Param("userId") int userId,
                                                                @Param("familyId") int familyId,
                                                                Pageable pageable);

    @Query(value = "SELECT * FROM notifications WHERE user_id=:userId AND id=:id", nativeQuery = true)
    Notification getByUserIdAndId(@Param("userId") int userId,
                                  @Param("id") int id);
}
