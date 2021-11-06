package com.example.demo.Event.Repo;

import com.example.demo.Event.Entity.GroupEvent;
import com.example.demo.domain.IdClasses.GroupEventIdClass;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface GroupEventRepo extends JpaRepository<GroupEvent, GroupEventIdClass> {
//    @Query(value = "INSERT INTO group_event (head_event_id, sub_event_id) VALUES (:headEventId, :subEventId)", nativeQuery = true)
//    void

    @Query(value = "SELECT * FROM group_event WHERE is_deleted=FALSE AND head_event_id=:headEventId", nativeQuery = true)
    ArrayList<GroupEvent> findAllSubEventsByHeadEvent(@Param("headEventId") int headEventId);

    @Query(value = "SELECT * FROM group_event WHERE is_deleted=FALSE AND head_event_id=:headEventId",
            countQuery = "COUNT sub_event_id FROM group_event WHERE is_deleted=FALSE AND head_event_id=:headEventId",
            nativeQuery = true)
    ArrayList<GroupEvent> findAllSubEventsByHeadEventWithPagination(@Param("headEventId") int headEventId, Pageable pageable);

    @Query(value = "SELECT * FROM group_event WHERE sub_event_id=:subEventId", nativeQuery = true)
    Integer findHeadEventIdByEventId(@Param("subEventId") int subEventId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE group_event SET is_deleted=TRUE " +
            "WHERE NOT sub_event_id=:headEventId " +
            "AND head_event_id=:headEventId", nativeQuery = true)
    Integer deleteAllSubEventsInGroup(@Param("headEventId") int headEventId);
}
