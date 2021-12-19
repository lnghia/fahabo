package com.example.demo.Twilio.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomNameRepo extends JpaRepository<RoomName, Integer> {
    RoomName findByRoomName(String roomName);
}
