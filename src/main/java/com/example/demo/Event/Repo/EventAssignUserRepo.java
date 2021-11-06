package com.example.demo.Event.Repo;

import com.example.demo.Event.Entity.EventAssignUser;
import com.example.demo.domain.IdClasses.EventAssignUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAssignUserRepo extends JpaRepository<EventAssignUser, EventAssignUserId> {
    @Query(value = "UPDATE events_assign_users SET is_deleted=TRUE WHERE is_deleted=FALSE AND event_id IN (SELECT id FROM events WHERE family_id=:familyId)", nativeQuery = true)
    void deleteEventUserRelationByFamilyId(@Param("familyId") int familyId);
}
