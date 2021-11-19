package com.example.demo.Communication.Call.Repo;

import com.example.demo.Communication.Call.Entity.UserInCallRoom;
import com.example.demo.Communication.Call.IdClass.UserInCallRoomIdClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface USerInCallRoomRepo extends JpaRepository<UserInCallRoom, UserInCallRoomIdClass> {
    @Query(value = "SELECT user_id FROM users_in_call_rooms WHERE room_name=:roomName", nativeQuery = true)
    ArrayList<Integer> findAllUserIdInRoomCall(@Param("roomName") String roomName);

    @Modifying
    @Query(value = "DELETE FROM users_in_call_rooms WHERE room_name=:roomName", nativeQuery = true)
    int deleteAllUserInRoomCall(@Param("roomName") String roomName);

    @Modifying
    @Query(value = "DELETE FROM users_in_call_rooms WHERE room_name=:roomName AND user_id=:userId", nativeQuery = true)
    int deleteUserFromRoomCall(@Param("roomName") String roomName,
                               @Param("userId") int userId);

    @Query(value = "SELECT c.id FROM (users as a INNER JOIN users_in_call_rooms as b ON a.id=b.user_id) as c WHERE c.is_deleted=FALSE AND c.id=:userId", nativeQuery = true)
    Optional<Integer> checkIfUserISInAVideoCall(@Param("userId") int userId);
}
