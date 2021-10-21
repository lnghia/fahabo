package com.example.demo.Helpers;

import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Album;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
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
