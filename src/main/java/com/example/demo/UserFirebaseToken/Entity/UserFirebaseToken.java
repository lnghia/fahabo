package com.example.demo.UserFirebaseToken.Entity;

import com.example.demo.UserFirebaseToken.IdClass.UserFirebaseTokenIdClass;
import com.example.demo.User.Entity.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users")
    private User user;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "altitude")
    private BigDecimal altitude;

    @Column(name = "updated_at")
    private Date updated_at;

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

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
