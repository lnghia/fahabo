package com.example.demo.Controllers;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.*;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.FamilyHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.Helpers.UserHelper;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Album.AlbumService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.Role.RoleService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.*;
import com.example.demo.domain.Family.Family;
import com.google.rpc.Help;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/users")
@Slf4j
public class UserController {
    @Autowired
    private UserHelper userHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserInFamilyService userInFamilyService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FamilyHelper familyHelper;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private FirebaseMessageHelper firebaseMessageHelper;

    @GetMapping
    private ResponseEntity<Response> getUsers(@RequestHeader("User-Agent") String userAgent) {
        List<User> users = userService.getUsers();
        List<Object> data = users.stream().map(user -> userHelper.UserToJson(user)).collect(Collectors.toList());

        log.info(userAgent);

        String token = "dRNAZti-R4K9jtHA1HC-b5:APA91bFdz08iVuV204qsTbWuVmKe8vastey-OmxCf27mN4WL91j7kUmgLJUfCzV-JOXS5FCxKvNTzaRqKaot5jTiKjqfqE-" +
                "FYTQ1MJhkBayS3R6DMM729G50n-YltN9BLdSxHt5mjlGbxS_o";

        String tmp = "eLge7rIu5UA1sAx-x6vzWq:APA91bG20weCjpg2DnymcxpnAGGDZwsLJOjNHeFVU2-MOtxcD6oc9eRYlqgSO26A-G33dj70x48UqWQ8KyFU6H-4yBz5XrrCDvC0oB_IM5dulJOfmSHdyZ07ayV6ymY-CYtmkHHrkR_p";

        firebaseMessageHelper.sendNotifications(List.of(token, tmp), "hihi", "haha");

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }

//    @GetMapping
//    private ResponseEntity<Response> saveUser(@RequestBody String name, @RequestBody String username, @RequestBody String password){
//        User newUser = new User(name, username, password);
//
//        return ResponseEntity.ok(new Response(userService.saveUser(newUser), ""));
//    }

    @GetMapping("/newuser")
    private ResponseEntity<Response> saveUser() {
//        User newUser = new User("t", "t", "12345");
        User user = new User();
        User user1 = new User();

        user.setPassword("123");
        user.setEmail("jkl@gmail.com");
        user.setLanguageCode("vi");
        user.setDeleted(false);

        user1.setDeleted(false);
        user1.setPassword("123");
        user.setPhoneNumber("000");
        user1.setLanguageCode("vi");

        userService.saveUser(user);

        return ResponseEntity.ok(new Response(userService.saveUser(user1), new ArrayList<>(List.of("user information invalid."))));
    }

    @PostMapping("/temp")
    private ResponseEntity<Response> temp(@Valid @RequestBody TempReqForm temp) {
        return ResponseEntity.ok(new Response(temp, new ArrayList<>()));
    }

    @PostMapping("/update_profile")
    private ResponseEntity<Response> updateProfile(@Valid @RequestBody UpdateProfileReqForm requestBody) throws ParseException {
//        log.debug(requestBody.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (requestBody.getEmail() != null) {
            user.setEmail(requestBody.getEmail());
            user.setUsername(requestBody.getEmail());
        }
        if (requestBody.getBirthday() != null) {
            user.setBirthday(formatter.parse(requestBody.getBirthday()));
        }
        if (requestBody.getPhoneNumber() != null) {
            user.setPhoneNumber(requestBody.getPhoneNumber());
        }
        if (requestBody.getName() != null) {
            user.setName(requestBody.getName());
        }
        user.setLanguageCode(requestBody.getLanguageCode());

        userService.updateUser(user);

