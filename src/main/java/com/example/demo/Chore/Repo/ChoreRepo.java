package com.example.demo.Chore.Repo;

import com.example.demo.Chore.Entity.Chore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ChoreRepo extends JpaRepository<Chore, Integer>, JpaSpecificationExecutor<Chore> {
    @Query(value = "SELECT * FROM chores WHERE is_deleted=FALSE AND family_id=:familyId", nativeQuery = true)
    ArrayList<Chore> findByFamilyId(@Param("familyId") int familyId);

    @Query(value = "SELECT * FROM chores WHERE is_deleted=FALSE AND id=:id", nativeQuery = true)
    Chore getById(@Param("id") int id);

    @Query(value = "SELECT * " +
            "FROM chores WHERE id IN (SELECT id " +
            "FROM (chores AS a INNER JOIN chores_assign_users AS b ON id=chore_id) " +
            "WHERE (chores.is_deleted=FALSE " +
            "AND family_id=:familyId " +
            "AND (COALESCE(:userId) IS NULL OR (cast(b.user_id as VARCHAR) IN (:userId))) " +
            "AND (COALESCE(:status) IS NULL OR (a.status IN (:status))) " +
            "AND (:title IS NULL OR :title='' OR LOWER(title) LIKE %:title%) " +
            "AND (:from='' OR :to='' OR (cast(created_at as VARCHAR) >= :from AND cast(created_at as VARCHAR) <= :to)))) " +
            "ORDER BY :sortByDeadline" +
            ", CASE WHEN :sortByDeadline THEN deadline END DESC " +
            ", CASE WHEN NOT :sortByDeadline THEN created_at END DESC",
            countQuery = "SELECT COUNT(id) FROM (chores AS a INNER JOIN chores_assign_users AS b ON id=chore_id) " +
                    "WHERE chores.is_deleted=FALSE " +
                    "AND family_id=:familyId " +
                    "AND (COALESCE(:userId) IS NULL OR (cast(b.user_id as VARCHAR) IN (:userId))) " +
                    "AND (COALESCE(:status) IS NULL OR (a.status IN (:status))) " +
                    "AND (:title IS NULL OR :title='' OR LOWER(a.title) LIKE %:title%)" +
                    "AND (:from='' OR :to='' OR (cast(created_at as VARCHAR) >= :from AND cast(created_at as VARCHAR) <= :to)))",
            nativeQuery = true)
//@Query(value = "SELECT DISTINCT * " +
//        "FROM chores WHERE id in (SELECT a.id " +
//        "FROM chores as a INNER JOIN chores_assign_users as b ON a.id=b.chore_id " +
//        "WHERE (a.is_deleted=FALSE " +
//        "AND a.family_id=:familyId " +
//        "AND (:userId IS NULL OR (cast(b.user_id as VARCHAR) IN :userId)) " +
//        "AND ((COALESCE(:status) IS NULL) OR (a.status IN (:status))) " +
//        "AND (:title IS NULL OR a.title LIKE cast(:title as text)) " +
//        "AND (:from IS NULL OR :to IS NULL OR cast(created_at as VARCHAR) >= :from AND cast(created_at as VARCHAR) <= :to)) " +
//        "ORDER BY :sortByDeadline" +
//        ", CASE WHEN :sortByDeadline THEN a.deadline END DESC " +
//        ", CASE WHEN NOT :sortByDeadline THEN a.created_at END DESC)",
//        countQuery = "SELECT COUNT(DISTINCT a.id) FROM chores as a INNER JOIN chores_assign_users as b ON a.id=b.chore_id " +
//                "WHERE a.is_deleted=FALSE " +
//                "AND a.family_id=:familyId " +
//                "AND (:userId IS NULL OR (cast(b.user_id as VARCHAR) IN (:userId))) " +
//                "AND (COALESCE(:status) IS NULL OR (a.status IN (:status))) " +
//                "AND (:title IS NULL OR a.title LIKE cast(:title as text))" +
//                "AND (:from IS NULL OR COALESCE(:to) OR cast(created_at as VARCHAR) >= :from AND cast(created_at as VARCHAR) <= :to)) ",
//        nativeQuery = true)
    ArrayList<Chore> findAlLFilteredByUserAndStatusAndTitleSortedByCreatedAtOrDeadLine(@Param("familyId") int familyId,
                                                                                       @Param("userId") List<String> userId,
                                                                                       @Param("status") List<String> status,
                                                                                       @Param("title") String title,
                                                                                       @Param("sortByDeadline") boolean sortByDeadline,
                                                                                       @Param("from") String from,
                                                                                       @Param("to") String to,
                                                                                       Pageable pageable);
    @Modifying
    @Query(value = "UPDATE chores SET is_deleted=TRUE WHERE is_deleted=FALSE AND family_id=:familyId", nativeQuery = true)
    void deleteChoresInFamily(@Param("familyId") int familyId);
}
