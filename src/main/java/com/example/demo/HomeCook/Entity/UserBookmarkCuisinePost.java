package com.example.demo.HomeCook.Entity;

import com.example.demo.User.Entity.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users_bookmark_cuisinepost")
public class UserBookmarkCuisinePost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "users", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "posts", referencedColumnName = "id")
    private CookPost cookPost;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "created_date")
    private Date createdDate;

    public UserBookmarkCuisinePost() {
    }

    public UserBookmarkCuisinePost(User user, CookPost cookPost, Date createdDate) {
        this.user = user;
        this.cookPost = cookPost;
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CookPost getCookPost() {
        return cookPost;
    }

    public void setCookPost(CookPost cookPost) {
        this.cookPost = cookPost;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
