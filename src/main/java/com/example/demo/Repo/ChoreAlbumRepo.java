package com.example.demo.Repo;

import com.example.demo.domain.ChoreAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoreAlbumRepo extends JpaRepository<ChoreAlbum, Integer> {
    @Modifying
    @Query(value = "UPDATE chore_album SET is_deleted=TRUE WHERE is_deleted=FALSE AND chore_id IN (select chore_id from chores WHERE is_deleted=FALSE AND family_id=:familyId)", nativeQuery = true)
    void deleteChoreAlbumByFamily(@Param("familyId") int familyId);
}
