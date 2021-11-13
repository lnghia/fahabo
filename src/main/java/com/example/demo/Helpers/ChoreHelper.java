package com.example.demo.Helpers;

import com.example.demo.Service.Chore.ChoreService;
import com.example.demo.Service.ChoreAlbum.ChoreAlbumService;
import com.example.demo.Service.ChoresAssignUsers.ChoresAssignUsersService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.PhotoInChore.PhotoInChoreService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.*;
import com.example.demo.domain.Family.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChoreHelper {
    @Autowired
    private ChoreService choreService;

    @Autowired
    private ChoresAssignUsersService choresAssignUsersService;

    @Autowired
    private ChoreAlbumService choreAlbumService;

    @Autowired
    private PhotoInChoreService photoInChoreService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    public List<User> assignUser(int[] assigneeIds, Chore chore){
        List<User> users = new ArrayList<>();

        for(var assigneeId : assigneeIds){
            User assignee = userService.getUserById(assigneeId);
            ChoresAssignUsers choresAssignUsers = new ChoresAssignUsers();
            choresAssignUsers.setAssignee(assignee);
            choresAssignUsers.setChore(chore);
            chore.getChoresAssignUsers().add(choresAssignUsers);
            assignee.getChoresAssignUsers().add(choresAssignUsers);
            users.add(assignee);
        }

        return users;
    }

    public Date getNewDeadline(Date deadline, String repeatType){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);

        switch (repeatType){
            case "DAILY":
                calendar.add(Calendar.DATE, 1);
                break;
            case "WEEKLY":
                calendar.add(Calendar.DATE, 7);
                break;
            case "MONTHLY":
                calendar.add(Calendar.MONTH, 1);
                break;
        }

        return calendar.getTime();
    }
//    public Chore[] findWithFilterSortedBy(User user, Family family, String[] status, String title, String sortedBy, int page, int size){
//        ArrayList<Chore> chores = choreService.findAllByFamilyId(family.getId());
//
//        if(user != null){
//            chores = chores.stream().map(chore -> {
//                return chore.getChoresAssignUsers().stream().filter(choresAssignUsers -> {
//                    return choresAssignUsers.getAssignee().getId() == user.getId();
//                })
//            })
//        }
//    }

    void deleteChoresInFamily(int familyId){
        choresAssignUsersService.deleteChoreUserRelationByFamilyId(familyId);
        photoInChoreService.deletePhotosInChoreAlbumByFamilyId(familyId);
        choreAlbumService.deleteChoreAlbumByFamilyId(familyId);
        choreService.deleteChoresInFamily(familyId);

        Family family = familyService.findById(familyId);
        Family tmp = familyService.findByName(Helper.getInstance().TEMP_FAMILY);

        for(var chore : family.getChores()){
            chore.setFamily(tmp);
            choreService.saveChore(chore);
        }
    }

    public HashMap<String, Object> getJson(int familyId, Chore chore) {
        HashMap<String, Object> rs = new HashMap<>(){{
            put("choreId", chore.getId());
            put("title", chore.getTitle());
            put("description", chore.getDescription());
            put("status", chore.getStatus());
            put("deadline", chore.getDeadLineAsString());
            put("repeatType", chore.getRepeatType());
        }};

        User[] assignees = chore.getChoresAssignUsers().stream().filter(choresAssignUsers1 -> !choresAssignUsers1.isDeleted()).map(choresAssignUsers1 -> {
            return choresAssignUsers1.getAssignee();
        }).toArray(size -> new User[size]);
        Photo[] photos = Arrays.stream(assignees).map(user -> {
            return new Photo(Integer.toString(user.getId()), user.getAvatar());
        }).toArray(size -> new Photo[size]);

        HashMap<String, String> avatars = Helper.getInstance().redirectImageLinks(photos);

        rs.put("assignees", Arrays.stream(assignees).map(user -> {
            return new HashMap<String, Object>(){{
                put("memberId", user.getId());
                put("name", user.getName());
                put("avatar", avatars.containsKey(Integer.toString(user.getId())) ? avatars.get(Integer.toString(user.getId())) : user.getAvatar());
                put("isHost", familyService.isHostInFamily(user.getId(), familyId));
            }};
        }));

        return rs;
    }

    public boolean isPhotoNumExceedLimitChore(int num, Chore chore){
        if(chore == null || chore.getChoreAlbumSet().isEmpty()){
            return num > Helper.getInstance().CHORE_PHOTO_MAX_NUM;
        }

        int choreAlbumId = chore.getChoreAlbumSet().iterator().next().getId();

        return photoInChoreService.countPhotosNumInChore(choreAlbumId) + num > Helper.getInstance().CHORE_PHOTO_MAX_NUM;
    }
}
