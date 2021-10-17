package com.example.demo.domain;

import com.example.demo.Helpers.Helper;

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

    @Column(name = "name")
    private String familyName;

    @Column(name = "thumbnail")
    private String thumbnail;

    @OneToMany(mappedBy = "family", fetch = FetchType.EAGER)
    private Set<UserInFamily> usersInFamily = new HashSet<>();

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

    public void addUser(UserInFamily userInFamily){
        usersInFamily.add(userInFamily);
    }

    public boolean checkIfUserExist(User user){
        return usersInFamily.stream().filter(userInFamily ->
            userInFamily.getUser().getId() == user.getId()
        ).findAny().isPresent();
    }

    public UserInFamily deleteUser(User user){
        UserInFamily userInFamily = usersInFamily.stream().filter(userInFamily1 ->
                userInFamily1.getUser().getId() == user.getId()
        ).findFirst().orElse(null);

        if(userInFamily != null){
            usersInFamily.removeIf(userInFamily1 -> userInFamily1.equals(userInFamily));
        }

        return userInFamily;
    }

    public void deleteUser(UserInFamily userInFamily){
        usersInFamily.removeIf(userInFamily1 -> userInFamily1.equals(userInFamily));
    }

    public HashMap<String, Object> getJson(boolean getThumbnail){
        return new HashMap<>(){{
            put("familyName", familyName);
            put("familyId", id);
            put("thumbnail", ((getThumbnail) ? Helper.getInstance().createSharedLink(thumbnail) : thumbnail));
            put("memberNum", usersInFamily.size());
        }};
    }

    public HashMap<String, Object> getJsonInDetail(boolean getThumbnail){
        return new HashMap<>(){{
            put("familyName", familyName);
            put("familyId", id);
            put("thumbnail", ((getThumbnail) ? Helper.getInstance().createSharedLink(thumbnail) : thumbnail));
            put("memberNum", usersInFamily.size());
        }};
    }
}
