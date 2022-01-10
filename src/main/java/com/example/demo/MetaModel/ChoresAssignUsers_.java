package com.example.demo.MetaModel;

import com.example.demo.Chore.Entity.ChoresAssignUsers;
import com.example.demo.User.Entity.User;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ChoresAssignUsers.class)
public class ChoresAssignUsers_ {
    public static volatile SingularAttribute<ChoresAssignUsers, User> assignee;
}
