package com.example.demo.UserFirebaseToken.Entity;

import com.example.demo.UserFirebaseToken.IdClass.UserFirebaseTokenIdClass;
import com.example.demo.domain.User;

import javax.persistence.*;

@Entity
@Table(name = "user_firebase_token")
@IdClass(UserFirebaseTokenIdClass.class)
public class UserFirebaseToken {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "users", referencedColumnName = "id")
    private User user;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public UserFirebaseToken() {
    }

    public UserFirebaseToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.userId = user.getId();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
