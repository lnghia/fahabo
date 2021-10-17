package com.example.demo.Service.UserInFamily;

import com.example.demo.Repo.UserInFamilyRepo;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Service.Role.RoleService;
import com.example.demo.domain.Family;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserInFamilyServiceImpl implements UserInFamilyService{
    @Autowired
    private UserInFamilyRepo userInFamilyRepo;

    @Autowired
    private RoleService roleService;

    @Override
    public UserInFamily saveUserInFamily(UserInFamily userInFamily) {
//        return userInFamilyRepo.save(userInFamily.getUser().getId(), userInFamily.getFamily().getId());
        return userInFamilyRepo.save(userInFamily);
    }

    @Override
    @Transactional
    public void delete(UserInFamily userInFamily) {
        userInFamilyRepo.delete(userInFamily);
//        userInFamilyRepo.deleteById(userId, familyId);
    }

    @Override
    public UserInFamily findByUserIdAndFamilyId(int userId, int familyId) {
        return userInFamilyRepo.findByUserIdAndFamilyId(userId, familyId);
    }

    @Override
    public void setRoleForUserInFamily(User user, Family family, Role role) {
        UserInFamily userInFamily = userInFamilyRepo.findByUserIdAndFamilyId(user.getId(), family.getId());

//        userInFamily.getUser().getUserInFamilies().stream().filter(userInFamily1 -> userInFamily1.equals(userInFamily)).findFirst()
        userInFamily.setRole(role);
        userInFamilyRepo.save(userInFamily);
//        UserInFamily tmp =  user.getUserInFamilies().stream().filter(userInFamily1 -> userInFamily1.getFamilyId() == family.getId()).findFirst().orElse(null);
//        tmp.setRole(role);
    }

    @Override
    public boolean hasRole(User user, Family family, String roleName) {
        Role role = roleService.findByName(roleName);
        UserInFamily userInFamily = userInFamilyRepo.findByUserIdAndFamilyId(user.getId(), family.getId());

        return (role != null && userInFamily != null && userInFamily.getRole().equals(role));
    }

    @Override
    public List<Integer> getUserIdsInFamily(int familyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userInFamilyRepo.getUserIdsInFamily(familyId, pageable);
    }

    @Override
    public List<User> getUsersInFamily(int familyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userInFamilyRepo.getUsersInFamily(familyId, pageable);
    }
}
