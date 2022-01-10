package com.example.demo.HomeCook.Helper;

import com.example.demo.DropBox.*;
import com.example.demo.FileUploader.FileUploader;
import com.example.demo.Helpers.MediaFileHelper;
import com.example.demo.HomeCook.Entity.*;
import com.example.demo.HomeCook.Service.CookPostService;
import com.example.demo.Album.Entity.Image;
import com.example.demo.HomeCook.Service.UserReactCookPostService;
import com.example.demo.HomeCook.Service.UsersBookmarkCuisinePostsService;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.User.Entity.User;
import liquibase.pro.packaged.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class CookPostHelper {
    @Autowired
    private MediaFileHelper mediaFileHelper;

    @Autowired
    private CookPostService cookPostService;

    @Autowired
    private UserReactCookPostService userReactCookPostService;

    @Autowired
    private UsersBookmarkCuisinePostsService usersBookmarkCuisinePostsService;

    public String saveThumbnail(String thumbnailInBytes, CookPost cookPost) throws ExecutionException, InterruptedException {
        Date now = new Date();
        ItemToUpload[] items = new ItemToUpload[1];
        items[0] = new ItemToUpload(cookPost.getId() + "_cuisine" + "_" + now.getTime() + ".jpg", thumbnailInBytes);
        String thumbnailUri = null;

        FileUploader fileUploader = new FileUploader();

        UploadExecutionResult result = fileUploader.uploadItems(items);
        UploadResult rs = fileUploader.getSuccessesAndFails(result);
        ArrayList<Image> success = rs.getSuccessUploads();

        if (!success.isEmpty()) {
            thumbnailUri = success.get(0).getUri();
            cookPost.setThumbnail(success.get(0).getUri());
            cookPostService.save(cookPost);
        }

        return thumbnailUri;
    }

    public ThumbnailAvatarFetchResult getThumbnailAndAvatar(User user, CookPost cookPost) throws ExecutionException, InterruptedException {
        String thumbnail = null;
        String avatar = null;
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

        return new ThumbnailAvatarFetchResult(avatar, thumbnail);
    }

    public ArrayList<HashMap<String, Object>> getJsonWithThumbnailsAvatars(User user, ArrayList<CookPost> cookPosts) throws ExecutionException, InterruptedException {
        ArrayList<Image> thumbnails = new ArrayList<>();
        ArrayList<Image> avatars = new ArrayList<>();
        for (var item : cookPosts) {
            thumbnails.add(new Image(Integer.toString(item.getId()), item.getThumbnail()));
            avatars.add(new Image(Integer.toString(item.getId()), item.getAuthor().getAvatar()));
        }

        DropBoxRedirectedLinkGetter getterAvatar = new DropBoxRedirectedLinkGetter();
        DropBoxRedirectedLinkGetter getterThumbnail = new DropBoxRedirectedLinkGetter();
        GetRedirectedLinkExecutionResult executionResultThumbnails = getterThumbnail.getRedirectedLinks(thumbnails);
        HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> thumbnailSuccess = executionResultThumbnails.getSuccessfulResults();
        GetRedirectedLinkExecutionResult executionResultAvatar = getterAvatar.getRedirectedLinks(avatars);
        HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> avatarSuccess = executionResultAvatar.getSuccessfulResults();
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();

        if (executionResultAvatar != null && executionResultAvatar != null) {
            for (var item : cookPosts) {
                String key = Integer.toString(item.getId());
                String thumbnail = (thumbnailSuccess != null && thumbnailSuccess.containsKey(key)) ? thumbnailSuccess.get(key).getUri() : null;
                String avatar = (avatarSuccess != null && avatarSuccess.containsKey(key)) ? avatarSuccess.get(key).getUri() : null;
                UserReactCookPost userReactCookPost = userReactCookPostService.findByUserAndPost(user.getId(), item.getId());
                int userReactType = (userReactCookPost != null) ? userReactCookPost.getReaction() : 0;
                UserBookmarkCuisinePost userBookmarkCuisinePost = usersBookmarkCuisinePostsService.findByUserAndPost(user.getId(), item.getId());
                data.add(item.getJson(thumbnail, avatar, userReactType, "Asia/Saigon", userBookmarkCuisinePost != null));
            }
        }

        return data;
    }
}

