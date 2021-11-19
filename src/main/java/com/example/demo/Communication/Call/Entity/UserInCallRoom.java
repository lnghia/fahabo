package com.example.demo.Communication.Call.Entity;

import com.example.demo.Communication.Call.IdClass.UserInCallRoomIdClass;
import com.example.demo.domain.User;

import javax.persistence.*;

@Entity
@Table(name = "users_in_call_rooms")
@IdClass(UserInCallRoomIdClass.class)
public class UserInCallRoom {
    @Id
    @Column(name = "room_name")
    private String roomName;

    @Id
    @Column(name = "user_id")
    private int userId;

    @ManyToOne
    @JoinColumn(name = "users", referencedColumnName = "id")
    private User user;

    public UserInCallRoom() {
    }

    public UserInCallRoom(String roomName, User user) {
        this.roomName = roomName;
        this.user = user;
        this.userId = user.getId();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }
}
