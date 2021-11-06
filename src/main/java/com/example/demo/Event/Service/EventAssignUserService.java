package com.example.demo.Event.Service;

import com.example.demo.Event.Entity.EventAssignUser;
import com.example.demo.Event.Repo.EventAssignUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventAssignUserService {
    @Autowired
    private EventAssignUserRepo eventAssignUserRepo;

    public EventAssignUser saveEventAssignUser(EventAssignUser eventAssignUser){
        return eventAssignUserRepo.save(eventAssignUser);
    }

    public void deleteEventUserRelationship(int familyId){
        eventAssignUserRepo.deleteEventUserRelationByFamilyId(familyId);
    }
}
