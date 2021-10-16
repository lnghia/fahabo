package com.example.demo.Repo;

import com.example.demo.domain.Family;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FamilyRepo extends JpaRepository<Family, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users_roles (user_id, role_id, family_id) VALUES(:userId, :roleId, :familyId)", nativeQuery = true)
    void setAMemberWithRole(@Param("userId") int userId,
                            @Param("roleId") int roleId,
                            @Param("familyId") int familyId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users_in_families (user_id, family_id) VALUES(:userId, :familyId)", nativeQuery = true)
    void addMember(@Param("userId") int userId,
                   @Param("familyId") int familyId);

    @Query(value = "SELECT * FROM families WHERE name=:name", nativeQuery = true)
    Family getByName(@Param("name") String name);

    @Query(value = "SELECT * FROM families WHERE id=:id", nativeQuery = true)
    Family getById(@Param("id") int id);

    @Query(value = "SELECT user_id FROM users_in_families WHERE user_id=:id", nativeQuery = true)
    Integer getMemberById(@Param("id") int id);
}
