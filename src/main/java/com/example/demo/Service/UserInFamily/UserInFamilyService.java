package com.example.demo.Service.UserInFamily;

import com.example.demo.domain.Family;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;

import java.util.List;

public interface UserInFamilyService {
    public UserInFamily saveUserInFamily(UserInFamily userInFamily);
    void delete(UserInFamily userInFamily);
    UserInFamily findByUserIdAndFamilyId(int userId, int familyId);
    void setRoleForUserInFamily(User user, Family family, Role role);
    boolean hasRole(User user, Family family, String roleName);
    List<Integer> getUserIdsInFamily(int familyId, int page, int size);
    List<User> getUsersInFamily(int familyId, int page, int size);
    List<UserInFamily> findAllByUserId(int userId);
    List<UserInFamily> findAllByUserIdWithPagination(int userId, int page, int size);
}
