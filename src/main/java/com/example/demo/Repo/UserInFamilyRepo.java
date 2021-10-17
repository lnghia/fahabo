package com.example.demo.Repo;

import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInFamilyRepo extends JpaRepository<UserInFamily, Integer> {
    @Modifying
    @Query(value = "DELETE FROM users_in_families WHERE user_id=:userId AND family_id=:familyId", nativeQuery = true)
    void deleteById(@Param("userId") int userId,
                    @Param("familyId") int familyId);

    @Query(value = "SELECT * FROM users_in_families WHERE user_id=:userId AND family_id=:familyId", nativeQuery = true)
    UserInFamily findByUserIdAndFamilyId(@Param("userId") int userId,
                                         @Param("familyId") int familyId);

//    ORDER BY :#{#pageable}
    @Query(value = "SELECT DISTINCT users from users_in_families WHERE families=:familyId",
            countQuery = "SELECT count(DISTINCT users) FROM users_in_families WHERE families=:familyId",
            nativeQuery = true)
    List<Integer> getUserIdsInFamily(@Param("familyId") int familyId, Pageable pageable);

    @Query(value = "SELECT id, name, avatar, phone_number FROM users " +
            "WHERE id IN (SELECT DISTINCT user_id FROM users_in_families WHERE family_id=:familyId)",
            /*countQuery = "SELECT count(DISTINCT users) FROM users_in_families WHERE families=:familyId",*/
            nativeQuery = true)
    List<User> getUsersInFamily(@Param("familyId") int familyId, Pageable pageable);
}

//SELECT DISTINCT(*) FROM users WHERE id IN (SELECT DISTINCT users FROM users INNER JOIN users_in_families ON users.id = users_in_families.users)