package com.example.demo.Event.Helper;

import com.example.demo.DropBox.*;
import com.example.demo.Event.Entity.*;
import com.example.demo.Event.RequestBody.CreateEventReqBody;
import com.example.demo.Event.RequestBody.UpdateEventReqBody;
import com.example.demo.Event.Service.*;
import com.example.demo.Helpers.FamilyHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.*;
import com.example.demo.domain.Family.Family;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventHelper {
    @Autowired
    private EventService eventService;

    @Autowired
    private EventAlbumService eventAlbumService;

    @Autowired
    private EventAssignUserService eventAssignUserService;

    @Autowired
    private PhotoInEventService photoInEventService;

    @Autowired
    private FamilyHelper familyHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private DropBoxHelper dropBoxHelper;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private GroupEventService groupEventService;

    public Event createEvent(CreateEventReqBody reqBody, User user) throws ParseException {
        Event event = new Event();
        eventService.saveEvent(event);

        FamilyService familyService = familyHelper.getFamilyService();
        Family family = familyService.findById(reqBody.familyId);
        Date now = Helper.getInstance().getNowAsTimeZone(family.getTimezone());

        event.setFamily(family);
        event.setTitle(reqBody.title);
        event.setDescription(reqBody.description);
        event.setReporter(user);
        if (reqBody.repeatType != null && !reqBody.repeatType.isBlank() && !reqBody.repeatType.isEmpty()) {
            event.setRepeatType(reqBody.repeatType);
            event.setRepeatOccurrences(reqBody.occurrences);
        }

        if (reqBody.from != null && !reqBody.from.isEmpty() && !reqBody.to.isBlank() &&
                reqBody.to != null && !reqBody.to.isEmpty() && !reqBody.to.isBlank()) {
            try {
                event.setFrom(Helper.getInstance().formatDate(reqBody.from));
                event.setTo(Helper.getInstance().formatDate(reqBody.to));
            } catch (ParseException e) {
                event.setDeleted(false);
                eventService.saveEvent(event);
                throw new ParseException("Couldn't parse from and to", 0);
            }
        }
        event.setRepeatType(reqBody.repeatType);
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        eventService.saveEvent(event);

        assignUsers(reqBody.assigneeIds, event);

        //assign users
//        assignUsers(reqBody.assigneeIds, event);

        return event;
    }

    public void assignUsers(int[] userIds, Event event) {
        if (userIds == null) return;
        for (var assigneeId : userIds) {
            User assignee = userService.getUserById(assigneeId);
            EventAssignUser eventAssignUser = new EventAssignUser();
            eventAssignUser.setAssignee(assignee);
            eventAssignUser.setEvent(event);
            event.getEventAssignUsers().add(eventAssignUser);
            eventAssignUserService.saveEventAssignUser(eventAssignUser);
//            eventService.saveEvent(event);
        }
    }

    public ArrayList<Image> addPhotosToEvent(String[] photos, Event event, Family family) throws ExecutionException, InterruptedException {
        if (photos != null && photos.length != 0) {
            Date now = Helper.getInstance().getNowAsTimeZone(family.getTimezone());
            int headEventId = groupEventService.findHeadEventId(event.getId());
            event = eventService.getById(headEventId);

            EventAlbum eventAlbum;
            if (event.getEventAlbumSet().isEmpty()) {
                eventAlbum = new EventAlbum();
                eventAlbum.setEvent(event);
                eventAlbumService.saveEventAlbum(eventAlbum);
                event.getEventAlbumSet().add(eventAlbum);
            } else {
                eventAlbum = event.getEventAlbumSet().iterator().next();
            }

            ItemToUpload[] items = new ItemToUpload[photos.length];
            HashMap<String, Photo> newPhotos = new HashMap<>();

            for (int i = 0; i < photos.length; ++i) {
                Photo photo = new Photo();
                PhotoInEvent photoInEvent = new PhotoInEvent();

                photo.setCreatedAt(now);
                photo.setUpdatedAt(now);

                photoService.savePhoto(photo);
                photoInEvent.setPhoto(photo);
                photoInEvent.setAlbum(eventAlbum);
                photoInEventService.savePhotoInEvent(photoInEvent);
                photo.setName(Helper.getInstance().generatePhotoNameToUploadToAlbum(
                        family.getId(),
                        eventAlbum.getId(),
                        photo.getId()));
                photoService.savePhoto(photo);
                newPhotos.put(photo.getName(), photo);

                items[i] = new ItemToUpload(photo.getName(), photos[i]);
            }

            UploadResult result = dropBoxHelper.uploadImages(items, eventAlbum.getId(), 1);
            ArrayList<Image> success = result.successUploads;

            for (var image : success) {
                Photo photo = newPhotos.get(image.getName());
                photo.setUri(image.getMetadata().getUrl());
                photoService.savePhoto(photo);
            }

            return success;
        }

        return new ArrayList<>();
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

    public ArrayList<HashMap<String, Object>> getPhotos(Event event, int page, int size) throws ExecutionException, InterruptedException {
        EventAlbum eventAlbum = event.getEventAlbumSet().iterator().next();
        ArrayList<PhotoInEvent> photoInEvents = photoInEventService.getAllPhotos(eventAlbum.getId(), page, size);
        List<Integer> photoIds = photoInEvents.stream().map(photoInChore -> {
            return photoInChore.getPhotoId();
        }).collect(Collectors.toList());
        List<Photo> photos = photoIds.stream().map(photoId -> {
            return photoService.getById(photoId);
        }).collect(Collectors.toList());
        ArrayList<HashMap<String, Object>> data;

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

            return data;
        }

        return null;
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

    public int countPhotosInEvent(int eventAlbumId) {
        if (eventAlbumId < 0) return 0;
        return photoInEventService.countPhotosNumInEvent(eventAlbumId);
    }

    public void deleteEventsInFamily(int familyId) {
        eventAssignUserService.deleteEventUserRelationship(familyId);
        photoInEventService.deletePhotosInFamilyEvents(familyId);
        eventAlbumService.deleteEventAlbumsInFamily(familyId);
        eventService.deleteEventsInFamily(familyId);
    }

    public void deleteEvent(Event event, int headEventId, boolean deleteImagesAndAlbums) {
        if (event.getId() == headEventId && deleteImagesAndAlbums) {
            photoInEventService.deletePhotosInEvent(event.getId());
            eventAlbumService.deleteEventAlbumInEvent(event.getId());
        }
        event.getEventAssignUsers().forEach(eventAssignUser -> {
            eventAssignUser.setDeleted(true);
            eventAssignUserService.saveEventAssignUser(eventAssignUser);
        });
        event.setDeleted(true);
        eventService.saveEvent(event);
    }

    public boolean isPhotoNumExceedLimitChore(int num, Event event) {
        if (event == null) {
            return num > Helper.getInstance().CHORE_PHOTO_MAX_NUM;
        }
        if (event.getEventAlbumSet().isEmpty()) return false;

        int choreAlbumId = event.getEventAlbumSet().iterator().next().getId();

        return photoInEventService.countPhotosNumInEvent(choreAlbumId) + num > Helper.getInstance().CHORE_PHOTO_MAX_NUM;
    }

    public void cancelAssignUsers(Event event) {
        event.getEventAssignUsers().forEach(eventsAssignUsers -> {
            eventsAssignUsers.setDeleted(true);
            eventAssignUserService.saveEventAssignUser(eventsAssignUsers);
        });
    }

    public Event createNewEventFromExistingOne(Event event) {
        Event event1 = new Event();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(event.getFamily().getTimezone()));

        event1.setRepeatType(event.getRepeatType());
        event1.setFrom(event.getFrom());
        event1.setTo(event.getTo());
        event1.setCreatedAt(calendar.getTime());
        event1.setUpdatedAt(calendar.getTime());
        event1.setTitle(event.getTitle());
        event1.setDescription(event.getDescription());
        event1.setReporter(event.getReporter());
        event1.setRepeatOccurrences(event.getRepeatOccurrences());
        event1.setFamily(event.getFamily());

        return event1;
    }

    public void updateRepeatType(Event event, Event headEvent, User user, UpdateEventReqBody reqBody) throws ParseException {
        event.setRepeatType(reqBody.repeatType);
        deleteAllSubEventsInGroup(event);
        if (reqBody.repeatType != null && !reqBody.repeatType.isBlank() && !reqBody.repeatType.isEmpty()) {
            ArrayList<Event> subEvents = createSubEvents(headEvent, user, reqBody.repeatType, reqBody.occurrences, true);

            int[] assigneeIds = new int[headEvent.getEventAssignUsers().size()];
            int i = 0;

            for (var tmp : headEvent.getEventAssignUsers()) {
                assigneeIds[i++] = tmp.getAssignee().getId();
            }
            for (var e : subEvents) {
                assignUsers(reqBody.assigneeIds, e);
            }
        }
    }

    public void updateOccurrences(Event event, Event headEvent, User user, UpdateEventReqBody reqBody) throws ParseException {
        deleteAllSubEventsInGroup(event);
        if (reqBody.occurrences > 0) {
            ArrayList<Event> subEvents = createSubEvents(headEvent, user, reqBody.repeatType, reqBody.occurrences, true);

            int[] assigneeIds = new int[headEvent.getEventAssignUsers().size()];
            int i = 0;

            for (var tmp : headEvent.getEventAssignUsers()) {
                assigneeIds[i++] = tmp.getAssignee().getId();
            }
            for (var e : subEvents) {
                assignUsers(reqBody.assigneeIds, e);
            }
        }
    }

    public void updateEvent(Event event, User user, UpdateEventReqBody reqBody) throws ParseException {
        if (reqBody.title != null && !reqBody.title.isBlank() && !reqBody.title.isEmpty()) {
            event.setTitle(reqBody.title);
        }
        if (reqBody.description != null && !reqBody.description.isBlank() && !reqBody.description.isEmpty()) {
            event.setDescription(reqBody.description);
        }
        if (reqBody.occurrences != event.getRepeatOccurrences()) {
            event.setRepeatOccurrences(reqBody.occurrences);
        }
        if (!reqBody.repeatType.equals(event.getRepeatType())) {
            event.setRepeatType(reqBody.repeatType);
        }
//        try {
//            if (reqBody.from != null && !reqBody.from.isEmpty() && !reqBody.to.isBlank()) {
//                event.setFrom(Helper.getInstance().formatDate(reqBody.from));
//            }
//            if (reqBody.to != null && !reqBody.to.isEmpty() && !reqBody.to.isBlank()) {
//                event.setTo(Helper.getInstance().formatDate(reqBody.to));
//            }
//        } catch (ParseException e) {
//            throw new ParseException("Couldn't parse from and to", 0);
//        }
        cancelAssignUsers(event);
        assignUsers(reqBody.assigneeIds, event);
        event.setUpdatedAt(Helper.getInstance().getNowAsTimeZone(event.getFamily().getTimezone()));

        eventService.saveEvent(event);
    }

    public ArrayList<Image> updateAllEventsInGroup(Event event, User user, UpdateEventReqBody reqBody, boolean updatePhotos) throws ParseException, ExecutionException, InterruptedException {
        int headEventId = groupEventService.findHeadEventId(event.getId());
        Event headEvent = eventService.getById(headEventId);
        String firstRepeatType = headEvent.getRepeatType();
        int firstOccurrences = headEvent.getRepeatOccurrences();

        // update headEvent
        updateEvent(headEvent, user, reqBody);
        if (!firstRepeatType.equals(reqBody.repeatType)) {
            updateRepeatType(event, headEvent, user, reqBody);
        } else if (firstOccurrences != reqBody.occurrences) {
            updateOccurrences(event, headEvent, user, reqBody);
        } else {

            ArrayList<GroupEvent> events = groupEventService.findAllEventsByHeadEventId(headEventId);

            for (var tmp : events) {
                updateEvent(tmp.getSubEvent(), user, reqBody);
            }
        }

        ArrayList<Image> rs = new ArrayList<>();
        if (updatePhotos) {
            if (reqBody.photos != null && reqBody.photos.length > 0) {
                rs = addPhotosToEvent(reqBody.photos, headEvent, event.getFamily());
            }
        }

        return rs;
    }

    public void deleteAllSubEventsInGroup(Event event) {
        int headEventId = groupEventService.findHeadEventId(event.getId());
        Event headEvent = eventService.getById(headEventId);
        ArrayList<GroupEvent> events = groupEventService.findAllEventsByHeadEventId(headEventId);

        for (var groupEvent : events) {
            if (groupEvent.getSubEvent().getId() != headEventId) {
                deleteEvent(groupEvent.getSubEvent(), headEventId, false);
            }
        }
    }

    public void deleteAllEventsInGroup(Event event) {
        int headEventId = groupEventService.findHeadEventId(event.getId());
        Event headEvent = eventService.getById(headEventId);
        ArrayList<GroupEvent> events = groupEventService.findAllEventsByHeadEventId(headEventId);

        for (var groupEvent : events) {
            if (groupEvent.getSubEvent().getId() != headEventId) {
                deleteEvent(groupEvent.getSubEvent(), headEventId, true);
            }
        }
        deleteEvent(headEvent, headEventId, true);
    }

    public ArrayList<Event> createSubEvents(Event headEvent, User user, String repeatType, int occurrences, boolean updateExistingHeadEvent) throws ParseException {
        ArrayList<Event> events = new ArrayList<>();

        if (updateExistingHeadEvent) {
            Date from = Helper.getInstance().formatDate(headEvent.getFromAsString());
            Date to = Helper.getInstance().formatDate(headEvent.getToAsString());
            events.add(headEvent);
            for (int i = 0; i < occurrences - 1; ++i) {
                Event subEvent = createNewEventFromExistingOne(headEvent);
                // reset from to
                Date new_from = Helper.getInstance().getNewDateAfterOccurrences(from, headEvent.getFamily().getTimezone(), repeatType, i + 1);
                Date new_to = Helper.getInstance().getNewDateAfterOccurrences(to, headEvent.getFamily().getTimezone(), repeatType, i + 1);
                subEvent.setFrom(new_from);
                subEvent.setTo(new_to);
                eventService.saveEvent(subEvent);
                groupEventService.createGroupEvent(headEvent, subEvent);
                events.add(subEvent);
            }
        }

        return events;
    }

    public ArrayList<Event> createRepeatEvent(CreateEventReqBody reqBody, User user) throws ParseException {
        ArrayList<Event> events = new ArrayList<>();
        Event headEvent = createEvent(reqBody, user);
        GroupEvent headGroupEvent = groupEventService.createGroupEvent(headEvent, headEvent);

        Date from = Helper.getInstance().formatDate(reqBody.from);
        Date to = Helper.getInstance().formatDate(reqBody.to);
        events.add(headEvent);
        for (int i = 0; i < reqBody.occurrences - 1; ++i) {
            Event subEvent = createEvent(reqBody, user);
            // reset from to
            from = Helper.getInstance().getNewDateAfterOccurrences(from, headEvent.getFamily().getTimezone(), reqBody.repeatType, 1);
            to = Helper.getInstance().getNewDateAfterOccurrences(to, headEvent.getFamily().getTimezone(), reqBody.repeatType, 1);
            subEvent.setFrom(from);
            subEvent.setTo(to);
            eventService.saveEvent(subEvent);
            groupEventService.createGroupEvent(headEvent, subEvent);
            events.add(subEvent);
        }

        return events;
    }
}
