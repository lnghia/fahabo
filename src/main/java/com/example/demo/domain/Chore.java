package com.example.demo.domain;

import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Family.Family;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "chores")
@Slf4j
public class Chore {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "family_id", referencedColumnName = "id")
    private Family family;

    @Column(name = "status")
    private String status;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private Date deadline;

    @ManyToOne
    @JoinColumn(name = "reporter", referencedColumnName = "id")
    private User reporter;

    @Column(name = "repeat_type")
    private String repeatType;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "chore")
    private Set<ChoreAlbum> choreAlbumSet = new HashSet<>();

    @OneToMany(mappedBy = "chore", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ChoresAssignUsers> choresAssignUsers = new HashSet<>();

    public Chore() {
    }

    public Chore(Family family, String status, String title, String description, Date deadline, User reporter, String repeatType, boolean isDeleted) {
        this.family = family;
        this.status = status;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.reporter = reporter;
        this.repeatType = repeatType;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getDeadline() {
        return deadline;
    }

    public String getDeadLine(){
        return Helper.getInstance().formatDate(deadline);
    }

    public String getCreatedAt(){
        return Helper.getInstance().formatDate(createdAt);
    }

    public String getUpdatedAt(){
        return Helper.getInstance().formatDate(updatedAt);
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public User getUser() {
        return reporter;
    }

    public void setUser(User user) {
        this.reporter = user;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ChoreAlbum> getChoreAlbumSet() {
        return choreAlbumSet;
    }

    public Date getCreatedAtAsDate(){
        return createdAt;
    }

    public Date getUpdatedAtAsDate(){
        return updatedAt;
    }

    public void setChoreAlbumSet(Set<ChoreAlbum> choreAlbumSet) {
        this.choreAlbumSet = choreAlbumSet;
    }

    public Set<ChoresAssignUsers> getChoresAssignUsers() {
        return choresAssignUsers;
    }

    public void setChoresAssignUsers(Set<ChoresAssignUsers> choresAssignUsers) {
        this.choresAssignUsers = choresAssignUsers;
    }

    public String getDeadLineAsString(){
        return Helper.getInstance().formatDate(deadline);
    }

    public HashMap<String, Object> getJson() {
        HashMap<String, Object> rs = new HashMap<>(){{
            put("choreId", id);
            put("title", title);
            put("description", description);
            put("status", status);
            put("deadline", getDeadLineAsString());
            put("repeatType", repeatType);
        }};

        User[] assignees = choresAssignUsers.stream().filter(choresAssignUsers1 -> !choresAssignUsers1.isDeleted()).map(choresAssignUsers1 -> {
            return choresAssignUsers1.getAssignee();
        }).toArray(size -> new User[size]);
        Photo[] photos = Arrays.stream(assignees).map(user -> {
            return new Photo(Integer.toString(user.getId()), user.getAvatar());
        }).toArray(size -> new Photo[size]);

        HashMap<String, String> avatars = Helper.getInstance().redirectImageLinks(photos);

        rs.put("assignees", Arrays.stream(assignees).map(user -> {
            return new HashMap<String, Object>(){{
               put("memberId", user.getId());
               put("name", user.getName());
               put("avatar", avatars.containsKey(Integer.toString(user.getId())) ? avatars.get(Integer.toString(user.getId())) : user.getAvatar());
            }};
        }));

        return rs;
    }
}
