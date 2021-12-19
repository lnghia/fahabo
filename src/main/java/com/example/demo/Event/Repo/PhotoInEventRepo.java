package com.example.demo.Event.Repo;

import com.example.demo.Event.Entity.PhotoInEvent;
import com.example.demo.domain.IdClasses.PhotoInEventId;
import com.example.demo.domain.PhotoInChore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Repository
public interface PhotoInEventRepo extends JpaRepository<PhotoInEvent, PhotoInEventId> {
    @Query(value = "SELECT COUNT(photos) FROM photos_in_events WHERE is_deleted=FALSE AND event_album_id=:eventAlbumId", nativeQuery = true)
    int countPhotoInEvent(@Param("eventAlbumId") int eventAlbumId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE photos_in_events SET is_deleted=TRUE " +
            "WHERE is_deleted=FALSE " +
            "AND event_albums IN (SELECT tmp.id FROM (SELECT a.id, a.is_deleted, b.family_id FROM events_albums AS a INNER JOIN events AS b ON a.event_id=b.id) AS tmp WHERE tmp.is_deleted=FALSE AND tmp.family_id=:familyId)",
            nativeQuery = true)
    int deletePhotosINEventAlbumByFamilyId(@Param("familyId") int familyId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE photos_in_events SET is_deleted=TRUE " +
            "WHERE event_albums IN (SELECT id FROM events_albums WHERE event_id=:eventId AND is_deleted=FALSE)", nativeQuery = true)
    int deletePhotosInEvent(@Param("eventId") int eventId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE photos_in_events SET is_deleted=TRUE " +
            "WHERE event_albums IN (SELECT id FROM events_albums WHERE event_id=:eventId AND is_deleted=FALSE) AND photo_id=:photoId", nativeQuery = true)
    int deletePhotosInEventByPhotoId(@Param("eventId") int eventId, @Param("photoId") int photoId);

    @Query(value = "SELECT * FROM photos_in_events WHERE is_deleted=FALSE AND event_album_id=:eventAlbumId", nativeQuery = true)
    ArrayList<PhotoInEvent> findAllByEventAlbumId(@Param("eventAlbumId") int albumId, Pageable pageable);

    @Query(value = "SELECT photo_id FROM photos_in_events WHERE is_deleted=FALSE AND event_album_id=:eventAlbumId", nativeQuery = true)
    ArrayList<Integer> findAllByEventAlbumId(@Param("eventAlbumId") int albumId);


}
