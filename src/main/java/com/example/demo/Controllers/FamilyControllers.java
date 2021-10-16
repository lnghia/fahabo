package com.example.demo.Controllers;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.DropBoxAuthenticator;
import com.example.demo.DropBox.DropBoxUploader;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.Helpers.FamilyHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.RequestForm.CreateFamilyReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/families")
@Slf4j
public class FamilyControllers {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private FamilyService familyService;

    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

    @Autowired
    private FamilyHelper familyHelper;

    @Transactional
    @PostMapping("/new_family")
    public ResponseEntity<Response> createFamily(@Valid @RequestBody CreateFamilyReqForm requestBody) {
        ArrayList<User> users = new ArrayList<>();

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Family family = new Family(requestBody.familyName);

        requestBody.ids.forEach(id -> {
            User tmpUser = familyHelper.getUserService().getUserById(id);
            users.add(tmpUser);
        });
        users.add(user);

        familyHelper.createFamily(family);
        familyHelper.addMember(family, users);

        if (requestBody.thumbnail != null) {
            DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();

            DropBoxUploader uploader = new DropBoxUploader(clientV2);

            requestBody.thumbnail.setName(familyHelper.generateImgUploadId(family));

            try {
                UploadExecutionResult executionResult = uploader.uploadItems(Helper.getInstance().convertAImgToParaForUploadImgs(requestBody.thumbnail));

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

                family.setThumbnail(successUploads.get(0).getMetadata().getUrl());
                familyHelper.getFamilyService().updateFamily(family);
            } catch (ExecutionException | InterruptedException e) {
                log.error("Threading exception while initializing client: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("upload.fail"))));
            }
        }
        else {
            family.setThumbnail(familyHelper.defaultThumbnail);
        }
//            data.put("fail", failUploads.stream().map(Image::toJson).collect(Collectors.toList()));

        return ResponseEntity.ok(new Response(family.getJson((requestBody.thumbnail != null)), new ArrayList<>()));
    }
}
