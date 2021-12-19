package com.example.demo.HomeCook.Entity;

import com.example.demo.HomeCook.IdClass.UserReactCookPostIdClass;
import com.example.demo.domain.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cook_post_pool")
@IdClass(UserReactCookPostIdClass.class)
public class CookPostPool {
    @Id
    private int userId;

    @Id
    private int cookPostId;

    @Column(name = "showed")
    private boolean showed = false;

    @ManyToOne
    @JoinColumn(name = "users", referencedColumnName = "id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "cookposts", referencedColumnName = "id")
    private CookPost cookposts;

    @Column(name = "added_date")
    private Date addedDate;

    public CookPostPool() {
    }

    public CookPostPool(User user, CookPost cookPost) {
        this.users = user;
        this.userId = user.getId();
        this.cookposts = cookPost;
        this.cookPostId = cookPost.getId();
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
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

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
        this.userId = users.getId();
    }

    public CookPost getCookposts() {
        return cookposts;
    }

    public void setCookposts(CookPost cookposts) {
        this.cookposts = cookposts;
        this.cookPostId = cookposts.getId();
    }

    public boolean isShowed() {
        return showed;
    }

    public void setShowed(boolean showed) {
        this.showed = showed;
    }
}
