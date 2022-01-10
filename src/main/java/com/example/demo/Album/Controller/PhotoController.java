package com.example.demo.Album.Controller;

import com.example.demo.Album.Entity.Album;
import com.example.demo.Album.Entity.AlbumsPhotos;
import com.example.demo.Album.Entity.Image;
import com.example.demo.Album.Entity.Photo;
import com.example.demo.DropBox.*;
import com.example.demo.Helpers.MediaFileHelper;
import com.example.demo.Helpers.UserAlbumHelper;
import com.example.demo.Album.Helper.UserPhotoHelper;
import com.example.demo.RequestForm.DeletePhotoReqForm;
import com.example.demo.RequestForm.GetPhotosReqForm;
import com.example.demo.RequestForm.UpdatePhotoReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Album.Service.Album.AlbumService;
import com.example.demo.Album.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Album.Service.Photo.PhotoService;
import com.example.demo.User.Service.UserService;
import com.example.demo.User.Entity.CustomUserDetails;
import com.example.demo.User.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumsPhotosService albumsPhotosService;

    @Autowired
    private UserPhotoHelper userPhotoHelper;

    @Autowired
    private UserAlbumHelper userAlbumHelper;

    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

    @Autowired
    private MediaFileHelper mediaFileHelper;

    @Autowired
    private UserService userService;

    @PostMapping("/delete")
    public ResponseEntity<Response> deletePhoto(@Valid @RequestBody DeletePhotoReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (requestBody.photoIds == null)
            return ResponseEntity.ok(new Response("no data to delete.", new ArrayList<>()));

        for (int photoId : requestBody.photoIds) {
            if (photoService.checkIfPhotoExistById(photoId) && userPhotoHelper.canUserUpdatePhoto(user, photoId)) {
                Photo photo = photoService.getById(photoId);
                ArrayList<AlbumsPhotos> albumsPhotos = albumsPhotosService.getByPhotoId(photoId);

                for (var albumPhoto : albumsPhotos){
                    albumPhoto.setDeleted(true);
                    photo.setDeleted(true);
                    albumsPhotosService.saveAlbumsPhotos(albumPhoto);
                    photoService.savePhoto(photo);
                }
            }
        }

        return ResponseEntity.ok(new Response("deleted successfully", new ArrayList<>()));
    }

    @PostMapping
    public ResponseEntity<Response> getPhotos(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                              @Valid @RequestBody GetPhotosReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Album album = albumService.findById(requestBody.albumId);

        if (userAlbumHelper.checkUserAlbumRelationship(user, album)) {
            List<Integer> photoIds = albumService.findAllPhotoIdsByAlbumIdWithPagination(requestBody.albumId, page, size);
            List<Photo> photos = photoIds.stream().map(photoId -> {
                return photoService.getById(photoId);
            }).collect(Collectors.toList());
            ArrayList<HashMap<String, Object>> data;

            try {
                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();

                Date start = new Date();

                GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(photos.stream().map(photo -> {
                    return new Image(photo.getName(), photo.getUri());
                }).collect(Collectors.toList())));

                if (executionResult != null) {
                    Date secondStart = new Date();

                    data = new ArrayList<>(photos.stream()
                            .map(photo -> {
                                return (executionResult.getSuccessfulResults().containsKey(photo.getName())) ?
                                        photo.getJsonWithRedirectedUri(executionResult.getSuccessfulResults().get(photo.getName()).getUri()) : photo.getJson(null);
                            }).collect(Collectors.toList()));

                    Date end = new Date();

                    log.info("Get photos execution time: " + Long.toString(end.getTime() - start.getTime()));
                    log.info("Map photo results execution time: " + Long.toString(end.getTime() - secondStart.getTime()));

                    return ResponseEntity.ok(new Response(data, new ArrayList<>()));
                }

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_photo")
    public ResponseEntity<Response> updatePhoto(@Valid @RequestBody UpdatePhotoReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (userPhotoHelper.canUserUpdatePhoto(user, requestBody.photoId)) {
            Photo photo = photoService.getById(requestBody.photoId);
            ArrayList<Integer> albumId = albumsPhotosService.getAlbumIdByPhotoId(requestBody.photoId);
            int familyId = albumService.getFamilyIdByAlbumId(albumId.get(0));

            if (requestBody.base64Data != null && !requestBody.base64Data.isBlank() && !requestBody.base64Data.isEmpty()) {
                try {
                    Image image = new Image();
                    image.setName(photo.getName());
                    image.setBase64Data(requestBody.base64Data);

                    String photoUri = mediaFileHelper.updatePhoto(image, photo, albumId.get(0), familyId);

                    if (photoUri == null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                    }

                    photo.setUri(photoUri);
                    photoService.savePhoto(photo);

                    return ResponseEntity.ok(new Response(photo.getJsonWithRedirectedUri(photoUri), new ArrayList<>()));
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Cound not upload image", e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @GetMapping(value = "/{id:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        try {
            FileInputStream fileInputStream = mediaFileHelper.readImg(id);

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(fileInputStream.readAllBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
