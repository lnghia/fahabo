package com.example.demo.HomeCook.Entity;

import com.example.demo.HomeCook.IdClass.UserReactCookPostIdClass;
import com.example.demo.domain.User;
import liquibase.pro.packaged.C;

import javax.persistence.*;

@Entity
@Table(name = "users_react_cookposts")
@IdClass(UserReactCookPostIdClass.class)
public class UserReactCookPost {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "cookpost_id")
    private int cookPostId;

    @ManyToOne
    @JoinColumn(name = "users", referencedColumnName = "id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "cookposts", referencedColumnName = "id")
    private CookPost cookposts;

    @Column(name = "reaction")
    private int reaction = 0;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public UserReactCookPost() {
    }

    public UserReactCookPost(User user, CookPost cookPost) {
        this.users = user;
        this.userId = user.getId();
        this.cookposts = cookPost;
        this.cookPostId = cookPost.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public User getUser() {
        return users;
    }

    public void setUser(User user) {
        this.users = user;
        this.userId = user.getId();
    }

    public CookPost getCookPost() {
        return cookposts;
    }

    public void setCookPost(CookPost cookPost) {
        this.cookposts = cookPost;
        this.cookPostId = cookPost.getId();
    }

    public int getReaction() {
        return reaction;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCookPostId() {
        return cookPostId;
    }

    public void setCookPostId(int cookPostId) {
        this.cookPostId = cookPostId;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
