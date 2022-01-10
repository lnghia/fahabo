package com.example.demo.UserInFamily.Entity;

import com.example.demo.Family.Entity.Family;
import com.example.demo.User.Entity.User;
import com.example.demo.domain.IdClasses.UserInFamilyIdClass;
import com.example.demo.domain.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;

@Entity
@Table(name = "users_in_families")
@IdClass(UserInFamilyIdClass.class)
public class UserInFamily implements Serializable {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "family_id")
    private int familyId;

    @Column(name = "role_id")
    private int roleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "families", referencedColumnName = "id")
    private Family family;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles", referencedColumnName = "id")
    private Role role;

    @Column(name = "count_chat")
    private int countChat = 0;

    public UserInFamily(){}

    public UserInFamily(User user, Family family) {
        this.user = user;
        this.family = family;
        userId = user.getId();
        familyId = family.getId();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
        this.familyId = family.getId();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        this.roleId = role.getId();
    }

    public int getCountChat() {
        return countChat;
    }

    public void setCountChat(int countChat) {
        this.countChat = countChat;
    }

    public HashMap<String, Object> getJson(){
        return new HashMap<>(){{
           put("userId", userId);
           put("familyId", familyId);
           put("user", user.getJson());
           put("family", family.getJson(false));
        }};
    }
}
