package com.example.demo.Event.Entity;

import com.example.demo.domain.IdClasses.GroupEventIdClass;

import javax.persistence.*;

@Entity
@Table(name = "group_event")
@IdClass(GroupEventIdClass.class)
public class GroupEvent {
    @Id
    @Column(name = "head_event_id")
    private int headEventId;

    @Id
    @Column(name = "sub_event_id")
    private int subEventId;

    @ManyToOne
    @JoinColumn(name = "sub_event", referencedColumnName = "id")
    private Event subEvent;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public int getHeadEventId() {
        return headEventId;
    }

    public void setHeadEventId(int headEventId) {
        this.headEventId = headEventId;
    }

    public int getSubEventId() {
        return subEventId;
    }

    public void setSubEventId(int subEventId) {
        this.subEventId = subEventId;
    }

    public Event getSubEvent() {
        return subEvent;
    }

    public void setSubEvent(Event subEvent) {
        this.subEvent = subEvent;
        this.subEventId = subEvent.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
