package com.example.demo.SocialAccountType.Entity;

import com.example.demo.User.Entity.User;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;

@Entity
public class SocialAccountType {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "social_name")
    private String socialName;

    @Column(name = "change_password_url")
    private String changePasswordUrl;

    @OneToMany(mappedBy = "socialAccountType", cascade = CascadeType.ALL)
    private Collection<User> users;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
    }

    public String getChangePasswordUrl() {
        return changePasswordUrl;
    }

    public void setChangePasswordUrl(String changePasswordUrl) {
        this.changePasswordUrl = changePasswordUrl;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public HashMap<String, Object> getJson(){
        HashMap<String, Object> rs = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        rs.put("id", id);
        rs.put("name", socialName);
//        rs.put("users", users.stream().map(User::getUsername).collect(Collectors.toList()));

        return rs;
    }
}
