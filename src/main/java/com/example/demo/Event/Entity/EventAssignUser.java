package com.example.demo.Event.Entity;

import com.example.demo.domain.IdClasses.EventAssignUserId;
import com.example.demo.User.Entity.User;

import javax.persistence.*;

@Entity
@Table(name = "events_assign_users")
@IdClass(EventAssignUserId.class)
public class EventAssignUser {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "event_id")
    private int eventId;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "assignee", referencedColumnName = "id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "id")
    private Event event;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
        userId = assignee.getId();
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        eventId = event.getId();
    }
}
