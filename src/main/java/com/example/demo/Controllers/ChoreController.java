package com.example.demo.Controllers;

import com.example.demo.DropBox.*;
import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.RequestBody.GetEventDetailReqBody;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.ChoreHelper;
import com.example.demo.Helpers.Helper;
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
import com.example.demo.domain.Family.Family;
import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    private FirebaseMessageHelper firebaseMessageHelper;

    @PostMapping("/new_chore")
    public ResponseEntity<Response> createChore(@Valid @RequestBody CreateChoreReqForm requestBody) throws ParseException, ExecutionException, InterruptedException {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Date now = new Date();
        Family family = familyService.findById(requestBody.familyId);
        List<User> users = new ArrayList<>();
        Helper helper = Helper.getInstance();
        String langCode = (family.getTimezone() == null) ? "en" : ((family.getTimezone().equals("Asia/Ho_Chi_Minh") || family.getTimezone().equals("Asia/Saigon")) ? "vi" : "en");

        if (family.checkIfUserExist(user)) {
            int photosNum = (requestBody.photos != null) ? requestBody.photos.length : 0;
            if (choreHelper.isPhotoNumExceedLimitChore(photosNum, null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.photosNumInChoreExceeded"))));
            }

            Chore chore = new Chore();
            choreService.saveChore(chore);
            chore.setStatus(requestBody.status);
            chore.setReporter(user);

            if (requestBody.title != null && !requestBody.title.isBlank() && !requestBody.title.isEmpty()) {
                chore.setTitle(requestBody.title);
            }
            if (requestBody.repeatType != null && !requestBody.repeatType.isBlank() && !requestBody.repeatType.isEmpty()) {
                chore.setRepeatType(requestBody.repeatType);
            }
            if (requestBody.description != null && !requestBody.description.isEmpty() && !requestBody.description.isBlank()) {
                chore.setDescription(requestBody.description);
            }
            if (requestBody.deadline != null && !requestBody.deadline.isBlank() && !requestBody.deadline.isEmpty()) {
                chore.setDeadline(Helper.getInstance().formatDateWithoutTime(requestBody.deadline));
            }
            if (requestBody.assigneeIds != null && requestBody.assigneeIds.length != 0) {
                users = choreHelper.assignUser(requestBody.assigneeIds, chore);
            } else {
                choreHelper.assignUser(new int[]{user.getId()}, chore);
            }
            chore.setCreatedAt(now);
            chore.setUpdatedAt(now);

            if (requestBody.photos != null && requestBody.photos.length != 0) {
                ChoreAlbum choreAlbum = new ChoreAlbum();
                choreAlbum.setChore(chore);
                choreAlbumService.saveChoreAlbum(choreAlbum);
                chore.getChoreAlbumSet().add(choreAlbum);

                ItemToUpload[] items = new ItemToUpload[requestBody.photos.length];
                HashMap<String, Photo> newPhotos = new HashMap<>();

                for (int i = 0; i < requestBody.photos.length; ++i) {
                    Photo photo = new Photo();
                    PhotoInChore photoInChore = new PhotoInChore();

                    photo.setCreatedAt(now);
                    photo.setUpdatedAt(now);

                    photoService.savePhoto(photo);
                    photoInChore.setPhoto(photo);
                    photoInChore.setAlbum(choreAlbum);
                    photoInChoreService.savePhotoInChore(photoInChore);
                    photo.setName(Helper.getInstance().generatePhotoNameToUploadToAlbum(
                            family.getId(),
                            choreAlbum.getId(),
                            photo.getId()));
                    photoService.savePhoto(photo);
                    newPhotos.put(photo.getName(), photo);

                    items[i] = new ItemToUpload(photo.getName(), requestBody.photos[i]);
                }

                UploadResult result = dropBoxHelper.uploadImages(items, choreAlbum.getId(), 1);
                ArrayList<Image> success = result.successUploads;

                for (var image : success) {
                    Photo photo = newPhotos.get(image.getName());
                    photo.setUri(image.getMetadata().getUrl());
                    photoService.savePhoto(photo);
                }
            }
            family.getChores().add(chore);
            chore.setFamily(family);
            familyService.saveFamily(family);

            if(!users.isEmpty()){
                firebaseMessageHelper.notifyUsers(
                        users,
                        helper.getMessageInLanguage("choreHasBeenAssignedTitle", langCode),
                        String.format(helper.getMessageInLanguage("choreHasBeenAssignedBody", langCode), user.getName()),
                        new HashMap<String, String>() {{
                            put("navigate", "CHORE_DETAIL");
                            put("id", Integer.toString(chore.getId()));
                        }});
            }

            return ResponseEntity.ok(new Response(chore.getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping
    public ResponseEntity<Response> getChores(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                              @Valid @RequestBody GetChoresReqForm requestBody,
                                              @RequestHeader("User-Agent") String userAgent) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        log.info("Device", userAgent);

        if (family.checkIfUserExist(user)) {
            List<User> users = (requestBody.assigneeIds != null) ? requestBody.assigneeIds.stream().filter(id -> {
                return userService.getUserById(id) != null;
            }).map(id -> {
                return userService.getUserById(id);
            }).collect(Collectors.toList()) : null;

            ArrayList<Chore> chores = null;
            try {
                chores = choreService.findAll(
                        (requestBody.assigneeIds != null) ? users : List.of(),
                        family,
                        (requestBody.statuses != null) ? requestBody.statuses : List.of(),
                        (requestBody.searchText != null) ? requestBody.searchText : "",
//                        (requestBody.sortBy.equals("deadline")),
                        (requestBody.sortBy != null) && requestBody.sortBy.equals("deadline"),
                        (requestBody.from != null) ? Helper.getInstance().formatDateWithoutTime(requestBody.from) : null,
                        (requestBody.to != null) ? Helper.getInstance().formatDateWithoutTime(requestBody.to) : null,
                        page,
                        size
                );

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(family.getTimezone()));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                for (var chore : chores) {
                    if (!chore.getStatus().equals("DONE") && chore.getDeadline().before(calendar.getTime())) {
                        chore.setStatus("EXPIRED");
                        choreService.saveChore(chore);
                    }
                }

                return ResponseEntity.ok(new Response(chores.stream().map(chore -> {
                    return choreHelper.getJson(family.getId(), chore);
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
        HashSet<Integer> assignedUsers = new HashSet<>();
        Helper helper = Helper.getInstance();
        String langCode = (chore.getFamily().getTimezone() == null) ? "en" : ((chore.getFamily().getTimezone().equals("Asia/Ho_Chi_Minh") || chore.getFamily().getTimezone().equals("Asia/Saigon")) ? "vi" : "en");

        if (chore == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.choreIdNotExist"))));
        }
        int photosNum = (requestBody.photos != null) ? requestBody.photos.length : 0;
        if (choreHelper.isPhotoNumExceedLimitChore(photosNum, chore)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.photosNumInChoreExceeded"))));
        }


        if (chore.getFamily().checkIfUserExist(user)) {
            List<User> users = new ArrayList<>();

            if (requestBody.status != null && !requestBody.status.isEmpty() && !requestBody.status.isBlank()) {
                String repeat = chore.getRepeatType();

                if (repeat != null && !repeat.isBlank() && !repeat.isEmpty() && requestBody.status.equals("DONE") && !chore.getStatus().equals("DONE")) {
                    Chore repeatChore = new Chore(
                            chore.getFamily(),
                            "IN_PROGRESS",
                            chore.getTitle(),
                            chore.getDescription(),
                            choreHelper.getNewDeadline(new Date(), chore.getRepeatType()),
                            chore.getReporter(),
                            chore.getRepeatType(),
                            false);
                    repeatChore.setCreatedAt(chore.getCreatedAtAsDate());
                    repeatChore.setUpdatedAt(chore.getUpdatedAtAsDate());
                    choreService.saveChore(repeatChore);
                    int[] ids = new int[chore.getChoresAssignUsers().size()];
                    Iterator<ChoresAssignUsers> iterator = chore.getChoresAssignUsers().iterator();
                    for (int i = 0; i < chore.getChoresAssignUsers().size(); ++i) {
                        ids[i] = iterator.next().getAssignee().getId();
                    }
                    choreHelper.assignUser(ids, repeatChore);

                    if (!chore.getChoreAlbumSet().isEmpty()) {
                        ChoreAlbum choreAlbum = new ChoreAlbum(repeatChore);
                        choreAlbumService.saveChoreAlbum(choreAlbum);
                        ArrayList<PhotoInChore> photoInChore = photoInChoreService.findAllByChoreAlbumId(chore.getChoreAlbumSet().iterator().next().getId());
                        for (var item : photoInChore) {
                            PhotoInChore newOne = new PhotoInChore();
                            newOne.setAlbum(choreAlbum);
                            newOne.setPhoto(item.getPhoto());
                            photoInChoreService.savePhotoInChore(newOne);
                        }
                        repeatChore.getChoreAlbumSet().add(choreAlbum);
                        choreService.saveChore(repeatChore);
                    }
                }
                chore.setStatus(requestBody.status);
            }
            if (requestBody.title != null && !requestBody.title.isBlank() && !requestBody.status.isBlank()) {
                chore.setTitle(requestBody.title);
            }
            if (requestBody.description != null && !requestBody.description.isBlank() && !requestBody.description.isEmpty()) {
                chore.setDescription(requestBody.description);
            }
            if (requestBody.repeatType != null) {
                chore.setRepeatType(requestBody.repeatType);
            }
            if (requestBody.deadline != null && !requestBody.deadline.isEmpty() && !requestBody.deadline.isBlank()) {
                chore.setDeadline(Helper.getInstance().formatDateWithoutTime(requestBody.deadline));
                chore.setStatus("IN_PROGRESS");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone(chore.getFamily().getTimezone()));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                if (Helper.getInstance().formatDateWithoutTime(requestBody.deadline).before(calendar.getTime())) {
                    chore.setStatus("EXPIRED");
                }
            }
            if (requestBody.assigneeIds != null) {
                chore.getChoresAssignUsers().forEach(choresAssignUsers -> {
                    assignedUsers.add(choresAssignUsers.getUserId());
                    choresAssignUsers.setDeleted(true);
                    choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
                });
                for (var id : requestBody.assigneeIds) {
                    User assignee = userService.getUserById(id);
                    if(!assignedUsers.contains(id)){
                        users.add(assignee);
                    }
                    ChoresAssignUsers choresAssignUsers = new ChoresAssignUsers();
                    choresAssignUsers.setChore(chore);
                    choresAssignUsers.setAssignee(assignee);
                    choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
                }
            }

            ArrayList<String> uris = new ArrayList<>();

            if (requestBody.photos != null && requestBody.photos.length > 0) {
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
                ItemToUpload[] items = new ItemToUpload[requestBody.photos.length];
                HashMap<String, Photo> newPhotos = new HashMap<>();

                for (int i = 0; i < requestBody.photos.length; ++i) {
                    Photo photo = new Photo();
                    PhotoInChore photoInChore = new PhotoInChore();

                    photo.setCreatedAt(now);
                    photo.setUpdatedAt(now);

                    photoService.savePhoto(photo);
                    photoInChore.setPhoto(photo);
                    photoInChore.setAlbum(choreAlbum);
                    photoInChoreService.savePhotoInChore(photoInChore);
                    photo.setName(Helper.getInstance().generatePhotoNameToUploadToAlbum(
                            chore.getFamily().getId(),
                            choreAlbum.getId(),
                            photo.getId()));
                    photoService.savePhoto(photo);
                    newPhotos.put(photo.getName(), photo);

                    items[i] = new ItemToUpload(photo.getName(), requestBody.photos[i]);
                }

                UploadResult result = dropBoxHelper.uploadImages(items, choreAlbum.getId(), 1);
                ArrayList<Image> success = result.successUploads;

                for (var image : success) {
                    Photo photo = newPhotos.get(image.getName());
                    photo.setUri(image.getMetadata().getUrl());
                    photoService.savePhoto(photo);
                    uris.add(image.getUri());
                }
            }
            if (requestBody.deletePhotos != null && requestBody.deletePhotos.length > 0) {
                ChoreAlbum choreAlbum = chore.getChoreAlbumSet().iterator().next();
                for (var id : requestBody.deletePhotos) {
                    Photo photo = photoService.getById(id);
                    PhotoInChore photoInChore = photoInChoreService.getPhotoInChoreByAlbumIdAndPhotoId(choreAlbum.getId(), id);
                    photoInChore.setDeleted(true);
                    photo.setDeleted(true);
                    photoInChoreService.savePhotoInChore(photoInChore);
                    photoService.savePhoto(photo);
                }
            }
            String timezone = chore.getFamily().getTimezone();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone(timezone));
            chore.setUpdatedAt(calendar.getTime());
            choreService.saveChore(chore);

            HashMap<String, Object> data = chore.getJson();
            data.put("photos", uris.stream().map(s -> {
                return new HashMap() {{
                    put("uri", s);
                }};
            }).collect(Collectors.toList()));

            if(!users.isEmpty()){
                firebaseMessageHelper.notifyUsers(
                        users,
                        helper.getMessageInLanguage("choreHasBeenAssignedTitle", langCode),
                        String.format(helper.getMessageInLanguage("choreHasBeenAssignedBody", langCode), user.getName()),
                        new HashMap<String, String>() {{
                            put("navigate", "CHORE_DETAIL");
                            put("id", Integer.toString(chore.getId()));
                        }});
            }

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
                return ResponseEntity.ok(new Response(List.of(), new ArrayList<>()));
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

    @PostMapping("/detail")
    private ResponseEntity<Response> getEventDetail(@RequestBody GetChoreDetailReqForm reqForm) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Helper helper = Helper.getInstance();
        Chore chore = choreService.getById(reqForm.choreId);

        if(chore == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.choreIdNotExist"))));
        }

        if (chore.getFamily().checkIfUserExist(user)) {
            return ResponseEntity.ok(new Response(choreHelper.getJson(chore.getFamily().getId(), chore), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
