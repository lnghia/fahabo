package com.example.demo.Event.Entity;

import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Photo;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "events")
@Slf4j
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "family_id", referencedColumnName = "id")
    private Family family;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "from_time")
    private Date from;

    @Column(name = "to_time")
    private Date to;

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

    @OneToMany(mappedBy = "event")
    private Set<EventAlbum> eventAlbumSet = new HashSet<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private Set<EventAssignUser> eventAssignUsers = new HashSet<>();

    @Column(name = "repeat_occurrences")
    private int repeatOccurrences;

    @Column(name = "notified")
    private boolean notified = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
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

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
        if(repeatType == null || repeatType.isEmpty() || repeatType.isBlank()){
            setRepeatOccurrences(0);
        }
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<EventAlbum> getEventAlbumSet() {
        return eventAlbumSet;
    }

    public void setEventAlbumSet(Set<EventAlbum> eventAlbumSet) {
        this.eventAlbumSet = eventAlbumSet;
    }

    public void addEventAlbum(EventAlbum eventAlbum){
        eventAlbumSet.add(eventAlbum);
    }

    public Set<EventAssignUser> getEventAssignUsers() {
        return eventAssignUsers;
    }

    public void setEventAssignUsers(Set<EventAssignUser> eventAssignUsers) {
        this.eventAssignUsers = eventAssignUsers;
    }

    public int getRepeatOccurrences() {
        return repeatOccurrences;
    }

    public void setRepeatOccurrences(int repeatOccurrences) {
        this.repeatOccurrences = repeatOccurrences;
    }

    //    public String getDeadLineAsString(){
//        return Helper.getInstance().formatDate(deadline);
//    }

    public String getFromAsString(){
        return Helper.getInstance().formatDate(from);
    }

    public String getToAsString(){
        return Helper.getInstance().formatDate(to);
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public HashMap<String, Object> getJson() {
        HashMap<String, Object> rs = new HashMap<>(){{
            put("eventId", id);
            put("title", title);
            put("description", description);
            put("from", getFromAsString());
            put("to", getToAsString());
            put("repeatType", repeatType);
        }};

        Date start = new Date();
        User[] assignees = eventAssignUsers.stream().filter(choresAssignUsers1 -> !choresAssignUsers1.isDeleted()).map(choresAssignUsers1 -> {
            return choresAssignUsers1.getAssignee();
        }).toArray(size -> new User[size]);
        Photo[] photos = Arrays.stream(assignees).map(user -> {
            return new Photo(Integer.toString(user.getId()), user.getAvatar());
        }).toArray(size -> new Photo[size]);
        Date end = new Date();
        log.info("Execution time: " + Long.toString(end.getTime() - start.getTime()));

        start = new Date();
        HashMap<String, String> avatars = Helper.getInstance().redirectImageLinks(photos);
        end = new Date();
        log.info("Execution time: " + Long.toString(end.getTime() - start.getTime()));

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
