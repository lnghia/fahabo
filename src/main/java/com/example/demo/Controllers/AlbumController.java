package com.example.demo.Controllers;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.DropBoxAuthenticator;
import com.example.demo.DropBox.DropBoxUploader;
import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.Helpers.AlbumFamilyHelper;
import com.example.demo.Helpers.AlbumHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.Helpers.UserAlbumHelper;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/api/v1/albums")
@Slf4j
public class AlbumController {
    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

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

    @Autowired
    private AlbumHelper albumHelper;

    @PostMapping("/new_album")
    public ResponseEntity<Response> createAlbum(@Valid @RequestBody CreateAlbumReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);
        Date now = new Date();

        if (family.checkIfUserExist(user)) {
            if (!albumFamilyHelper.isAlbumTitleUniqueInFamily(family.getId(), requestBody.title)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.albumTitleExists"))));
            }

            Album newAlbum = new Album(requestBody.title);

            if (requestBody.description != null && !requestBody.description.isEmpty() && !requestBody.description.isBlank()) {
                newAlbum.setDescription(requestBody.description);
            }
            newAlbum.setFamily(family);
            newAlbum.setCreatedAt(now);
            newAlbum.setUpdatedAt(now);
            albumService.saveAlbum(newAlbum);

            family.addAlbum(newAlbum);
            familyService.saveFamily(family);

            return ResponseEntity.ok(new Response(albumHelper.getJson(newAlbum), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_album")
    public ResponseEntity<Response> updateAlbum(@Valid @RequestBody UpdateAlbumReqForm requestBody) {
        Album album = albumService.findById(requestBody.albumId);
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserInFamily userInFamily = userInFamilyService.findByUserIdAndFamilyId(user.getId(), album.getFamily().getId());

        if (userInFamily != null) {
            if (!albumFamilyHelper.isAlbumTitleUniqueInFamily(album.getFamily().getId(), requestBody.title)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.albumTitleExists"))));
            }

            Date now = new Date();

            album.setUpdatedAt(now);
            album.setTitle(requestBody.title);
            if (requestBody.description != null && !requestBody.description.isBlank() && !requestBody.description.isEmpty()) {
                album.setDescription(requestBody.description);
            }
            albumService.saveAlbum(album);

            return ResponseEntity.ok(new Response(albumHelper.getJson(album), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/delete_album")
    public ResponseEntity<Response> deleteAlbum(@Valid @RequestBody DeleteAlbumReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Album album = albumService.findById(requestBody.albumId);

        if (userAlbumHelper.checkUserAlbumRelationship(user, album)) {
            album.getPhotosInAlbum().forEach(albumsPhotos -> {
                albumsPhotos.getPhoto().setDeleted(true);
                albumsPhotos.setDeleted(true);
                albumsPhotosService.saveAlbumsPhotos(albumsPhotos);
            });
            album.setDeleted(true);
            albumService.saveAlbum(album);

            return ResponseEntity.ok(new Response(albumHelper.getJson(album), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/add_photo")
    public ResponseEntity<Response> addPhoto(@Valid @RequestBody AddPhotoReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Album album = albumService.findById(requestBody.albumId);

        if (userAlbumHelper.checkUserAlbumRelationship(user, album)) {
            List<Photo> photos = new ArrayList<>();

            try {
                Date now = new Date();

                DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();
                DropBoxUploader uploader = new DropBoxUploader(clientV2);

                for (int i = 0; i < requestBody.photos.size(); ++i) {
                    photos.add(new Photo(now, now));
                    photoService.savePhoto(photos.get(i));
                    AlbumsPhotos albumsPhotos = new AlbumsPhotos(album, photos.get(i));
                    albumsPhotosService.saveAlbumsPhotos(albumsPhotos);
                    photos.get(i).getPhotoInAlbums().add(albumsPhotos);
                    photoService.savePhoto(photos.get(i));
                }

                ItemToUpload[] itemsToUpload = Helper.getInstance().listOfImagesToArrOfItemToUploadWithGeneratedName(requestBody.photos, photos, album.getId(), album.getFamily().getId());

                UploadExecutionResult result = uploader.uploadItems(itemsToUpload);

                if (result == null) {
                    for (var photo : photos) {
                        photo.setDeleted(true);
                        photo.getPhotoInAlbums().forEach(albumsPhotos -> albumsPhotos.setDeleted(true));
                        photoService.savePhoto(photo);
                    }

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
                }

//                HashMap<String, Object> data = new HashMap<>();
                ArrayList<Image> successUploads = new ArrayList<>();
                ArrayList<Image> failUploads = new ArrayList<>();

                result.getCreationResults().forEach((k, v) -> {
                    if (v.isOk()) {
                        successUploads.add(new Image(k, v.metadata, v.uri.get()));
                    } else {
                        failUploads.add(new Image(k, v.metadata));
                    }
                });

                if (successUploads.isEmpty()) {
                    for (var photo : photos) {
                        photo.setDeleted(true);
                        photo.getPhotoInAlbums().forEach(albumsPhotos -> albumsPhotos.setDeleted(true));
                        photoService.savePhoto(photo);
                    }

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
                }

                ArrayList<HashMap<String, Object>> data = new ArrayList<>();
                for (var item : successUploads) {
                    Photo photo = photoService.getByName(item.getName());
                    photo.setUri(item.getMetadata().getUrl());
                    photoService.savePhoto(photo);

                    data.add(photo.getJson(item.getUri()));
                }

                return ResponseEntity.ok(new Response(data, new ArrayList<>()));
            } catch (ExecutionException | InterruptedException e) {
                for (var photo : photos) {
                    photo.setDeleted(true);
                    photo.getPhotoInAlbums().forEach(albumsPhotos -> albumsPhotos.setDeleted(true));
                    photoService.savePhoto(photo);
                }

                log.error("Error creating threads to execute upload images " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping
    public ResponseEntity<Response> getAlbums(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                              @Valid @RequestBody GetAlbumsReqForm requestBody){
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if(family.checkIfUserExist(user)){
            List<Album> albums = albumService.findAllByFamilyIdWithPagination(family.getId(), page, size);

            return ResponseEntity.ok(new Response(albums.stream().map(album -> albumHelper.getJson(album)).collect(Collectors.toList()), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
