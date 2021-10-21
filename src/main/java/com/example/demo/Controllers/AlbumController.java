package com.example.demo.Controllers;

import com.example.demo.Helpers.AlbumFamilyHelper;
import com.example.demo.Helpers.UserAlbumHelper;
import com.example.demo.RequestForm.CreateAlbumReqForm;
import com.example.demo.RequestForm.DeleteAlbumReqForm;
import com.example.demo.RequestForm.UpdateAlbumReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(path = "/api/v1/albums")
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private AlbumsPhotosService albumsPhotosService;

    @Autowired
    private UserInFamilyService userInFamilyService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private AlbumFamilyHelper albumFamilyHelper;

    @Autowired
    private UserAlbumHelper userAlbumHelper;

    @PostMapping("/new_album")
    public ResponseEntity<Response> createAlbum(@Valid @RequestBody CreateAlbumReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);
        Date now = new Date();

        if (family.checkIfUserExist(user)) {
            if(!albumFamilyHelper.isAlbumTitleUniqueInFamily(family.getId(), requestBody.title)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.albumTitleExists"))));
            }

            Album newAlbum = new Album(requestBody.title);

            if(requestBody.description != null && !requestBody.description.isEmpty() && !requestBody.description.isBlank()){
                newAlbum.setDescription(requestBody.description);
            }
            newAlbum.setFamily(family);
            newAlbum.setCreatedAt(now);
            newAlbum.setUpdatedAt(now);
            albumService.saveAlbum(newAlbum);

            family.addAlbum(newAlbum);
            familyService.saveFamily(family);

            return ResponseEntity.ok(new Response(newAlbum.getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_album")
    public ResponseEntity<Response> updateAlbum(@Valid @RequestBody UpdateAlbumReqForm requestBody){
        Album album = albumService.findById(requestBody.albumId);
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserInFamily userInFamily = userInFamilyService.findByUserIdAndFamilyId(user.getId(), album.getFamily().getId());

        if(userInFamily != null){
            if(!albumFamilyHelper.isAlbumTitleUniqueInFamily(album.getFamily().getId(), requestBody.title)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.albumTitleExists"))));
            }

            Date now = new Date();

            album.setUpdatedAt(now);
            album.setTitle(requestBody.title);
            if(requestBody.description != null && !requestBody.description.isBlank() && !requestBody.description.isEmpty()){
                album.setDescription(requestBody.description);
            }
            albumService.saveAlbum(album);

            return ResponseEntity.ok(new Response(album.getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/delete_album")
    public ResponseEntity<Response> deleteAlbum(@Valid @RequestBody DeleteAlbumReqForm requestBody){
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Album album = albumService.findById(requestBody.albumId);

        if(userAlbumHelper.checkUserAlbumRelationship(user, album)){
            album.getPhotosInAlbum().forEach(albumsPhotos -> {
                albumsPhotos.getPhoto().setDeleted(true);
                albumsPhotos.setDeleted(true);
                albumsPhotosService.saveAlbumsPhotos(albumsPhotos);
            });
            album.setDeleted(true);
            albumService.saveAlbum(album);

            return ResponseEntity.ok(new Response(album.getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
