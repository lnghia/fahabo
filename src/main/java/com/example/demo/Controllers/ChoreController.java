package com.example.demo.Controllers;

import com.example.demo.DropBox.*;
import com.example.demo.Helpers.ChoreHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.Repo.ChoreRepo;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Chore.ChoreService;
import com.example.demo.Service.ChoreAlbum.ChoreAlbumService;
import com.example.demo.Service.ChoresAssignUsers.ChoresAssignUsersService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.PhotoInChore.PhotoInChoreService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.*;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/chores")
@Slf4j
public class ChoreController {
    @Autowired
    private ChoreService choreService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChoreHelper choreHelper;

    @Autowired
    private DropBoxHelper dropBoxHelper;

    @Autowired
    private ChoreAlbumService choreAlbumService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoInChoreService photoInChoreService;

    @Autowired
    private ChoresAssignUsersService choresAssignUsersService;

    @PostMapping("/new_chore")
    public ResponseEntity<Response> createChore(@Valid @RequestBody CreateChoreReqForm requestBody) throws ParseException, ExecutionException, InterruptedException {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Date now = new Date();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            Chore chore = new Chore();
            choreService.saveChore(chore);
            chore.setStatus(requestBody.status);
            chore.setReporter(user);
            if (requestBody.title != null && !requestBody.title.isBlank() && !requestBody.title.isEmpty()) {
                chore.setTitle(requestBody.title);
            }
            if (requestBody.description != null && !requestBody.description.isEmpty() && !requestBody.description.isBlank()) {
                chore.setDescription(requestBody.description);
            }
            if (requestBody.deadline != null && !requestBody.deadline.isBlank() && !requestBody.deadline.isEmpty()) {
                chore.setDeadline(Helper.getInstance().formatDateWithoutTime(requestBody.deadline));
            }
            if (requestBody.assigneeIds != null) {
                choreHelper.assignUser(requestBody.assigneeIds, chore);
            } else {
                choreHelper.assignUser(new int[]{user.getId()}, chore);
            }
            chore.setCreatedAt(now);
            chore.setUpdatedAt(now);

            if (requestBody.photos != null) {
                ChoreAlbum choreAlbum = new ChoreAlbum();
                choreAlbum.setChore(chore);
                choreAlbumService.saveChoreAlbum(choreAlbum);
                chore.getChoreAlbumSet().add(choreAlbum);

                UploadResult result = dropBoxHelper.uploadImages(requestBody.photos, choreAlbum.getId(), 1);

                result.successUploads.forEach(image -> {
                    Photo photo = new Photo();
                    PhotoInChore photoInChore = new PhotoInChore();

                    photo.setName(image.getName());
                    photo.setUri(image.getMetadata().getUrl());
                    photo.setCreatedAt(now);
                    photo.setUpdatedAt(now);
                    photoService.savePhoto(photo);
                    photoInChore.setPhoto(photo);
                    photoInChore.setAlbum(choreAlbum);
                    photoInChoreService.savePhotoInChore(photoInChore);
                });
            }
            family.getChores().add(chore);
            chore.setFamily(family);
            familyService.saveFamily(family);

            return ResponseEntity.ok(new Response(chore.getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping
    public ResponseEntity<Response> getChores(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                              @Valid @RequestBody GetChoresReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            List<User> users = (requestBody.assigneeIds != null) ? requestBody.assigneeIds.stream().filter(id -> {
                return userService.getUserById(id) != null;
            }).map(id -> {
                return userService.getUserById(id);
            }).collect(Collectors.toList()) : null;

            ArrayList<Chore> chores = null;
            try {
                chores = choreService.findAll(
                        users,
                        family,
                        requestBody.statuses,
                        requestBody.searchText,
//                        (requestBody.sortBy.equals("deadline")),
                        requestBody.sortBy.equals("deadline"),
                        (requestBody.from != null) ? Helper.getInstance().formatDateWithoutTime(requestBody.from) : null,
                        (requestBody.to != null) ? Helper.getInstance().formatDateWithoutTime(requestBody.to) : null,
                        page,
                        size
                );

                return ResponseEntity.ok(new Response(chores.stream().map(chore -> {
                    return chore.getJson();
                }).collect(Collectors.toList()), new ArrayList<>()));
            } catch (ParseException e) {
                log.error("Couldn't parse date in /chores.", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.dateFormatInvalid"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/delete_chore")
    public ResponseEntity<Response> deleteChore(@Valid @RequestBody DeleteChoreReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Chore chore = choreService.getById(requestBody.choreId);

        if (chore == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.choreIdNotExist"))));
        }

        if (chore.getFamily().checkIfUserExist(user)) {
            chore.getChoresAssignUsers().forEach(choresAssignUsers -> {
                choresAssignUsers.setDeleted(true);
                choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
            });
            chore.setDeleted(true);
            choreService.saveChore(chore);

            return ResponseEntity.ok(new Response(requestBody.choreId, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_chore")
    public ResponseEntity<Response> updateChore(@Valid @RequestBody UpdateChoreReqForm requestBody) throws ParseException, ExecutionException, InterruptedException {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Chore chore = choreService.getById(requestBody.choreId);

        if (chore == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.choreIdNotExist"))));
        }

        if (chore.getFamily().checkIfUserExist(user)) {
            if (requestBody.status != null && !requestBody.status.isEmpty() && !requestBody.status.isBlank()) {
                chore.setStatus(requestBody.status);
            }
            if (requestBody.title != null && !requestBody.title.isBlank() && !requestBody.status.isBlank()) {
                chore.setTitle(requestBody.title);
            }
            if (requestBody.description != null && !requestBody.description.isBlank() && !requestBody.description.isEmpty()) {
                chore.setDescription(requestBody.description);
            }
            if (requestBody.deadline != null && !requestBody.deadline.isEmpty() && !requestBody.deadline.isBlank()) {
                chore.setDeadline(Helper.getInstance().formatDate(requestBody.deadline));
            }
            if (requestBody.assigneeIds != null) {
                chore.getChoresAssignUsers().forEach(choresAssignUsers -> {
                    choresAssignUsers.setDeleted(true);
                    choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
                });
                for (var id : requestBody.assigneeIds) {
                    ChoresAssignUsers choresAssignUsers = new ChoresAssignUsers();
                    choresAssignUsers.setChore(chore);
                    choresAssignUsers.setAssignee(userService.getUserById(id));
                    choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
                }
            }

            ArrayList<String> uris = new ArrayList<>();

            if (requestBody.photos != null) {
                ChoreAlbum choreAlbum;
                boolean hasNoAlbum = chore.getChoreAlbumSet().isEmpty();
                if (hasNoAlbum) {
                    choreAlbum = new ChoreAlbum();
                    choreAlbum.setChore(chore);
                    choreAlbumService.saveChoreAlbum(choreAlbum);
                    chore.getChoreAlbumSet().add(choreAlbum);
                } else {
                    choreAlbum = chore.getChoreAlbumSet().iterator().next();
                }
                Date now = new Date();
                UploadResult result = dropBoxHelper.uploadImages(requestBody.photos, choreAlbum.getId(), 1);

                result.successUploads.forEach(image -> {
                    Photo photo = new Photo();
                    PhotoInChore photoInChore = new PhotoInChore();

                    photo.setName(image.getName());
                    photo.setUri(image.getMetadata().getUrl());
                    photo.setCreatedAt(now);
                    photo.setUpdatedAt(now);
                    photoService.savePhoto(photo);
                    photoInChore.setPhoto(photo);
                    photoInChore.setAlbum(choreAlbum);
                    photoInChoreService.savePhotoInChore(photoInChore);

                    uris.add(image.getUri());
                });
            }
            chore.setUpdatedAt(new Date());
            choreService.saveChore(chore);

            HashMap<String, Object> data = chore.getJson();
            data.put("photos", uris.stream().map(s -> {
                return new HashMap() {{
                    put("uri", s);
                }};
            }).collect(Collectors.toList()));

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/get_chore_photos")
    public ResponseEntity<Response> getChorePhotos(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                                   @Valid @RequestBody GetChorePhotoReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Chore chore = choreService.getById(requestBody.choreId);

        if (chore.getFamily().checkIfUserExist(user)) {
            if (chore.getChoreAlbumSet().isEmpty()) {
                return ResponseEntity.ok(new Response(new HashMap<String, Object>() {{
                    put("photos", List.of());
                }}, new ArrayList<>()));
            }
            ChoreAlbum choreAlbum = chore.getChoreAlbumSet().iterator().next();
            ArrayList<PhotoInChore> photoInChores = photoInChoreService.findAllByChoreAlbumId(choreAlbum.getId(), page, size);
            List<Integer> photoIds = photoInChores.stream().map(photoInChore -> {
                return photoInChore.getPhotoId();
            }).collect(Collectors.toList());
            List<Photo> photos = photoIds.stream().map(photoId -> {
                return photoService.getById(photoId);
            }).collect(Collectors.toList());
            ArrayList<HashMap<String, Object>> data;

            try {
//                DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();
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
}
