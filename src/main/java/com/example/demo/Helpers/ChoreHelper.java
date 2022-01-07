package com.example.demo.Helpers;

import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadExecutionResult;
import com.example.demo.DropBox.UploadResult;
import com.example.demo.FileUploader.FileUploader;
import com.example.demo.Service.Chore.ChoreService;
import com.example.demo.Service.ChoreAlbum.ChoreAlbumService;
import com.example.demo.Service.ChoresAssignUsers.ChoresAssignUsersService;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.PhotoInChore.PhotoInChoreService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.*;
import com.example.demo.domain.Family.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

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

    @Autowired
    private PhotoService photoService;

    @Autowired
    private MediaFileHelper mediaFileHelper;

    public List<User> assignUser(int[] assigneeIds, Chore chore) {
        List<User> users = new ArrayList<>();

        for (var assigneeId : assigneeIds) {
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

    public Date getNewDeadline(Date deadline, String repeatType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);

        switch (repeatType) {
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

    void deleteChoresInFamily(int familyId) {
        choresAssignUsersService.deleteChoreUserRelationByFamilyId(familyId);
        photoInChoreService.deletePhotosInChoreAlbumByFamilyId(familyId);
        choreAlbumService.deleteChoreAlbumByFamilyId(familyId);
        choreService.deleteChoresInFamily(familyId);

        Family family = familyService.findById(familyId);
        Family tmp = familyService.findByName(Helper.getInstance().TEMP_FAMILY);

        for (var chore : family.getChores()) {
            chore.setFamily(tmp);
            choreService.saveChore(chore);
        }
    }

    public HashMap<String, Object> getJson(int familyId, Chore chore) {
        HashMap<String, Object> rs = new HashMap<>() {{
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
            return new HashMap<String, Object>() {{
                put("memberId", user.getId());
                put("name", user.getName());
                put("avatar", avatars.containsKey(Integer.toString(user.getId())) ? avatars.get(Integer.toString(user.getId())) : user.getAvatar());
                put("isHost", familyService.isHostInFamily(user.getId(), familyId));
            }};
        }));

        return rs;
    }

    public boolean isPhotoNumExceedLimitChore(int num, Chore chore) {
        if (chore == null || chore.getChoreAlbumSet().isEmpty()) {
            return num > Helper.getInstance().CHORE_PHOTO_MAX_NUM;
        }

        int choreAlbumId = chore.getChoreAlbumSet().iterator().next().getId();

        return photoInChoreService.countPhotosNumInChore(choreAlbumId) + num > Helper.getInstance().CHORE_PHOTO_MAX_NUM;
    }

    public void addPhotos(Chore chore, String[] photos, Family family) throws ExecutionException, InterruptedException {
        Date now = new Date();

        ChoreAlbum choreAlbum;

        ItemToUpload[] items = new ItemToUpload[photos.length];
        HashMap<String, Photo> newPhotos = new HashMap<>();

        boolean hasNoAlbum = chore.getChoreAlbumSet().isEmpty();
        if (hasNoAlbum) {
            choreAlbum = new ChoreAlbum();
            choreAlbum.setChore(chore);
            choreAlbumService.saveChoreAlbum(choreAlbum);
            chore.getChoreAlbumSet().add(choreAlbum);
        } else {
            choreAlbum = chore.getChoreAlbumSet().iterator().next();
        }

        for (int i = 0; i < photos.length; ++i) {
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

            items[i] = new ItemToUpload(photo.getName(), photos[i]);
        }
        FileUploader fileUploader = new FileUploader();
        UploadExecutionResult result = fileUploader.uploadItems(items);
        UploadResult rs = fileUploader.getSuccessesAndFails(result);
        ArrayList<Image> success = rs.getSuccessUploads();
        ArrayList<Image> fail = rs.getFailUploads();

        for (var image : success) {
            Photo photo = newPhotos.get(image.getName());
            photo.setUri(image.getUri());
            photoService.savePhoto(photo);
        }
    }

    public void deletePhotos(Chore chore, int[] photoIds) {
        ChoreAlbum choreAlbum = chore.getChoreAlbumSet().iterator().next();
        for (var id : photoIds) {
            Photo photo = photoService.getById(id);
            PhotoInChore photoInChore = photoInChoreService.getPhotoInChoreByAlbumIdAndPhotoId(choreAlbum.getId(), id);
            photoInChore.setDeleted(true);
            photo.setDeleted(true);
            photoInChoreService.savePhotoInChore(photoInChore);
            photoService.savePhoto(photo);
        }
    }

    public List<User> assignUsers(Chore chore, int[] assigneeIds) {
        HashSet<Integer> assignedUsers = new HashSet<>();
        List<User> users = new ArrayList<>();

        chore.getChoresAssignUsers().forEach(choresAssignUsers -> {
            assignedUsers.add(choresAssignUsers.getUserId());
            choresAssignUsers.setDeleted(true);
            choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
        });
        for (var id : assigneeIds) {
            User assignee = userService.getUserById(id);
            if (!assignedUsers.contains(id)) {
                users.add(assignee);
            }
            ChoresAssignUsers choresAssignUsers = new ChoresAssignUsers();
            choresAssignUsers.setChore(chore);
            choresAssignUsers.setAssignee(assignee);
            choresAssignUsersService.saveChoresAssignUsers(choresAssignUsers);
        }

        return users;
    }

    public void setDeadLine(Chore chore, String deadline) throws ParseException {
        chore.setDeadline(Helper.getInstance().formatDateWithoutTime(deadline));
        chore.setStatus("IN_PROGRESS");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(chore.getFamily().getTimezone()));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        if (Helper.getInstance().formatDateWithoutTime(deadline).before(calendar.getTime())) {
            chore.setStatus("EXPIRED");
        }
    }

    public void updateStatus(Chore chore, String status) {
        String repeat = chore.getRepeatType();

        if (repeat != null && !repeat.isBlank() && !repeat.isEmpty() && status.equals("DONE") && !chore.getStatus().equals("DONE")) {
            Chore repeatChore = new Chore(
                    chore.getFamily(),
                    "IN_PROGRESS",
                    chore.getTitle(),
                    chore.getDescription(),
                    getNewDeadline(new Date(), chore.getRepeatType()),
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
            assignUser(ids, repeatChore);

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
        chore.setStatus(status);
    }
}
