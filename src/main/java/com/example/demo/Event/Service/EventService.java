package com.example.demo.Event.Service;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.Repo.EventRepo;
import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepo eventRepo;

    public Event saveEvent(Event event){
        return eventRepo.save(event);
    }

    public Event getById(int id){
        return eventRepo.getById(id);
    }

    public void deleteEventsInFamily(int familyId){
        eventRepo.deleteEventsInFamily(familyId);
    }

    public ArrayList<Event> findAll(List<Integer> user,
                                   Family family,
                                   String title,
                                   boolean sortByDeadLine,
                                   Date from,
                                   Date to,
                                   int page,
                                   int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<String> users = (user != null) ? user.stream().map(user1 -> {
            return Integer.toString(user1);
        }).collect(Collectors.toList()) : List.of();

        ArrayList<Event> events = eventRepo.findAlLFilteredByUserAndStatusAndTitleSortedByCreatedAtOrDeadLine(
                family.getId(),
                users,
//                sb.toString(),
                title,
                sortByDeadLine,
                (from != null) ? Helper.getInstance().formatDateWithTimeForQuery(from) : "",
                (to != null) ? Helper.getInstance().formatDateWithTimeForQuery(to) : "",
                pageable
        );

        return events;
    }
}
