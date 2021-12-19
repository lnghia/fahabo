package com.example.demo.domain.Family;

import com.example.demo.Event.Entity.Event;
import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Album;
import com.example.demo.domain.Chore;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInFamily;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "families")
public class Family {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "name")
    private String familyName;

    @Column(name = "thumbnail")
    private String thumbnail;

    @OneToMany(mappedBy = "family", fetch = FetchType.EAGER)
    private Set<UserInFamily> usersInFamily = new HashSet<>();

    @OneToMany(mappedBy = "family", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Album> albums = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "default_album", referencedColumnName = "id")
    private Album defaultAlbum;

    @OneToMany(mappedBy = "family")
    private Set<Chore> chores = new HashSet<>();

    @OneToMany(mappedBy = "family")
    private Set<Event> events = new HashSet<>();

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

//    @ManyToMany(mappedBy = "families")
//    private Set<Role> roles;

    public Family() {
    }

    public Family(String name, String thumbnail) {
        this.familyName = name;
        this.thumbnail = thumbnail;
    }

    public Family(String name) {
        this.familyName = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Set<UserInFamily> getUsersInFamily() {
        return usersInFamily;
    }

    public void setUsersInFamily(Set<UserInFamily> usersInFamily) {
        this.usersInFamily = usersInFamily;
    }

    public Set<Chore> getChores() {
        return chores;
    }

    public void setChores(Set<Chore> chores) {
        this.chores = chores;
    }

    public void addUser(UserInFamily userInFamily) {
        usersInFamily.add(userInFamily);
    }

    public boolean checkIfUserExist(User user) {
        return usersInFamily.stream().filter(userInFamily ->
                userInFamily.getUser().getId() == user.getId()
        ).findAny().isPresent();
    }

    public UserInFamily deleteUser(User user) {
        UserInFamily userInFamily = usersInFamily.stream().filter(userInFamily1 ->
                userInFamily1.getUser().getId() == user.getId()
        ).findFirst().orElse(null);

        if (userInFamily != null) {
            usersInFamily.removeIf(userInFamily1 -> userInFamily1.equals(userInFamily));
        }

        return userInFamily;
    }

    public void deleteUser(UserInFamily userInFamily) {
        usersInFamily.removeIf(userInFamily1 -> userInFamily1.equals(userInFamily));
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public Album getDefaultAlbum() {
        return defaultAlbum;
    }

    public void setDefaultAlbum(Album defaultAlbum) {
        this.defaultAlbum = defaultAlbum;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public HashMap<String, Object> getJson(boolean getThumbnail) {
        return new HashMap<>() {{
            put("familyName", familyName);
            put("familyId", id);
            put("thumbnail", ((getThumbnail) ? Helper.getInstance().createSharedLink(thumbnail) : thumbnail));
            put("memberNum", usersInFamily.size());
        }};
    }

    public HashMap<String, Object> getJson(String thumbnailRedirected) {
        return new HashMap<>() {{
            put("familyName", familyName);
            put("familyId", id);
            put("thumbnail", ((thumbnailRedirected == null) ? thumbnail : thumbnailRedirected));
            put("memberNum", usersInFamily.size());
        }};
    }

    public HashMap<String, Object> getJsonInDetail(boolean getThumbnail) {
        return new HashMap<>() {{
            put("familyName", familyName);
            put("familyId", id);
            put("thumbnail", ((getThumbnail) ? Helper.getInstance().createSharedLink(thumbnail) : thumbnail));
            put("memberNum", usersInFamily.size());
        }};
    }
}
