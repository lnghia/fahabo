package com.example.demo.Event.Controller;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.Entity.GroupEvent;
import com.example.demo.Event.Helper.EventHelper;
import com.example.demo.Event.RequestBody.*;
import com.example.demo.Event.Service.*;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.RequestForm.GetChoresReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.Photo;
import com.example.demo.domain.User;
import com.google.rpc.Help;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/events")
@Slf4j
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private EventAlbumService eventAlbumService;

    @Autowired
    private EventAssignUserService eventAssignUserService;

    @Autowired
    private PhotoInEventService photoInEventService;

    @Autowired
    private EventHelper eventHelper;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private GroupEventService groupEventService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private FirebaseMessageHelper firebaseMessageHelper;

    @PostMapping("/new_event")
    public ResponseEntity<Response> createEvent(@Valid @RequestBody CreateEventReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        ArrayList<Event> events = new ArrayList<>();
        Family family = familyService.findById(reqBody.familyId);
        List<User> users = new ArrayList<>();
        Helper helper = Helper.getInstance();
        String langCode = (family.getTimezone() == null) ? "en" : ((family.getTimezone().equals("Asia/Ho_Chi_Minh") || family.getTimezone().equals("Asia/Saigon")) ? "vi" : "en");

        try {
            if (reqBody.repeatType == null || reqBody.repeatType.isEmpty() || reqBody.repeatType.isBlank()) {
                Event event = eventHelper.createEvent(reqBody, user);
                events.add(event);
                GroupEvent headGroupEvent = groupEventService.createGroupEvent(event, event);
            } else {
                events.addAll(eventHelper.createRepeatEvent(reqBody, user));
            }
            if (eventHelper.isPhotoNumExceedLimitChore(reqBody.photos.length, null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(""))));
            }
            ArrayList<Image> success = eventHelper.addPhotosToEventByHeadEvent(reqBody.photos, events.get(0), events.get(0).getFamily());
            List<Integer> photoIds = photoInEventService.getAllPhotoIdsInEvent(events.get(0).getEventAlbumSet().iterator().next().getId());
            List<Photo> photos = photoIds.stream().map(id -> {
                return photoService.getById(id);
            }).collect(Collectors.toList());
            eventHelper.addPhotosToSubEvents(photos, events, events.get(0).getId());
            for (var event : events) {
                family.getEvents().add(event);
                event.setFamily(family);
                familyService.saveFamily(family);
            }

            for (var tmp : events.get(0).getEventAssignUsers()) {
                users.add(tmp.getAssignee());
            }
            List<User> usersToNotify = new ArrayList<>();
            if (users.size() > 1) {
                usersToNotify = users.stream().filter(user1 -> user1.getId() != user.getId()).collect(Collectors.toList());
                firebaseMessageHelper.notifyUsers(
                        usersToNotify,
                        helper.getMessageInLanguage("eventHasBeenAssignedTitle", langCode),
                        String.format(helper.getMessageInLanguage("eventHasBeenAssignedBody", langCode), user.getName()),
                        new HashMap<String, String>() {{
                            put("navigate", "EVENT_DETAIL");
                            put("id", Integer.toString(events.get(0).getId()));
                        }});
            } else {
                usersToNotify = family.getUsersInFamily().stream().filter(userInFamily -> userInFamily.getUserId() != user.getId()).map(userInFamily -> userInFamily.getUser()).collect(Collectors.toList());
                firebaseMessageHelper.notifyUsers(
                        usersToNotify,
                        helper.getMessageInLanguage("eventHasBeenAssignedTitle", langCode),
                        String.format(helper.getMessageInLanguage("eventHasBeenAssignedBody", langCode), user.getName()),
                        new HashMap<String, String>() {{
                            put("navigate", "EVENT_DETAIL");
                            put("id", Integer.toString(events.get(0).getId()));
                        }});
            }

            return ResponseEntity.ok(new Response(events.get(0).getJson(), new ArrayList<>(List.of())));
        } catch (ParseException e) {
            log.error("Couldn't parse date of from and to.", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation."))));
        } catch (ExecutionException | InterruptedException e) {
//            event.setDeleted(false);
//            eventService.saveEvent(event);
            log.error("Couldn't upload photos.", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
        }
    }

    @PostMapping("/update_event")
    public ResponseEntity<Response> updateEvent(@Valid @RequestBody UpdateEventReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Event event = eventService.getById(reqBody.eventId);
        List<User> users = new ArrayList<>();
        HashSet<Integer> assignedUsers = new HashSet<>();
        Helper helper = Helper.getInstance();
        String langCode = (event.getFamily().getTimezone() == null) ? "en" : ((event.getFamily().getTimezone().equals("Asia/Ho_Chi_Minh") || event.getFamily().getTimezone().equals("Asia/Saigon")) ? "vi" : "en");

        for (var eventAssignUser : event.getEventAssignUsers()) {
            assignedUsers.add(eventAssignUser.getUserId());
        }

        if (event != null && !event.isDeleted()) {
            if (event.getReporter().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
            }
            if (eventHelper.isPhotoNumExceedLimitChore(reqBody.photos.length, event)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(""))));
            }
            try {
                ArrayList<Image> images = new ArrayList<>();
                if (reqBody.updateAll == null || !reqBody.updateAll) {
                    images = eventHelper.updateASingleEvent(event, user, reqBody);
                    eventService.saveEvent(event);
                    if (reqBody.deletePhotos != null && reqBody.deletePhotos.length > 0) {
                        eventHelper.deletePhotosInEventByPhotoId(event.getId(), reqBody.deletePhotos);
                    }
                } else {
                    images = eventHelper.updateAllEventsInGroup(event, user, reqBody, true);
                }

                HashMap<String, Object> data = event.getJson();
                data.put("photos", images.stream().map(s -> {
                    return new HashMap() {{
                        put("uri", s.getUri());
                    }};
                }).collect(Collectors.toList()));

                for (var tmp : event.getEventAssignUsers()) {
                    if (!assignedUsers.contains(tmp.getUserId())) {
                        users.add(tmp.getAssignee());
                    }
                }
                if (!users.isEmpty()) {
                    firebaseMessageHelper.notifyUsers(
                            users,
                            helper.getMessageInLanguage("eventHasBeenAssignedTitle", langCode),
                            String.format(helper.getMessageInLanguage("eventHasBeenAssignedBody", langCode), user.getName()),
                            new HashMap<String, String>() {{
                                put("navigate", "EVENT_DETAIL");
                                put("id", Integer.toString(event.getId()));
                            }});
                }

                return ResponseEntity.ok(new Response(
                        data,
                        new ArrayList<>(List.of())
                ));
            } catch (ParseException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation."))));
            } catch (ExecutionException | InterruptedException e) {
                log.info("Couldn't upload images.", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("event.eventDoesNotExist"))));
    }

    @PostMapping("/delete_event")
    public ResponseEntity<Response> deleteEvent(@Valid @RequestBody DeleteEventReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Event event = eventService.getById(reqBody.eventId);
        int headEventId = groupEventService.findHeadEventId(event.getId());

        if (event == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("event.eventDoesNotExist"))));
        }
        if (event.getReporter().getId() == user.getId()) {
            if (reqBody.deleteAll == null || !reqBody.deleteAll) {
                eventHelper.deleteEvent(event, headEventId, true);
            } else {
                eventHelper.deleteAllEventsInGroup(event);
            }

            return ResponseEntity.ok(new Response(
                    new HashMap<>() {{
                        put("eventId", reqBody.eventId);
                    }},
                    new ArrayList<>()
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping
    public ResponseEntity<Response> getAll(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                           @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                           @Valid @RequestBody GetChoresReqForm requestBody,
                                           @RequestHeader("User-Agent") String userAgent) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

        log.info("Device:", userAgent);

        if (family.checkIfUserExist(user)) {
            try {
                ArrayList<Event> events = eventService.findAll(
                        requestBody.assigneeIds,
                        family,
                        (requestBody.searchText != null) ? requestBody.searchText : "",
                        (requestBody.sortBy != null) && requestBody.sortBy.equals("deadline"),
                        (requestBody.from != null) ? Helper.getInstance().formatDate(requestBody.from) : null,
                        (requestBody.to != null) ? Helper.getInstance().formatDate(requestBody.to) : null,
                        page,
                        size
                );

                return ResponseEntity.ok(new Response(events.stream().map(event -> {
                    return event.getJson();
                }).collect(Collectors.toList()), new ArrayList<>()));
            } catch (ParseException e) {
                log.error("Couldn't parse date /events.", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.dateFormatInvalid"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/get_event_photos")
    private ResponseEntity<Response> getEventPhotos(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                                    @Valid @RequestBody GetEventPhotoReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
//        int headEventId = groupEventService.findHeadEventId(reqBody.eventId);
//        Event headEvent = eventService.getById(headEventId);
        Event event = eventService.getById(reqBody.eventId);

        if (event.getFamily().checkIfUserExist(user)) {
            try {
                ArrayList<HashMap<String, Object>> data = eventHelper.getPhotos(event, page, size);

                return ResponseEntity.ok(new Response(data, new ArrayList<>()));
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error in start getting redirected image threads.", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/dates_contain_events")
    private ResponseEntity<Response> findDatesContainEvents(@Valid @RequestBody FindDatesContainEventsReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);
        Helper helper = Helper.getInstance();

        if (family.checkIfUserExist(user)) {
            try {
                ArrayList<String> datesContainEvents = eventHelper.findDatesContainEventsInFamily(
                        helper.formatDateWithoutTime(reqBody.from),
                        helper.formatDateWithoutTime(reqBody.to),
                        family.getId()
                );

                return ResponseEntity.ok(new Response(datesContainEvents, new ArrayList<>()));
            } catch (ParseException e) {
                log.error("Couldn't parse date in /dates_contain_events", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.dateFormatInvalid"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/detail")
    private ResponseEntity<Response> getEventDetail(@RequestBody GetEventDetailReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Helper helper = Helper.getInstance();
        Event event = eventService.getById(reqBody.eventId);

        if(event == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.eventNotExist"))));
        }

        if (event.getFamily().checkIfUserExist(user)) {
            return ResponseEntity.ok(new Response(event.getJson(), new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
