package com.example.demo.Helpers;

import com.example.demo.Album.Service.Album.AlbumService;
import com.example.demo.Family.Service.Family.FamilyService;
import com.example.demo.UserInFamily.Service.UserInFamily.UserInFamilyService;
import com.example.demo.User.Service.UserService;
import com.example.demo.Album.Entity.Album;
import com.example.demo.User.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAlbumHelper {
    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private UserInFamilyService userInFamilyService;

    public boolean checkUserAlbumRelationship(User user, Album album){
        return userInFamilyService.findByUserIdAndFamilyId(user.getId(), album.getFamily().getId()) != null;
    }
}
