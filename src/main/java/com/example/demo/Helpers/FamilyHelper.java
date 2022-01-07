package com.example.demo.Helpers;

import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.DropBox.UploadResult;
import com.example.demo.Event.Helper.EventHelper;
import com.example.demo.FileUploader.FileUploader;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.ChoresAssignUsers.ChoresAssignUsersService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Role.RoleService;
import com.example.demo.Service.UserInFamily.UserInFamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Data
public class FamilyHelper {
    public final String defaultThumbnail = "https://media.istockphoto.com/vectors/large-happy-family-is-standing-and-hugging-several-generations-with-vector-id1221390042?b=1&k=20&m=1221390042&s=612x612&w=0&h=DIY35-8Q2jZe8LMLdwXETf54sKwQL3_F6OCtXdTnB44=";

    @Autowired
    AlbumHelper albumHelper;

    @Autowired
    ChoreHelper choreHelper;

    @Autowired
    FamilyService familyService;

    @Autowired
    UserInFamilyService userInFamilyService;

    @Autowired
    ChoresAssignUsersService choresAssignUsersService;

    @Autowired
    EventHelper eventHelper;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private FamilyService familyService;

//    public void createFamily(Family family){
//        familyService.updateFamily(family);
//    }
//
//    public void addMember(Family family, List<User> users){
//        for(User user : users){
//            familyService.addMember(user, family);
//            userService.joinFamily(user, family);
//        }
//    }
//
//    public String generateImgUploadId(Family family){
//        return String.format("thumbnail_%s_%s.png", family.getId(), new Date().getTime());
//    }

    public void deleteFamilyById(int familyId) {
        albumHelper.deleteAlbumsInFamily(familyId);
        choresAssignUsersService.deleteChoreUserRelationByFamilyId(familyId);
        choreHelper.deleteChoresInFamily(familyId);
        userInFamilyService.deleteUserInFamily(familyService.findById(familyId));
        albumHelper.pointFamilyAlbumsToTmpFamily(familyService.findById(familyId), familyService.findByName(Helper.getInstance().TEMP_FAMILY));
        eventHelper.deleteEventsInFamily(familyId);
//        familyService.deleteFamilyById(familyId);
    }

    public void addMember(List<Integer> ids, Family family) {
        ids.forEach(id -> {
            User tmpUser = userService.getUserById(id);

            UserInFamily userInFamily = new UserInFamily(tmpUser, family);
            userInFamily.setRole(roleService.getRoleMember());
            userInFamilyService.saveUserInFamily(userInFamily);

            tmpUser.addFamily(userInFamily);
            family.addUser(userInFamily);
            userService.updateUser(tmpUser);
            familyService.saveFamily(family);
        });
    }

    public void assignHost(User user, Family family) {
        UserInFamily userInFamily = new UserInFamily(user, family);
        userInFamily.setRole(roleService.getRoleHost());
        userInFamilyService.saveUserInFamily(userInFamily);
        user.addFamily(userInFamily);
        family.addUser(userInFamily);
        userService.updateUser(user);
        familyService.saveFamily(family);
    }

    public String saveThumbnail(Image thumbnail, Family family) throws ExecutionException, InterruptedException {
        ItemToUpload[] itemToUploads = Helper.getInstance().convertAImgToParaForUploadImgs(thumbnail);
        FileUploader fileUploader = new FileUploader();

        UploadExecutionResult result = fileUploader.uploadItems(itemToUploads);
        UploadResult rs = fileUploader.getSuccessesAndFails(result);
        ArrayList<Image> success = rs.getSuccessUploads();
        ArrayList<Image> fail = rs.getFailUploads();

        if (success.isEmpty()) {
            family.setThumbnail(Helper.getInstance().DEFAULT_FAMILY_THUMBNAIL);
            familyService.saveFamily(family);

            return null;
        }

        family.setThumbnail(success.get(0).getUri());
        familyService.saveFamily(family);

        return success.get(0).getUri();
    }
}
