package com.example.demo.MetaModel;

import com.example.demo.domain.ChoresAssignUsers;
import com.example.demo.domain.User;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ChoresAssignUsers.class)
public class ChoresAssignUsers_ {
    public static volatile SingularAttribute<ChoresAssignUsers, User> assignee;
}
