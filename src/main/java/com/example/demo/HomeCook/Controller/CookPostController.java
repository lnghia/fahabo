package com.example.demo.HomeCook.Controller;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.*;
import com.example.demo.HomeCook.Entity.CookPost;
import com.example.demo.HomeCook.Entity.CookPostPool;
import com.example.demo.HomeCook.Entity.UserReactCookPost;
import com.example.demo.HomeCook.RequestBody.CreateCuisinePostReqBody;
import com.example.demo.HomeCook.RequestBody.DeleteCuisinePostReqBody;
import com.example.demo.HomeCook.RequestBody.GetAllCookPostReqBody;
import com.example.demo.HomeCook.RequestBody.VoteReqBody;
import com.example.demo.HomeCook.Service.CookPostPoolService;
import com.example.demo.HomeCook.Service.CookPostService;
import com.example.demo.HomeCook.Service.UserReactCookPostService;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/cuisine_posts")
@Slf4j
public class CookPostController {
    @Autowired
    private DropBoxAuthenticator dropBoxAuthenticator;

    @Autowired
    private DropBoxHelper dropBoxHelper;

    @Autowired
    private CookPostService cookPostService;

    @Autowired
    private UserReactCookPostService userReactCookPostService;

    @Autowired
    private CookPostPoolService cookPostPoolService;

