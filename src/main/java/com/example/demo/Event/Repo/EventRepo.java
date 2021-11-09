package com.example.demo.Event.Repo;

import com.example.demo.Event.Entity.Event;
import com.example.demo.domain.Chore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Integer>{
    @Modifying
    @Query(value = "UPDATE events SET is_deleted=TRUE WHERE is_deleted=FALSE AND family_id=:familyId", nativeQuery = true)
    void deleteEventsInFamily(@Param("familyId") int familyId);

    @Query(value = "SELECT * " +
            "FROM events WHERE id IN (SELECT id " +
            "FROM (events AS a LEFT JOIN events_assign_users AS b ON id=event_id) " +
            "WHERE (a.is_deleted=FALSE " +
            "AND family_id=:familyId " +
            "AND (COALESCE(:userId) IS NULL OR (cast(b.user_id as VARCHAR) IN (:userId))) " +
            "AND (:title IS NULL OR :title='' OR LOWER(title) LIKE %:title%) " +
            "AND (:from='' OR :to='' OR (cast(from_time as VARCHAR) >= :from AND cast(from_time as VARCHAR) <= :to)" +
                                    "OR (cast(to_time as VARCHAR) >= :from AND cast(to_time as VARCHAR) <= :to) " +
                                    "OR (cast(from_time as VARCHAR) <= :from AND cast(to_time as VARCHAR) >= :to)))) " +
            "ORDER BY :sortByDeadline" +
            ", CASE WHEN :sortByDeadline THEN from_time END DESC " +
            ", CASE WHEN NOT :sortByDeadline THEN from_time END DESC",
            countQuery = "SELECT COUNT(id) FROM (events AS a LEFT JOIN events_assign_users AS b ON id=chore_id) " +
                    "WHERE events.is_deleted=FALSE " +
                    "AND family_id=:familyId " +
                    "AND (COALESCE(:userId) IS NULL OR (cast(b.user_id as VARCHAR) IN (:userId))) " +
                    "AND (:title IS NULL OR :title='' OR LOWER(a.title) LIKE %:title%)" +
                    "AND (:from='' OR :to='' OR (cast(from_time as VARCHAR) >= :from AND cast(from_time as VARCHAR) <= :to) " +
                                            "OR (cast(to_time as VARCHAR) >= :from AND cast(to_time as VARCHAR) <= :to) " +
                                            "OR (cast(from_time as VARCHAR) <= :from AND cast(to_time as VARCHAR) >= :to))",
            nativeQuery = true)
    ArrayList<Event> findAlLFilteredByUserAndStatusAndTitleSortedByCreatedAtOrDeadLine(@Param("familyId") int familyId,
                                                                                       @Param("userId") List<String> userId,
                                                                                       @Param("title") String title,
                                                                                       @Param("sortByDeadline") boolean sortByDeadline,
                                                                                       @Param("from") String from,
                                                                                       @Param("to") String to,
                                                                                       Pageable pageable);

    @Query(value = "SELECT id FROM events WHERE is_deleted=FALSE AND cast(date(from_time) as VARCHAR) <= :date AND cast(date(to_time) as VARCHAR) >= :date LIMIT 1", nativeQuery = true)
    Integer findAnEventIdOnDate(@Param("date") String date);
}