        return ResponseEntity.ok(new Response(userHelper.UserToJson(user), new ArrayList<>()));
    }

    @PostMapping("/get_profile")
    public ResponseEntity<Response> getProfile(@Valid @RequestBody GetProfileReqForm requestBody) {
        User user = (requestBody.id == null) ? ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser() :
                userService.getUserById(requestBody.id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("user.doesNotExist"))));
        }

        return ResponseEntity.ok(new Response(userHelper.UserToJson(user), new ArrayList<>()));
    }

    @PostMapping("/preview_images")
    public ResponseEntity<Response> getPreviewImages(@Valid @RequestBody GetPreviewImagesReqForm requestBody) {
        Family family = familyService.findById(requestBody.familyId);
        int id = family.getDefaultAlbum().getId();
        List<Integer> itemIds = albumService.get9LatestPhotosFromAlbum(id);
        ArrayList<Photo> photos = new ArrayList<>();
        ArrayList<Image> images = new ArrayList<>();

        for (var item : itemIds) {
            Photo photo = photoService.getById(item);
            images.add(new Image(photo.getName(), photo.getUri()));
        }
        DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
        try {
            GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(images);
            return ResponseEntity.ok(new Response(
                    photos.stream().map(photo -> {
                        String uri = result.getSuccessfulResults().containsKey(photo.getName()) ? result.getSuccessfulResults().get(photo.getName()).getUri() : photo.getUri();
                        return new HashMap<String, String>() {{
                            put("uri", uri);
                        }};
                    }).collect(Collectors.toList()),
                    new ArrayList<>())
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(new Response(new ArrayList<>(List.of()), new ArrayList<>()));
    }

    @GetMapping("/avatar")
    public ResponseEntity<Response> getAvatar() {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        HashMap<String, Object> data = new HashMap<>() {{
            put("uri", userHelper.createShareLink(user.getAvatar()));
        }};

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }

    @PostMapping("/update_avatar")
    public ResponseEntity<Response> updateAvatar(@RequestBody UpdateAvatarReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        DbxClientV2 dbxClientV2 = dropBoxAuthenticator.authenticateDropBoxClient();

        DropBoxUploader uploader = new DropBoxUploader(dbxClientV2);

        requestBody.getAvatar().setName(userService.generateImgUploadId(user));

        try {
            UploadExecutionResult executionResult = uploader.uploadItems(Helper.getInstance().convertAImgToParaForUploadImgs(requestBody.getAvatar()));

            if (executionResult == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
            }

            HashMap<String, Object> data = new HashMap<>();
            ArrayList<Image> successUploads = new ArrayList<>();
            ArrayList<Image> failUploads = new ArrayList<>();

            executionResult.getCreationResults().forEach((k, v) -> {
                if (v.isOk()) {
                    successUploads.add(new Image(k, v.metadata, v.uri.get()));
                } else {
                    failUploads.add(new Image(k, v.metadata));
                }
            });

            if (successUploads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
            }

            user.setAvatar(successUploads.get(0).getMetadata().getUrl());
            userService.updateUser(user);

//            executionResult.getSuccessfulUploads().forEach((k, v) -> {
//                successUploads.add(new Image(v.getName(), v.getUri()));
//            });
//            executionResult.getFailedUploads().forEach((k, v) -> {
//                failUploads.add(new Image(v.getName(), v.getUri()));
//            });

            data.put("avatar", successUploads.get(0).toJson());
//            data.put("fail", failUploads.stream().map(Image::toJson).collect(Collectors.toList()));

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        } catch (InterruptedException | ExecutionException e) {
            log.error("Threading exception while initializing client: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
        }
    }

    @PostMapping("/join_family")
    public ResponseEntity<Response> joinFamily(@Valid @RequestBody JoinFamilyReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);
        Role role = roleService.findByName("MEMBER");
        Helper helper = Helper.getInstance();
        String langCode = (family.getTimezone() == null) ? "en" : ((family.getTimezone().equals("Asia/Ho_Chi_Minh") || family.getTimezone().equals("Asia/Saigon")) ? "vi" : "en");

        if (family.checkIfUserExist(user)) {
            HashMap<String, Object> data = new HashMap<>() {{
                put("family", family.getJson(!family.getThumbnail().equals(Helper.getInstance().DEFAULT_FAMILY_THUMBNAIL)));
                put("alreadyHadFamily", (user.getUserInFamilies().size() > 1));
            }};

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(data, new ArrayList<>(List.of("family.hasBeenJoined"))));
        }

        UserInFamily userInFamily = new UserInFamily(user, family);
        userInFamily.setRole(role);
        userInFamilyService.saveUserInFamily(userInFamily);
        user.addFamily(userInFamily);
        family.addUser(userInFamily);
        userService.updateUser(user);
        familyService.saveFamily(family);

        HashMap<String, Object> data = new HashMap<>() {{
            put("family", family.getJson(!family.getThumbnail().equals(Helper.getInstance().DEFAULT_FAMILY_THUMBNAIL)));
            put("alreadyHadFamily", (user.getUserInFamilies().size() > 1));
        }};

        List<User> usersToNotify = family.getUsersInFamily().stream().filter(userInFamily1 -> userInFamily1.getUserId() != user.getId()).map(userInFamily1 -> userInFamily1.getUser()).collect(Collectors.toList());

        HashMap<String, String> notiData = new HashMap<>() {{
            put("navigate", "FAMILY_DETAIL");
            put("id", Integer.toString(family.getId()));
        }};

        firebaseMessageHelper.notifyUsers(
                usersToNotify,
                helper.getMessageInLanguage("newMemberJoinedFamilyTitle", langCode),
                String.format(helper.getMessageInLanguage("newMemberJoinedFamilyBody", langCode), user.getName(), family.getFamilyName()),
                notiData
        );

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }

    @PostMapping("/leave_family")
    public ResponseEntity<Response> leaveFamily(@Valid @RequestBody JoinFamilyReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            if (family.getUsersInFamily().size() == 1) {
                familyHelper.deleteFamilyById(requestBody.familyId);
                return ResponseEntity.ok(new Response(userService.getUserById(user.getId()).getJson(), new ArrayList<>()));
            }
            if (userInFamilyService.hasRole(user, family, "HOST")) {
                UserInFamily newHost = family.getUsersInFamily().stream().filter(userInFamily1 -> {
                    return userInFamily1.getUser().getId() != user.getId();
                }).findAny().orElse(null);

                if (newHost != null) {
                    Role role = roleService.findByName("HOST");
                    User _user = userService.getUserById(newHost.getUserId());

                    newHost.setRole(role);
                    userInFamilyService.saveUserInFamily(newHost);
//                    log.info(newHost.getUser().getJson().toString());
//                    log.info(_user.getJson().toString());
                }
            }

            UserInFamily userInFamily = family.deleteUser(user);
            UserInFamily userInFamily1 = user.deleteFamily(family);
            userService.updateUser(user);
            familyService.saveFamily(family);
            if (userInFamily != null) {
                userInFamilyService.delete(userInFamily);
//                userInFamilyService.saveUserInFamily(userInFamily);
            }

            return ResponseEntity.ok(new Response(userService.getUserById(user.getId()).getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("family.leaveFamilyFailure"))));
    }

    @PostMapping("/kick_member")
    public ResponseEntity<Response> kickMember(@Valid @RequestBody KickMemberReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (userInFamilyService.hasRole(user, family, "HOST")) {
            User userToKick = userService.getUserById(requestBody.userIdToKick);

            if (family.checkIfUserExist(userToKick) && family.getUsersInFamily().size() > 1) {
                UserInFamily association = family.deleteUser(userToKick);
                userToKick.deleteFamily(family);
                userService.updateUser(userToKick);
                familyService.saveFamily(family);
                if (association != null) {
                    userInFamilyService.delete(association);
                }

                return ResponseEntity.ok(new Response("kicked successfully.", new ArrayList<>()));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("family.kickMemberFailure"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("family.kickMemberFailure"))));
    }

    @PostMapping("/get_families")
    public ResponseEntity<Response> getFamilies(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                                @RequestBody GetFamiliesReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<UserInFamily> userInFamilies = userInFamilyService.findAllByUserIdWithPagination(user.getId(),
                (requestBody.searchText != null) ? requestBody.searchText.toLowerCase() : "",
                page, size
        );
//        List<Family> families = user.getUserInFamilies().stream().map(UserInFamily::getFamily).collect(Collectors.toList());
        List<Family> families = userInFamilies.stream().map(UserInFamily::getFamily).collect(Collectors.toList());

        ArrayList<HashMap<String, Object>> data;
        try {
            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();

            GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(new ArrayList<>(families.stream().map(family -> {
                return new Image(family.getFamilyName(), family.getThumbnail());
            }).collect(Collectors.toList())));

            if (result != null) {
                data = new ArrayList<>(families.stream()
                        .map(family -> {
                            return (result.getSuccessfulResults().containsKey(family.getFamilyName())) ? family.getJson(result.getSuccessfulResults().get(family.getFamilyName()).getUri()) : family.getJson(null);
                        }).collect(Collectors.toList()));

                return ResponseEntity.ok(new Response(data, new ArrayList<>()));
            }

            return ResponseEntity.ok(new Response(families.stream().map(family -> family.getJson(null)).collect(Collectors.toList()), new ArrayList<>()));
        } catch (ExecutionException | InterruptedException e) {
            log.error("Couldn't retrieve redirected thumbnail urls, unknown error.");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(families.stream().map(family -> family.getJson(null)).collect(Collectors.toList()),
                    new ArrayList<>(List.of("unknownError"))));
        }
    }

    @PostMapping("/make_video_call")
    public ResponseEntity<Response> makeVideoCall(@RequestBody VideoCallReqForm reqForm){
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqForm.familyId);
        List<User> users = new ArrayList<>();
        Helper helper = Helper.getInstance();
        String langCode = helper.getLangCode(family);

        if(reqForm.participantIds == null){
            return ResponseEntity.ok(new Response(null, new ArrayList<>()));
        }

        if (family.checkIfUserExist(user)) {
            for(var id : reqForm.participantIds){
                User participant = userService.getUserById(id);
                if(family.checkIfUserExist(participant)){
                    users.add(participant);
                }
            }

            HashMap<String, String> data = new HashMap<>(){{
                put("navigate", "VIDEO_CALL");
                put("id", reqForm.roomCallId);
            }};

            if(reqForm.participantIds != null && reqForm.participantIds.length == 0){
                firebaseMessageHelper.notifyAllUsersInFamily(
                        family,
                        helper.getMessageInLanguage("invitedToACallTitle", langCode),
                        String.format(helper.getMessageInLanguage("invitedToACallBody", langCode), family.getFamilyName(), user.getName()),
                        data
                );
            }
            else{
                firebaseMessageHelper.notifyUsers(
                        users,
                        helper.getMessageInLanguage("invitedToACallTitle", langCode),
                        String.format(helper.getMessageInLanguage("invitedToACallBody", langCode), family.getFamilyName(), user.getName()),
                        data);
            }

            return ResponseEntity.ok(new Response(null, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
