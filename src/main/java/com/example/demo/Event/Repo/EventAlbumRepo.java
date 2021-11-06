package com.example.demo.Event.Repo;

import com.example.demo.Event.Entity.EventAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventAlbumRepo extends JpaRepository<EventAlbum, Integer> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE events_albums SET is_deleted=TRUE WHERE is_deleted=FALSE AND event_id IN (select event_id from events WHERE is_deleted=FALSE AND family_id=:familyId)", nativeQuery = true)
    void deleteEventAlbumByFamily(@Param("familyId") int familyId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE events_albums SET is_deleted=TRUE " +
            "WHERE is_deleted=FALSE AND event_id=:eventId", nativeQuery = true)
    void deleteEventAlbumInEvent(@Param("eventId") int eventId);
}
