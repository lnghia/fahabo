package com.example.demo.Service.ChoresAssignUsers;

import com.example.demo.Repo.ChoresAssignUsersRepo;
import com.example.demo.domain.Chore;
import com.example.demo.domain.ChoresAssignUsers;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.example.demo.Specifications.ChoreSpecification.hasAssignee;

@Service
public class ChoresAssignUsersService {
    @Autowired
    private ChoresAssignUsersRepo choresAssignUsersRepo;

    public ChoresAssignUsers saveChoresAssignUsers(ChoresAssignUsers choresAssignUsers){
        return choresAssignUsersRepo.save(choresAssignUsers);
    }

    public int[] findAllByFamilyId(Family family){
//        Specification conditions = Specification.where(hasAssignee(user, family));
        return choresAssignUsersRepo.findChoreIdsByFamilyId(family.getId());
    }

    public void deleteChoreUserRelationByFamilyId(int familyId){
        choresAssignUsersRepo.deleteChoreUserRelationByFamilyId(familyId);
    }
}
