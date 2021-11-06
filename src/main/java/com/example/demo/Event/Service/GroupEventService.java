package com.example.demo.Event.Service;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Event.Entity.GroupEvent;
import com.example.demo.Event.Repo.GroupEventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class GroupEventService {
    @Autowired
    private GroupEventRepo groupEventRepo;

    public GroupEvent saveGroupEvent(GroupEvent groupEvent){
        return groupEventRepo.save(groupEvent);
    }

    public ArrayList<GroupEvent> findAllEventsByHeadEventId(int eventId){
        return groupEventRepo.findAllSubEventsByHeadEvent(eventId);
    }

    public ArrayList<GroupEvent> findAllEventsByHeadEventIdWithPagination(int eventId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return groupEventRepo.findAllSubEventsByHeadEventWithPagination(eventId, pageable);
    }

    public Integer findHeadEventId(int eventId){
        return groupEventRepo.findHeadEventIdByEventId(eventId);
    }

    public Integer deleteAllSubEventsInGroup(int headEventId){
        return groupEventRepo.deleteAllSubEventsInGroup(headEventId);
    }

    public GroupEvent createGroupEvent(Event headEvent, Event subEvent){
        GroupEvent groupEvent = new GroupEvent();

        groupEvent.setHeadEventId(headEvent.getId());
        groupEvent.setSubEvent(subEvent);

        return groupEventRepo.save(groupEvent);
    }
}
