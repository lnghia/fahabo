package com.example.demo.Family.Controller;

import com.example.demo.Album.Entity.Album;
import com.example.demo.Album.Entity.Image;
import com.example.demo.Album.Helper.AlbumHelper;
import com.example.demo.DropBox.*;
import com.example.demo.Helpers.*;
import com.example.demo.RequestForm.*;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Album.Service.Album.AlbumService;
import com.example.demo.Family.Service.Family.FamilyService;
import com.example.demo.Service.Role.RoleService;
import com.example.demo.UserInFamily.Service.UserInFamily.UserInFamilyService;
import com.example.demo.User.Service.UserService;
import com.example.demo.User.Entity.CustomUserDetails;
import com.example.demo.User.Entity.User;
import com.example.demo.Family.Entity.Family;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/families")
@Slf4j
public class FamilyControllers {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInFamilyService userInFamilyService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private AlbumHelper albumHelper;

    @Autowired
    private FamilyHelper familyHelper;

    @Autowired
    private MediaFileHelper mediaFileHelper;

    @PostMapping("/new_family")
    public ResponseEntity<Response> createFamily(@Valid @RequestBody CreateFamilyReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Date now = new Date();

        Family family = new Family(requestBody.familyName);
        familyService.saveFamily(family);

        Album newAlbum = albumHelper.createDefaultAlbum(family, now);

        family.setTimezone(requestBody.timezone);
        family.addAlbum(newAlbum);
        family.setDefaultAlbum(newAlbum);
        familyService.saveFamily(family);

        if (requestBody.ids != null) {
            familyHelper.addMember(requestBody.ids, family);
        }
        familyHelper.assignHost(user, family);

        HashMap<String, Object> data = new HashMap<>() {{
            put("alreadyHadFamily", (user.getUserInFamilies().size() > 1));
        }};

        if (requestBody.thumbnail != null && !requestBody.thumbnail.getBase64Data().isBlank() && !requestBody.thumbnail.getBase64Data().isEmpty() && requestBody.thumbnail.getBase64Data() != null) {
            requestBody.thumbnail.setName(familyService.generateImgUploadId(family.getId()));

            try {
                String thumbnailUri = familyHelper.saveThumbnail(requestBody.thumbnail, family);

                if (thumbnailUri == null) {
                    data.put("family", family.getJson(false));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(data, new ArrayList<>(List.of("upload.fail"))));
                }
            } catch (ExecutionException | InterruptedException e) {
                family.setThumbnail(Helper.getInstance().DEFAULT_FAMILY_THUMBNAIL);
                familyService.saveFamily(family);
                log.error("Threading exception while initializing client: " + e.getMessage());
                e.printStackTrace();
                data.put("family", family.getJson(false));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(data, new ArrayList<>(List.of("upload.fail"))));
            }
        } else {
            family.setThumbnail(Helper.getInstance().DEFAULT_FAMILY_THUMBNAIL);
            familyService.saveFamily(family);
        }
        data.put("family", family.getJson((requestBody.thumbnail != null)));

        return ResponseEntity.ok(new Response(data, new ArrayList<>()));
    }

    @PostMapping("/detail")
    public ResponseEntity<Response> familyDetail(@Valid @RequestBody FamilyDetailReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            return ResponseEntity.ok(new Response(family.getJson(true), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/users_in_family")
    public ResponseEntity<Response> getUsersInFamily(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                                     @Valid @RequestBody JoinFamilyReqForm requestBody) {

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            String searchText = (requestBody.searchText != null) ? requestBody.searchText : "";
            List<Integer> userIds = userInFamilyService.getUserIdsInFamily(family.getId(), searchText, page, size);
            List<User> users = userIds.stream().map(id -> {
                return userService.getUserById(id);
            }).collect(Collectors.toList());
            ArrayList<HashMap<String, Object>> data;

            try {
                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();

                GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(new ArrayList<>(users.stream().map(user1 -> {
                    return new Image(user1.getName(), user1.getAvatar());
                }).collect(Collectors.toList())));

                if (result != null) {
                    data = new ArrayList<>(users.stream()
                            .map(user1 -> {
                                return (result.getSuccessfulResults().containsKey(user1.getName())) ?
                                        user1.getShortJsonWithHost(result.getSuccessfulResults().get(user1.getName()).getUri(), familyService.isHostInFamily(user1.getId(), family.getId())) :
                                        user1.getShortJsonWithHost(null, familyService.isHostInFamily(user1.getId(), family.getId()));
                            }).collect(Collectors.toList()));

                    return ResponseEntity.ok(new Response(data, new ArrayList<>()));
                }

                return ResponseEntity.ok(new Response(users.stream().map(user1 -> user1.getShortJson(null)).collect(Collectors.toList()), new ArrayList<>()));
            } catch (ExecutionException | InterruptedException e) {
                log.error("Couldn't retrieve redirected url, unknown error.");
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(
                        users.stream().map(user1 -> user1.getShortJsonWithHost(null, familyService.isHostInFamily(user1.getId(), family.getId()))).collect(Collectors.toList()),
                        new ArrayList<>(List.of("avatar.unavailable"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_family")
    public ResponseEntity<Response> updateFamily(@Valid @RequestBody UpdateFamilyReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            if (requestBody.name != null && !requestBody.name.isEmpty() && !requestBody.name.isBlank()) {
                family.setFamilyName(requestBody.name);
            }
            familyService.saveFamily(family);

            return ResponseEntity.ok(new Response(family.getJson(true), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update_thumbnail")
    public ResponseEntity<Response> updateThumbnail(@Valid @RequestBody UploadFamilyThumbnailReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        if (family.checkIfUserExist(user)) {
            if (requestBody.thumbnail == null ||
                    requestBody.thumbnail.getBase64Data() == null ||
                    requestBody.thumbnail.getBase64Data().isBlank() ||
                    requestBody.thumbnail.getBase64Data().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("upload.invalidFile"))));

            }
            requestBody.thumbnail.setName(familyService.generateImgUploadId(family.getId()));

            try {
                String thumbnailUri = familyHelper.saveThumbnail(requestBody.thumbnail, family);

                if(thumbnailUri == null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
                }

                return ResponseEntity.ok(new Response(family.getJson(thumbnailUri), new ArrayList<>()));
            } catch (InterruptedException | ExecutionException e) {
                log.error("Threading exception while initializing client: " + e.getMessage());
                e.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
