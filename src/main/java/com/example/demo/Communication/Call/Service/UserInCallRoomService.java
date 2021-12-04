package com.example.demo.Communication.Call.Service;

import com.example.demo.Communication.Call.Entity.UserInCallRoom;
import com.example.demo.Communication.Call.Repo.USerInCallRoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserInCallRoomService {
    @Autowired
    private USerInCallRoomRepo uSerInCallRoomRepo;

    public UserInCallRoom saveUserInCallRoom(UserInCallRoom userInCallRoom){
        return uSerInCallRoomRepo.save(userInCallRoom);
    }

    public ArrayList<Integer> findAllUserIdInRoomCall(String roomName){
        return uSerInCallRoomRepo.findAllUserIdInRoomCall(roomName);
    }

    @Transactional
    public int deleteAllUserInRoom(String roomName){
        return uSerInCallRoomRepo.deleteAllUserInRoomCall(roomName);
    }

    @Transactional
    public int deleteUserFromRoom(String roomName, int userId){
        return uSerInCallRoomRepo.deleteUserFromRoomCall(roomName, userId);
    }

    public boolean checkIfUserIsInAVideoCall(int userId){
        return uSerInCallRoomRepo.checkIfUserISInAVideoCall(userId).isPresent();
    }

    public int countPeopleLeftInRoom(String roomName){
        return uSerInCallRoomRepo.countPeopleLeftInRoom(roomName);
    }
}
