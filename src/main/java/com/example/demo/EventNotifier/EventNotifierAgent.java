package com.example.demo.EventNotifier;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.Entity.EventAssignUser;
import com.example.demo.Event.Service.EventService;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import liquibase.pro.packaged.E;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class EventNotifierAgent {

    @Autowired
    private EventService eventService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private FirebaseMessageHelper firebaseMessageHelper;

    @Scheduled(fixedDelay = 55000)
    public void notifyAboutUpComingEvents() {
        Date start = new Date();

        log.info("Checking for upcoming events ...");

        ArrayList<Family> families = familyService.findAllFamily();
        Helper helper = Helper.getInstance();

        for (var family : families) {
            if(family.getId() == 0) continue;
            log.debug(TimeZone.getTimeZone(family.getTimezone()).getDisplayName());
            ArrayList<Event> eventsIn30Mins = eventService.findAllUpComingEventsIn30Mins(family.getTimezone());
            String langCode = helper.getLangCode(family);

            log.info(String.format("Notifying %d events ...", eventsIn30Mins.size()));
            for (var event : eventsIn30Mins) {
                if (event.getEventAssignUsers().isEmpty()) {
                    firebaseMessageHelper.notifyAllUsersInFamily(
                            event.getFamily(),
                            helper.getMessageInLanguage("anUpComingEventIn30MinsTitle", langCode),
                            String.format(
                                    helper.getMessageInLanguage("anUpComingEventIn30MinsBody", langCode),
                                    event.getFromAsString()
                            ),
                            new HashMap<>()
                    );
                    event.setNotified(true);
                    eventService.saveEvent(event);
                } else {
                    List<User> participants = event.getEventAssignUsers().stream().map(EventAssignUser::getAssignee).collect(Collectors.toList());

                    firebaseMessageHelper.notifyUsers(
                            participants,
                            family,
                            helper.getMessageInLanguage("anUpComingEventIn30MinsTitle", langCode),
                            String.format(
                                    helper.getMessageInLanguage("anUpComingEventIn30MinsBody", langCode),
                                    event.getFromAsString()
                            ),
                            new HashMap<>()
                    );
                }
            }
        }

        Date end = new Date();

        log.info(String.format("Checking upcoming events completes in %d milliseconds", end.getTime() - start.getTime()));
    }
}
