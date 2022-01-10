package com.example.demo.Chore.Entity;

import com.example.demo.Chore.IdClasses.ChoresAssignUsersIdClass;
import com.example.demo.User.Entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Table(name = "chores_assign_users")
@IdClass(ChoresAssignUsersIdClass.class)
@Slf4j
public class ChoresAssignUsers {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "chore_id")
    private int choreId;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "assignee", referencedColumnName = "id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "chore", referencedColumnName = "id")
    private Chore chore;

    public ChoresAssignUsers() {
    }

    public ChoresAssignUsers(int userId, int choreId, boolean isDeleted, User assignee, Chore chore) {
        this.userId = userId;
        this.choreId = choreId;
        this.isDeleted = isDeleted;
        this.assignee = assignee;
        this.chore = chore;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChoreId() {
        return choreId;
    }

    public void setChoreId(int choreId) {
        this.choreId = choreId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
        this.userId = assignee.getId();
    }

    public Chore getChore() {
        return chore;
    }

    public void setChore(Chore chore) {
        this.chore = chore;
        this.choreId = chore.getId();
    }
}
