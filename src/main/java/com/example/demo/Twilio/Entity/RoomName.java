package com.example.demo.Twilio.Entity;

import javax.annotation.processing.Generated;
import javax.persistence.*;

@Entity
@Table(name = "room_names")
public class RoomName {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "ended")
    private boolean ended = false;

    public RoomName() {
    }

    public RoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }
}
