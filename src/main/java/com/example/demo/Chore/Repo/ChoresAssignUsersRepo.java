package com.example.demo.Chore.Repo;

import com.example.demo.Chore.Entity.ChoresAssignUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoresAssignUsersRepo extends JpaRepository<ChoresAssignUsers, Integer>, JpaSpecificationExecutor<ChoresAssignUsers> {
    @Query(value = "SELECT chored_id FROM (SELECT b.is_deleted, b.family_id, a.chore_id FROM chores_assign_users as a INNER JOIN chores as b ON a.chore_id=b.id) as c " +
            "WHERE c.is_deleted=FALSE AND family_id=:familyId", nativeQuery = true)
    int[] findChoreIdsByFamilyId(@Param("familyId") int familyId);

    @Modifying
    @Query(value = "UPDATE chores_assign_users SET is_deleted=TRUE WHERE is_deleted=FALSE AND chore_id IN (SELECT id FROM chores WHERE family_id=:familyId)", nativeQuery = true)
    void deleteChoreUserRelationByFamilyId(@Param("familyId") int familyId);
}
