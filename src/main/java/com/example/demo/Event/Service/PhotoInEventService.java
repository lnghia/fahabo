package com.example.demo.Event.Service;

import com.example.demo.Event.Entity.PhotoInEvent;
import com.example.demo.Event.Repo.PhotoInEventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PhotoInEventService {
    @Autowired
    private PhotoInEventRepo photoInEventRepo;

    public PhotoInEvent savePhotoInEvent(PhotoInEvent photoInEvent) {
        return photoInEventRepo.save(photoInEvent);
    }

    public int countPhotosNumInEvent(int eventAlbumId) {
        return photoInEventRepo.countPhotoInEvent(eventAlbumId);
    }

    public void deletePhotosInFamilyEvents(int familyId) {
        photoInEventRepo.deletePhotosINEventAlbumByFamilyId(familyId);
    }

    public void deletePhotosInEvent(int eventId) {
        photoInEventRepo.deletePhotosInEvent(eventId);
    }

    public ArrayList<PhotoInEvent> getAllPhotos(int eventAlbumId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return photoInEventRepo.findAllByEventAlbumId(eventAlbumId, pageable);
    }
}
