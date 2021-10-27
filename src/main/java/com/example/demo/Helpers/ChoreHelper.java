package com.example.demo.Helpers;

import com.example.demo.Service.Chore.ChoreService;
import com.example.demo.Service.ChoresAssignUsers.ChoresAssignUsersService;
import com.example.demo.Service.UserService;
import com.example.demo.domain.Chore;
import com.example.demo.domain.ChoresAssignUsers;
import com.example.demo.domain.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Component
public class ChoreHelper {
    @Autowired
    private ChoreService choreService;

    @Autowired
    private ChoresAssignUsersService choresAssignUsersService;

    @Autowired
    private UserService userService;

    public void assignUser(int[] assigneeIds, Chore chore){
        for(var assigneeId : assigneeIds){
            User assignee = userService.getUserById(assigneeId);
            ChoresAssignUsers choresAssignUsers = new ChoresAssignUsers();
            choresAssignUsers.setAssignee(assignee);
            choresAssignUsers.setChore(chore);
            chore.getChoresAssignUsers().add(choresAssignUsers);
            assignee.getChoresAssignUsers().add(choresAssignUsers);
        }
    }

    public Date getNewDeadline(Date deadline, String repeatType){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);

        switch (repeatType){
            case "DAILY":
                calendar.add(Calendar.DATE, 1);
                break;
            case "WEEKLY":
                calendar.add(Calendar.DATE, 7);
                break;
            case "MONTHLY":
                calendar.add(Calendar.MONTH, 1);
                break;
        }

        return calendar.getTime();
    }
//    public Chore[] findWithFilterSortedBy(User user, Family family, String[] status, String title, String sortedBy, int page, int size){
//        ArrayList<Chore> chores = choreService.findAllByFamilyId(family.getId());
//
//        if(user != null){
//            chores = chores.stream().map(chore -> {
//                return chore.getChoresAssignUsers().stream().filter(choresAssignUsers -> {
//                    return choresAssignUsers.getAssignee().getId() == user.getId();
//                })
//            })
//        }
//    }
}
