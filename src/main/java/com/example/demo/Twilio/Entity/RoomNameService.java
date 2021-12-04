package com.example.demo.Twilio.Entity;

import com.twilio.rest.video.v1.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomNameService {
    @Autowired
    private RoomNameRepo roomNameRepo;

    public RoomName findByRoomName(String roomName){
        return roomNameRepo.findByRoomName(roomName);
    }

    public RoomName save(RoomName roomName){
        return roomNameRepo.save(roomName);
    }

    public RoomName createRoomNameWithName(String name){
        RoomName roomName = new RoomName(name);
        return roomNameRepo.save(roomName);
    }

    public RoomName endRoom(String name){
        RoomName roomName = roomNameRepo.findByRoomName(name);
        roomName.setEnded(true);
        return roomNameRepo.save(roomName);
    }
}
