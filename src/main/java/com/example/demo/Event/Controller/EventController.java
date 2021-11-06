package com.example.demo.Event.Controller;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.Entity.PhotoInEvent;
import com.example.demo.Event.Helper.EventHelper;
import com.example.demo.Event.RequestBody.CreateEventReqBody;
import com.example.demo.Event.RequestBody.DeleteEventReqBody;
import com.example.demo.Event.RequestBody.GetEventPhotoReqBody;
import com.example.demo.Event.RequestBody.UpdateEventReqBody;
import com.example.demo.Event.Service.*;
import com.example.demo.Helpers.Helper;
import com.example.demo.RequestForm.GetChorePhotoReqForm;
import com.example.demo.RequestForm.GetChoresReqForm;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    @PostMapping("/new_event")
    public ResponseEntity<Response> createEvent(@Valid @RequestBody CreateEventReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        ArrayList<Event> events = new ArrayList<>();
        Family family = familyService.findById(reqBody.familyId);

        try {
            if(reqBody.repeatType == null || reqBody.repeatType.isEmpty() || reqBody.repeatType.isBlank()){
                events.add(eventHelper.createEvent(reqBody, user));
            }
            else{
                events.add(eventHelper.createRepeatEvent(reqBody, user).get(0));
            }
            ArrayList<Image> success = eventHelper.addPhotosToEvent(reqBody.photos, events.get(0), events.get(0).getFamily());
            for(var event : events){
                family.getEvents().add(event);
                event.setFamily(family);
                familyService.saveFamily(family);
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

        if (event != null && !event.isDeleted()) {
            if (event.getReporter().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
            }
            if (eventHelper.isPhotoNumExceedLimitChore(reqBody.photos.length, event)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of(""))));
            }
            try {
                ArrayList<Image> images = eventHelper.updateAllEventsInGroup(event, user, reqBody, true);
//                if(reqBody.updateAll != null && !reqBody.updateAll){
//                    images = eventHelper.updateAllEventsInGroup(event, user, reqBody, true);
//                    eventService.saveEvent(event);
//                }
//                else{
//                    images = eventHelper.updateAllEventsInGroup(event, user, reqBody);
//                }

                HashMap<String, Object> data = event.getJson();
                data.put("photos", images.stream().map(s -> {
                    return new HashMap() {{
                        put("uri", s.getUri());
                    }};
                }).collect(Collectors.toList()));

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
            if(reqBody.deleteAll == null || !reqBody.deleteAll){
                eventHelper.deleteEvent(event, headEventId, false);
            }
            else{
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
                                           @Valid @RequestBody GetChoresReqForm requestBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(requestBody.familyId);

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
                                                    @Valid @RequestBody GetEventPhotoReqBody reqBody){
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        int headEventId = groupEventService.findHeadEventId(reqBody.eventId);
        Event headEvent = eventService.getById(headEventId);

        if(headEvent.getFamily().checkIfUserExist(user)){
            try {
                ArrayList<HashMap<String, Object>> data = eventHelper.getPhotos(headEvent, page, size);

                return ResponseEntity.ok(new Response(data, new ArrayList<>()));
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error in start getting redirected image threads.", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
