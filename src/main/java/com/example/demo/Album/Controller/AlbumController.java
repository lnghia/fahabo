package com.example.demo.Album.Controller;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.Album.Entity.Album;
import com.example.demo.Album.Entity.AlbumsPhotos;
import com.example.demo.Album.Entity.Image;
import com.example.demo.Album.Entity.Photo;
import com.example.demo.Album.Helper.AlbumFamilyHelper;
import com.example.demo.Album.Helper.AlbumHelper;
import com.example.demo.DropBox.*;
import com.example.demo.Helpers.*;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Album.Service.Album.AlbumService;
import com.example.demo.Album.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Family.Service.Family.FamilyService;
import com.example.demo.Album.Service.Photo.PhotoService;
import com.example.demo.UserInFamily.Service.UserInFamily.UserInFamilyService;
import com.example.demo.User.Entity.CustomUserDetails;
import com.example.demo.User.Entity.User;
import com.example.demo.UserInFamily.Entity.UserInFamily;
import com.example.demo.Family.Entity.Family;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
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

    @Autowired
    private MediaFileHelper mediaFileHelper;

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

            return ResponseEntity.ok(new Response(albumHelper.getJson(newAlbum, null), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                new HashMap<String, String>(){{ put("familyName", family.getFamilyName()); }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_album")
    public ResponseEntity<Response> updateAlbum(@Valid @RequestBody UpdateAlbumReqForm requestBody) {
        Album album = albumService.findById(requestBody.albumId);
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserInFamily userInFamily = userInFamilyService.findByUserIdAndFamilyId(user.getId(), album.getFamily().getId());

        if (userInFamily != null) {
            Date now = new Date();

            if (requestBody.title != null && !requestBody.title.isEmpty() && !requestBody.title.isBlank() && !album.getTitle().equals(requestBody.title)) {
                if (!albumFamilyHelper.isAlbumTitleUniqueInFamily(album.getFamily().getId(), requestBody.title)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.albumTitleExists"))));
                }
                album.setTitle(requestBody.title);
            }

            if (requestBody.description != null && !requestBody.description.isBlank() && !requestBody.description.isEmpty()) {
                album.setDescription(requestBody.description);
            }
            album.setUpdatedAt(now);
            albumService.saveAlbum(album);

            String uri = albumService.getMostRecentImageUriInAlbum(album.getId());
            ArrayList<Image> images = new ArrayList<>();
            images.add(new Image(album.getTitle(), uri));
            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
            try {
                GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(images);
                return ResponseEntity.ok(new Response(
                        albumHelper.getJson(album, result.getSuccessfulResults().containsKey(album.getTitle()) ? result.getSuccessfulResults().get(album.getTitle()).getUri() : null),
                        new ArrayList<>())
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(new Response(albumHelper.getJson(album, null), new ArrayList<>()));
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

            return ResponseEntity.ok(new Response(albumHelper.getJson(album, null), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/add_photo")
    public ResponseEntity<Response> addPhoto(@Valid @RequestBody AddPhotoReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Album album = albumService.findById(requestBody.albumId);
        Family family = album.getFamily();
        Album defaultAlbum = family.getDefaultAlbum();

        if (userAlbumHelper.checkUserAlbumRelationship(user, album)) {
            List<Photo> photos = new ArrayList<>();

            try {
                Date now = new Date();

                for (int i = 0; i < requestBody.photos.size(); ++i) {
                    photos.add(new Photo(now, now));
                    photoService.savePhoto(photos.get(i));
                    AlbumsPhotos albumsPhotos = new AlbumsPhotos(album, photos.get(i));
                    albumsPhotosService.saveAlbumsPhotos(albumsPhotos);
                    photos.get(i).getPhotoInAlbums().add(albumsPhotos);
                    if(album.getId() != defaultAlbum.getId()){
                        AlbumsPhotos albumsPhotosDefault = new AlbumsPhotos(defaultAlbum, photos.get(i));
                        albumsPhotosService.saveAlbumsPhotos(albumsPhotosDefault);
                        photos.get(i).getPhotoInAlbums().add(albumsPhotosDefault);
                    }
                    photoService.savePhoto(photos.get(i));
                }

                UploadResult rs = mediaFileHelper.saveImages(requestBody.photos, (ArrayList<Photo>) photos, requestBody.albumId, album.getFamily().getId());
                ArrayList<Image> successUploads = rs.getSuccessUploads();

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
                    photo.setUri(item.getUri());
                    photoService.savePhoto(photo);

                    data.add(photo.getJsonWithRedirectedUri(item.getUri()));
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
                                              @Valid @RequestBody GetAlbumsReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            String searchText = (requestBody.searchText != null) ? requestBody.searchText : "";
            List<Album> albums = new ArrayList<>();
            if(page == 0){
                albums.add(family.getDefaultAlbum());
            }
            albums.addAll(albumService.findAllByFamilyIdWithPagination(family.getId(), family.getDefaultAlbum().getId(), searchText, page, (page == 0) ? size - 1 : size));

            HashMap<String, String> uris = new HashMap<>();
            ArrayList<Image> images = new ArrayList<>();
            for(var album : albums){
                String uri = albumService.getMostRecentImageUriInAlbum(album.getId());
                images.add(new Image(Integer.toString(album.getId()), uri));
            }
            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
            try {
                GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(images);

                return ResponseEntity.ok(new Response(albums.stream().map(
                        album -> {
                            String uri = result.getSuccessfulResults().containsKey(Integer.toString(album.getId())) ? result.getSuccessfulResults().get(Integer.toString(album.getId())).getUri() : null;
                            return albumHelper.getJson(album, uri);
                        }).collect(Collectors.toList()
                ), new ArrayList<>()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(new Response(albums.stream().map(
                    album -> {
                        return albumHelper.getJson(album, null);
                    }).collect(Collectors.toList()
            ), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                new HashMap<String, String>(){{ put("familyName", family.getFamilyName()); }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/preview_album")
    public ResponseEntity<Response> previewDefaultAlbum(@Valid @RequestBody PreviewDefaultAlbumReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);
        int defaultAlbumId = family.getDefaultAlbum().getId();

        if (family.checkIfUserExist(user)) {
            List<Integer> photoIds = albumService.get9LatestPhotosFromAlbum(defaultAlbumId);
            List<Photo> photos = photoIds.stream().map(photoId -> {
                return photoService.getById(photoId);
            }).collect(Collectors.toList());
            ArrayList<HashMap<String, Object>> data;

            try {
                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();

                GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(photos.stream().map(photo -> {
                    return new Image(photo.getName(), photo.getUri());
                }).collect(Collectors.toList())));

                if (executionResult != null) {
                    data = new ArrayList<>(photos.stream()
                            .map(photo -> {
                                return (executionResult.getSuccessfulResults().containsKey(photo.getName())) ?
                                        photo.getJsonWithRedirectedUri(executionResult.getSuccessfulResults().get(photo.getName()).getUri()) : photo.getJson(null);
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

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                new HashMap<String, String>(){{ put("familyName", family.getFamilyName()); }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping(value = "/add_video", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadVideo(@RequestPart("file") MultipartFile file) throws IOException {
        DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();

        try {
            DropBoxUploader uploader = new DropBoxUploader(clientV2);

            String uploadRootPath = "/home/nghiale/Downloads";
            File uploadRootDir = new File(uploadRootPath);
            // Tạo thư mục gốc upload nếu nó không tồn tại.
            if (!uploadRootDir.exists()) {
                uploadRootDir.mkdirs();
            }

            String name = file.getOriginalFilename();
            log.info(name);
            if (name != null && name.length() > 0) {
                try {
                    InputStream inputStream = new BufferedInputStream(file.getInputStream());

                    ItemToUpload[] itemsToUpload = new ItemToUpload[1];
                    itemsToUpload[0] = new ItemToUpload(name+"1.jpg", inputStream);

                    UploadExecutionResult result = uploader.uploadItems(itemsToUpload);

                    if (result == null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
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

                    if (successUploads.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
                    }

                    return ResponseEntity.ok(new Response(successUploads.get(0).getUri(), new ArrayList<>()));
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Error creating threads to execute upload images " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
                }
            }

            return ResponseEntity.ok(new Response(null, null));
        }
        catch (Exception e){

        }

        return ResponseEntity.ok(new Response(null, null));
    }
}