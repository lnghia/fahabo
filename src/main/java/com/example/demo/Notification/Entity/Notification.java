package com.example.demo.Notification.Entity;

import com.example.demo.Helpers.Helper;
import com.example.demo.Family.Entity.Family;
import com.example.demo.User.Entity.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_clicked")
    private boolean isClicked = false;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "family_id", referencedColumnName = "id")
    private Family family;

    @Column(name = "navigate")
    private String navigate;

    @Column(name = "sub_id")
    private String subId;

    public Notification() {
    }

    public Notification(String type, String title, String description, Date createdAt, User user, Family family, String navigate, String subId) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.user = user;
        this.family = family;
        this.navigate = navigate;
        this.subId = subId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getNavigate() {
        return navigate;
    }

    public void setNavigate(String navigate) {
        this.navigate = navigate;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public HashMap<String, Object> getJson() {
        HashMap<String, Object> rs = new HashMap<>();
        Helper helper = Helper.getInstance();

        rs.put("id", id);
        rs.put("type", type);
        rs.put("title", title);
        rs.put("description", description);
        rs.put("created_at", helper.formatDateWithTimeAsTimezone(createdAt, family.getTimezone()));
        rs.put("isClicked", isClicked);
        rs.put("data", new HashMap<String, Object>() {{
            put("navigate", navigate);
            put("id", subId);
            put("familyId", Integer.toString(family.getId()));
        }});

        return rs;
    }
}