    @PostMapping("/create")
    public ResponseEntity<Response> createPost(@RequestBody CreateCuisinePostReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Date now = new Date();

        CookPost cookPost = new CookPost(reqBody.title, user, now, now);
        cookPostService.save(cookPost);
        try {
            cookPost.setContent(reqBody.content);
            cookPostService.save(cookPost);
        } catch (IOException e) {
            log.error("Error trying to save post content!");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
        }

        UserReactCookPost userReactCookPost = new UserReactCookPost(user, cookPost);
        userReactCookPost.setReaction(4);
        userReactCookPostService.save(userReactCookPost);

        ItemToUpload[] items = new ItemToUpload[1];
        items[0] = new ItemToUpload(cookPost.getId() + "_cuisine" + "_" + now.getTime() + ".jpg", reqBody.thumbnail);

        String thumbnailUri = null;
        try {
            UploadResult result = dropBoxHelper.uploadImages(items, 0, 1);
            ArrayList<Image> success = result.successUploads;

            if (!success.isEmpty()) {
                thumbnailUri = success.get(0).getUri();
                cookPost.setThumbnail(success.get(0).getMetadata().getUrl());
                cookPostService.save(cookPost);
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Couldn't upload cuisine post thumbnail.");
            e.printStackTrace();
        }

        String avatar = null;
        try {
            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
            GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(
                    List.of(new Image(user.getName(), user.getAvatar()))
            ));

            if (executionResult != null && !executionResult.getSuccessfulResults().isEmpty()) {
                avatar = executionResult.getSuccessfulResults().get(user.getName()).getUri();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Couldn't get author avatar ready to view url.");
            e.printStackTrace();
        }

        CookPostPool cookPostPool = new CookPostPool();
        cookPostPool.setUsers(user);
        cookPostPool.setCookposts(cookPost);
        cookPostPool.setAddedDate(now);
        cookPostPoolService.save(cookPostPool);

        return ResponseEntity.ok(new Response(cookPost.getJson(thumbnailUri, avatar, 0, "Asia/Saigon"), new ArrayList<>()));
    }

    @PostMapping("/update")
    public ResponseEntity<Response> updatePost(@RequestBody CreateCuisinePostReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Date now = new Date();

        CookPost cookPost = cookPostService.findById(reqBody.cuisinePostId);
        String thumbnailUri = null;

        if (cookPost != null) {
            if (cookPost.getAuthor().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
            }

            if (reqBody.content != null) {
                try {
                    cookPost.setContent(reqBody.content);
                } catch (IOException e) {
                    log.error("Error trying to save post content!");
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                }
            }
            if (reqBody.title != null && !reqBody.title.equals(cookPost.getTitle())) {
                cookPost.setTitle(reqBody.title);
            }
            if (reqBody.thumbnail != null) {
                ItemToUpload[] items = new ItemToUpload[1];
                items[0] = new ItemToUpload(cookPost.getId() + "_cuisine" + "_" + now.getTime() + ".jpg", reqBody.thumbnail);
                try {
                    UploadResult result = dropBoxHelper.uploadImages(items, 0, 1);
                    ArrayList<Image> success = result.successUploads;

                    if (!success.isEmpty()) {
                        thumbnailUri = success.get(0).getUri();
                        cookPost.setThumbnail(success.get(0).getMetadata().getUrl());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Couldn't upload cuisine post thumbnail.");
                    e.printStackTrace();
                }
            }
            cookPost.setUpdatedAt(now);
            cookPostService.save(cookPost);

            String avatar = null;
            try {
                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
                GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(
                        List.of(new Image(user.getName(), user.getAvatar()))
                ));

                if (executionResult != null && !executionResult.getSuccessfulResults().isEmpty()) {
                    avatar = executionResult.getSuccessfulResults().get(user.getName()).getUri();
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Couldn't get author avatar ready to view url.");
                e.printStackTrace();
            }

            UserReactCookPost userReactCookPost = userReactCookPostService.findByUserAndPost(user.getId(), reqBody.cuisinePostId);
            int reactionType = (userReactCookPost != null) ? userReactCookPost.getReaction() : 0;

            return ResponseEntity.ok(new Response(cookPost.getJson(thumbnailUri, avatar, reactionType, "Asia/Saigon"), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.cuisinePostNotExist"))));
    }

    @PostMapping("/vote")
    public ResponseEntity<Response> vote(@RequestBody VoteReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        CookPost cookPost = cookPostService.findById(reqBody.cuisinePostId);
        UserReactCookPost userReactCookPost = userReactCookPostService.findByUserAndPost(user.getId(), reqBody.cuisinePostId);

        if (userReactCookPost != null) {
            userReactCookPost.setDeleted(true);
            userReactCookPostService.save(userReactCookPost);
        }
        if (reqBody.voteId != null && reqBody.voteId != 0) {
            UserReactCookPost newReaction = new UserReactCookPost(user, cookPost);
            newReaction.setReaction(reqBody.voteId);
            userReactCookPostService.save(newReaction);
        }

        String avatar = null;
        String thumbnail = null;
        try {
            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
            GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(
                    List.of(new Image(user.getName(), user.getAvatar()),
                            new Image(Integer.toString(cookPost.getId()), cookPost.getThumbnail()))
            ));

            if (executionResult != null && !executionResult.getSuccessfulResults().isEmpty()) {
                avatar = (executionResult.getSuccessfulResults().containsKey(user.getName())) ? executionResult.getSuccessfulResults().get(user.getName()).getUri() : null;
                thumbnail = (executionResult.getSuccessfulResults().containsKey(Integer.toString(cookPost.getId()))) ?
                        executionResult.getSuccessfulResults().get(Integer.toString(cookPost.getId())).getUri() : null;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Couldn't get author avatar ready to view url.");
            e.printStackTrace();
        }

        userReactCookPost = userReactCookPostService.findByUserAndPost(user.getId(), reqBody.cuisinePostId);
        int reactionType = (userReactCookPost != null) ? userReactCookPost.getReaction() : 0;

        return ResponseEntity.ok(new Response(cookPost.getJson(thumbnail, avatar, reactionType, "Asia/Saigon"), new ArrayList<>()));
    }

    @PostMapping("/delete")
    public ResponseEntity<Response> delete(@Valid @RequestBody DeleteCuisinePostReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        CookPost cookPost = cookPostService.findById(reqBody.cuisinePostId);
        ArrayList<UserReactCookPost> userReactCookPost = userReactCookPostService.findByPost(reqBody.cuisinePostId);
        Date now = new Date();

        if (cookPost != null) {
            if (cookPost.getAuthor().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
            }
            cookPost.setDeleted(true);
            cookPost.setUpdatedAt(now);
            cookPostService.save(cookPost);
            if (userReactCookPost != null && !userReactCookPost.isEmpty()) {
                for (var item : userReactCookPost) {
                    item.setDeleted(true);
                    userReactCookPostService.save(item);
                }
            }

            return ResponseEntity.ok(new Response(
                    new HashMap<String, Object>() {{
                        put("cuisinePostId", reqBody.cuisinePostId);
                    }},
                    new ArrayList<>()
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.cuisinePostNotExist"))));
    }

    @PostMapping
    public ResponseEntity<Response> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", defaultValue = "5") Integer size,
                                           @RequestBody GetAllCookPostReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        ArrayList<CookPost> cookPosts = new ArrayList<>();

        ArrayList<CookPostPool> cookPostPools = cookPostPoolService.findAllByUser(user.getId(), reqBody.searchText, page, size);
        ArrayList<CookPost> cookPostArrayList = new ArrayList<>();

        if (cookPostPools.isEmpty()){
            cookPostArrayList = cookPostService.findAll(reqBody.searchText, page, size);
        } else {
            cookPostArrayList = new ArrayList<>(
                    cookPostPools.stream().map(CookPostPool::getCookposts).collect(Collectors.toList())
            );
        }

        ArrayList<Image> thumbnails = new ArrayList<>();
        ArrayList<Image> avatars = new ArrayList<>();
        for (var item : cookPostArrayList) {
            thumbnails.add(new Image(Integer.toString(item.getId()), item.getThumbnail()));
            avatars.add(new Image(Integer.toString(item.getId()), item.getAuthor().getAvatar()));
        }

        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        try {
            DropBoxRedirectedLinkGetter getterAvatar = new DropBoxRedirectedLinkGetter();
            DropBoxRedirectedLinkGetter getterThumbnail = new DropBoxRedirectedLinkGetter();
            GetRedirectedLinkExecutionResult executionResultThumbnails = getterThumbnail.getRedirectedLinks(thumbnails);
            HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> thumbnailSuccess = executionResultThumbnails.getSuccessfulResults();
            GetRedirectedLinkExecutionResult executionResultAvatar = getterAvatar.getRedirectedLinks(avatars);
            HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> avatarSuccess = executionResultAvatar.getSuccessfulResults();

            if (executionResultAvatar != null && executionResultAvatar != null) {
                for (var item : cookPostArrayList) {
                    String key = Integer.toString(item.getId());
                    String thumbnail = (thumbnailSuccess != null && thumbnailSuccess.containsKey(key)) ? thumbnailSuccess.get(key).getUri() : null;
                    String avatar = (avatarSuccess != null && avatarSuccess.containsKey(key)) ? avatarSuccess.get(key).getUri() : null;
                    UserReactCookPost userReactCookPost = userReactCookPostService.findByUserAndPost(item.getAuthor().getId(), item.getId());
                    int userReactType = (userReactCookPost != null) ? userReactCookPost.getReaction() : 0;
                    data.add(item.getJson(thumbnail, avatar, userReactType, "Asia/Saigon"));
                }

                return ResponseEntity.ok(new Response(data, new ArrayList<>()));
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Couldn't get author avatar ready to view url.");
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
    }
}
