package com.example.demo.Helpers;

import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Role.RoleService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
@Data
public class FamilyHelper {
    public final String defaultThumbnail = "https://media.istockphoto.com/vectors/large-happy-family-is-standing-and-hugging-several-generations-with-vector-id1221390042?b=1&k=20&m=1221390042&s=612x612&w=0&h=DIY35-8Q2jZe8LMLdwXETf54sKwQL3_F6OCtXdTnB44=";

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FamilyService familyService;

    public void createFamily(Family family){
        familyService.updateFamily(family);
    }

    public void addMember(Family family, List<User> users){
        for(User user : users){
            familyService.addMember(user, family);
            userService.joinFamily(user, family);
        }
    }

    public String generateImgUploadId(Family family){
        return String.format("thumbnail_%s_%s.png", family.getId(), new Date().getTime());
    }
}
