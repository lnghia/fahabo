package com.example.demo.Event.Service;

import com.example.demo.Event.Entity.EventAlbum;
import com.example.demo.Event.Repo.EventAlbumRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventAlbumService {
    @Autowired
    private EventAlbumRepo eventAlbumRepo;

    public EventAlbum saveEventAlbum(EventAlbum eventAlbum){
        return eventAlbumRepo.save(eventAlbum);
    }

    public void deleteEventAlbumsInFamily(int familyId){
        eventAlbumRepo.deleteEventAlbumByFamily(familyId);
    }

    public void deleteEventAlbumInEvent(int eventId){
        eventAlbumRepo.deleteEventAlbumInEvent(eventId);
    }
}
