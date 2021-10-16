package com.example.demo.domain;

import com.example.demo.Helpers.Helper;

import javax.persistence.*;
import java.util.*;

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

    @ManyToMany(cascade = CascadeType.REMOVE, mappedBy = "families", fetch = FetchType.EAGER)
    private Set<User> members;

    @ManyToMany(mappedBy = "families")
    private Set<Role> roles;

    public Family() {
        members = new HashSet<>();
        roles = new HashSet<>();
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

    public Set<User> getMembers() {
        if(members == null) members = new HashSet<>();
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public Set<Role> getRoles() {
        if(roles == null) roles = new HashSet<>();
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    public HashMap<String, Object> getJson(boolean getThumbnail){
        return new HashMap<>(){{
            put("familyName", familyName);
            put("familyId", id);
            put("thumbnail", ((getThumbnail) ? Helper.getInstance().createSharedLink(thumbnail) : thumbnail));
            put("memberNum", members.size());
        }};
    }
}
