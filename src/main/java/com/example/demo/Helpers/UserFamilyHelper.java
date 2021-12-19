package com.example.demo.Helpers;

import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFamilyHelper {
    @Autowired
    private UserInFamilyService userInFamilyService;

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    public Family joinFamily(User user, Family family){
        UserInFamily userInFamily = new UserInFamily(user, family);

        userInFamilyService.saveUserInFamily(userInFamily);
        user.addFamily(userInFamily);
        family.addUser(userInFamily);
        userService.updateUser(user);
        familyService.saveFamily(family);

        return family;
    }
}
