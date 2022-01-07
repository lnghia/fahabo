package com.example.demo.Helpers;

import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserPhotoHelper {
    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumsPhotosService albumsPhotosService;

    @Autowired
    private FamilyService familyService;

    public boolean canUserUpdatePhoto(User user, int photoId){
        ArrayList<Integer> albumId = albumsPhotosService.getAlbumIdByPhotoId(photoId);
        int familyId = albumService.getFamilyIdByAlbumId(albumId.get(0));
        Family family = familyService.findById(familyId);

        return family.checkIfUserExist(user);
    }
}
