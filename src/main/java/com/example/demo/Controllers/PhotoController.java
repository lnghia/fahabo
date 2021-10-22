package com.example.demo.Controllers;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.*;
import com.example.demo.Helpers.Helper;
import com.example.demo.Helpers.UserAlbumHelper;
import com.example.demo.Helpers.UserPhotoHelper;
import com.example.demo.RequestForm.DeletePhotoReqForm;
import com.example.demo.RequestForm.GetPhotosReqForm;
import com.example.demo.RequestForm.UpdateAlbumReqForm;
import com.example.demo.RequestForm.UpdatePhotoReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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

    @PostMapping("/delete")
    public ResponseEntity<Response> deletePhoto(@Valid @RequestBody DeletePhotoReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if(requestBody.photoId == null)
            return ResponseEntity.ok(new Response("no data to delete.", new ArrayList<>()));

        for (int photoId : requestBody.photoId) {
            if (userPhotoHelper.canUserUpdatePhoto(user, photoId)) {
                Photo photo = photoService.getById(photoId);
                AlbumsPhotos albumsPhotos = albumsPhotosService.getByPhotoId(photoId);

                albumsPhotos.setDeleted(true);
                photo.setDeleted(true);
                albumsPhotosService.saveAlbumsPhotos(albumsPhotos);
                photoService.savePhoto(photo);

//                return ResponseEntity.ok(new Response("deleted successfully.", new ArrayList<>()));
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
                DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();
                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();

                GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(photos.stream().map(photo -> {
                    return new Image(photo.getName(), photo.getUri());
                }).collect(Collectors.toList())));

                if (executionResult != null) {
                    data = new ArrayList<>(photos.stream()
                            .map(photo -> {
                                return (executionResult.getSuccessfulResults().containsKey(photo.getName())) ?
                                        photo.getJson(executionResult.getSuccessfulResults().get(photo.getName()).getUri()) : photo.getJson(null);
                            }).collect(Collectors.toList()));

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
//        Album album = albumService.findById(requestBody.);

        if (userPhotoHelper.canUserUpdatePhoto(user, requestBody.photoId)) {
            Photo photo = photoService.getById(requestBody.photoId);
            int albumId = albumsPhotosService.getAlbumIdByPhotoId(requestBody.photoId);
            int familyId = albumService.getFamilyIdByAlbumId(albumId);

            if (requestBody.base64Data != null && !requestBody.base64Data.isBlank() && !requestBody.base64Data.isEmpty()) {
                try {
                    DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();
                    DropBoxUploader uploader = new DropBoxUploader(clientV2);

                    Image image = new Image();
                    image.setName(photo.getName());
                    image.setBase64Data(requestBody.base64Data);

                    UploadExecutionResult result = uploader.uploadItems(Helper.getInstance().listOfImagesToArrOfItemToUploadWithGeneratedName(List.of(image),
                            List.of(photo),
                            albumId,
                            familyId
                    ));

                    if(result == null){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                    }

                    ArrayList<Image> successUploads = new ArrayList<>();
                    ArrayList<Image> failUploads = new ArrayList<>();

                    result.getCreationResults().forEach((k, v) -> {
                        if (v.isOk()) {
                            successUploads.add(new Image(k, v.metadata, v.uri.get()));
                        } else {
                            failUploads.add(new Image(k, v.metadata));
                        }
                    });

                    if(successUploads.isEmpty()){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                    }

                    photo.setUri(successUploads.get(0).getMetadata().getUrl());
                    photoService.savePhoto(photo);

                    return ResponseEntity.ok(new Response(photo.getJson(successUploads.get(0).getUri()), new ArrayList<>()));
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Cound not upload image", e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
