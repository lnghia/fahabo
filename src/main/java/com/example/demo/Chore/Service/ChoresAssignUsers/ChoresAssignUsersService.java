package com.example.demo.Chore.Service.ChoresAssignUsers;

import com.example.demo.Chore.Repo.ChoresAssignUsersRepo;
import com.example.demo.Chore.Entity.ChoresAssignUsers;
import com.example.demo.Family.Entity.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deleteChoreUserRelationByFamilyId(int familyId){
        choresAssignUsersRepo.deleteChoreUserRelationByFamilyId(familyId);
    }
}
