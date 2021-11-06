package com.example.demo.Service.Chore;

import com.example.demo.Helpers.Helper;
import com.example.demo.Repo.ChoreRepo;
import com.example.demo.Service.ChoreAlbum.ChoreAlbumService;
import com.example.demo.Service.ChoresAssignUsers.ChoresAssignUsersService;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.Service.PhotoInChore.PhotoInChoreService;
import com.example.demo.domain.Chore;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
//import static com.example.demo.Specifications.ChoreSpecification.hasStatus;

@Service
public class ChoreService {
    @Autowired
    private ChoreRepo choreRepo;

    @Autowired
    private ChoresAssignUsersService choresAssignUsersService;

    @Autowired
    private ChoreAlbumService choreAlbumService;

    @Autowired
    private PhotoInChoreService photoInChoreService;

    @Autowired
    private PhotoService photoService;


    public Chore saveChore(Chore chore) {
        return choreRepo.save(chore);
    }

    public Chore getById(int id) {
        return choreRepo.getById(id);
    }

    public ArrayList<Chore> findAllByFamilyId(int familyId) {
        return choreRepo.findByFamilyId(familyId);
    }

    public ArrayList<Chore> findAll(List<User> user, Family family, List<String> status, String title, boolean sortByDeadLine, Date from, Date to, int page , int size) throws ParseException {
        Pageable pageable = PageRequest.of(page, size);

        List<String> users = (user != null) ? user.stream().map(user1 -> {
            return Integer.toString(user1.getId());
        }).collect(Collectors.toList()) : null;
//
//        StringBuilder sb = new StringBuilder("");
//        if(user != null){
//            for(var u : user){
//                sb.append(Integer.toString(u.getId()) + ' ');
//            }
//        }

        ArrayList<Chore> chores = choreRepo.findAlLFilteredByUserAndStatusAndTitleSortedByCreatedAtOrDeadLine(
                family.getId(),
                users,
//                sb.toString(),
                status,
                title,
                sortByDeadLine,
                (from != null) ? Helper.getInstance().formatDateForQuery(from) : "",
                (to != null) ? Helper.getInstance().formatDateForQuery(to) : "",
                pageable
        );

        return chores;
    }

    public void deleteChoresInFamily(int familyId){
        choreRepo.deleteChoresInFamily(familyId);
    }
}
