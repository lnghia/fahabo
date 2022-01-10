package com.example.demo.EventNotifier;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.Entity.EventAssignUser;
import com.example.demo.Event.Service.EventService;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.Transaction;
import com.example.demo.ExpensesAndIncomes.Transaction.Helper.TransactionHelper;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.TransactionService;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.HomeCook.Entity.CookPost;
//import com.example.demo.HomeCook.Service.CookPostService;
import com.example.demo.HomeCook.Service.CookPostService;
import com.example.demo.Family.Service.Family.FamilyService;
import com.example.demo.Family.Entity.Family;
import com.example.demo.User.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private CookPostService cookPostService;

    @Scheduled(fixedDelay = 55000)
    public void notifyAboutUpComingEvents() {
//        Date start = new Date();

//        log.info("Checking for upcoming events ...");

        ArrayList<Family> families = familyService.findAllFamily();
        Helper helper = Helper.getInstance();

        for (var family : families) {
            if (family.getId() == 0) continue;
//            log.debug(TimeZone.getTimeZone(family.getTimezone()).getDisplayName());
            ArrayList<Event> eventsIn30Mins = eventService.findAllUpComingEventsIn30Mins(family.getTimezone());
            String langCode = helper.getLangCode(family);

//            log.info(String.format("Notifying %d events ...", eventsIn30Mins.size()));
            for (var event : eventsIn30Mins) {
                if (event.getEventAssignUsers().isEmpty()) {
                    firebaseMessageHelper.notifyAllUsersInFamily(
                            event.getFamily(),
                            helper.getMessageInLanguage("anUpComingEventIn30MinsTitle", langCode),
                            String.format(
                                    helper.getMessageInLanguage("anUpComingEventIn30MinsBody", langCode),
                                    event.getFromAsString()
                            ),
                            new HashMap<>() {{
                                put("navigate", "EVENT_DETAIL");
                                put("id", Integer.toString(event.getId()));
                            }}
                    );
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
                            new HashMap<>() {{
                                put("navigate", "EVENT_DETAIL");
                                put("id", Integer.toString(event.getId()));
                            }}
                    );
                }
                event.setNotified(true);
                eventService.saveEvent(event);
            }
        }

//        Date end = new Date();

//        log.info(String.format("Checking upcoming events completes in %d milliseconds", end.getTime() - start.getTime()));
    }

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void generateNextTransaction() {
        Date now = new Date();
        String nowAsString = Helper.getInstance().formatDateWithTimeForQuery(now);
        nowAsString = nowAsString.split(" ")[0];

        ArrayList<Transaction> transactions = transactionService.findAllFromTo(
                nowAsString + " 00:00:00",
                nowAsString + " 59:59:59");

        for (var transaction : transactions) {
            transactionHelper.createTransactionFromAvailableOne(transaction);
        }
    }

    @Scheduled(fixedDelay = 45000)
    public void reRankCookPosts() {
        ArrayList<CookPost> cookPosts = cookPostService.findAll();

        for (var item : cookPosts) {
            item.calculateRank();
            cookPostService.save(item);
        }
    }
}
