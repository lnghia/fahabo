package com.example.demo.Repo;

import com.example.demo.domain.ChoresAssignUsers;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoresAssignUsersRepo extends JpaRepository<ChoresAssignUsers, Integer>, JpaSpecificationExecutor<ChoresAssignUsers> {
    @Query(value = "SELECT chored_id FROM (SELECT b.is_deleted, b.family_id, a.chore_id FROM chores_assign_users as a INNER JOIN chores as b ON a.chore_id=b.id) as c " +
            "WHERE c.is_deleted=FALSE AND family_id=:familyId", nativeQuery = true)
    int[] findChoreIdsByFamilyId(@Param("familyId") int familyId);
}
