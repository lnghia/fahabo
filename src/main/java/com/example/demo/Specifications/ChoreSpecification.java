package com.example.demo.Specifications;

import com.example.demo.MetaModel.ChoresAssignUsers_;
import com.example.demo.Chore.Entity.Chore;
import com.example.demo.Chore.Entity.ChoresAssignUsers;
import com.example.demo.Family.Entity.Family;
import com.example.demo.User.Entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

public class ChoreSpecification {
//    public static Specification<Chore> hasStatus(String status) {
//        return (root, criteriaQuery, cb) -> cb.and(cb.equal(root.get("isDeleted"), false), cb.equal(root.get("status"), status));
//    }

    public static Specification<Chore> hasAssignee(User user, Family family) {
        return (Root<Chore> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) -> {
            Join<Chore, ChoresAssignUsers> choreChoresAssignUsersJoin = root.join("choresAssignUsers");
            if(choreChoresAssignUsersJoin == null) return null;
            Join<ChoresAssignUsers, User> choresAssignUsersUserJoin = choreChoresAssignUsersJoin.join(ChoresAssignUsers_.assignee);

            return root.in(cb.and(
                    cb.equal(choresAssignUsersUserJoin.get("isDeleted"), false),
//                    cb.equal(choreChoresAssignUsersJoin.get("chore").get("family").get("id"), family.getId()),
                    cb.equal(choresAssignUsersUserJoin.get("assignee").get("id"), user.getId())));
        };
    }
}
