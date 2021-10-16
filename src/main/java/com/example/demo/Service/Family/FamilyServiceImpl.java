package com.example.demo.Service.Family;

import com.example.demo.Repo.FamilyRepo;
import com.example.demo.RequestForm.CreateFamilyReqForm;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class FamilyServiceImpl implements FamilyService{
    @Autowired
    private FamilyRepo familyRepo;

//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RoleService roleService;

    @Override
    public boolean setHost(User user, Family family) {
        if(family.getMembers().contains(user)){
//            Role role = roleService.findByRoleName("HOST");
//            familyRepo.setAMemberWithRole(user.getId(), role.getId(), family.getId());

            familyRepo.save(family);

            return true;
        }

        return false;
    }

    @Override
    public void addUser(ArrayList<User> users, Family family) {
        users.forEach(user -> {
//            user.getFamilies().add(family);
//            family.getMembers().add(user);
//            userService.updateUser(user);
            familyRepo.addMember(user.getId(), family.getId());
        });
        familyRepo.save(family);
    }

    @Override
    public void updateFamily(Family family) {
        familyRepo.save(family);
    }

    @Override
    public Family createFamily(User creator, Family createdFamily, CreateFamilyReqForm requestBody) {
//        ArrayList<User> users = new ArrayList<>();
//
//        requestBody.ids.forEach(id -> {
//            User tmpUser = userService.getUserById(id);
//            users.add(tmpUser);
//        });
//        users.add(creator);
//
//        if(requestBody.thumbnail != null){
////            family.setThumbnail(requestBody.thumbnail.);
//        }
//
//        addUser(users, createdFamily);
//        setHost(creator, createdFamily);
//
        return null;
    }

    @Override
    public void addMember(User user, Family family) {
        if(family.getMembers() == null){
            family.setMembers(new HashSet<>(List.of(user)));
        }
        else{
            family.getMembers().add(user);
        }
        familyRepo.save(family);
    }

    @Override
    public Family findByName(String name) {
        return familyRepo.getByName(name);
    }

    @Override
    public Family findById(int id) {
        return familyRepo.getById(id);
    }

    @Override
    public Integer findMemberById(int id) {
        return familyRepo.getMemberById(id);
    }
}
