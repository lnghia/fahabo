package com.example.demo.HomeCook.Service;

import com.example.demo.HomeCook.Entity.UserBookmarkCuisinePost;
import com.example.demo.HomeCook.Repo.UsersBookmarkCuisinePostsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UsersBookmarkCuisinePostsService {
    @Autowired
    private UsersBookmarkCuisinePostsRepo usersBookmarkCuisinePostsRepo;

    public UserBookmarkCuisinePost save(UserBookmarkCuisinePost userBookmarkCuisinePost) {
        return usersBookmarkCuisinePostsRepo.save(userBookmarkCuisinePost);
    }

    public ArrayList<UserBookmarkCuisinePost> findAllByUserSortByCreatedDate(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return usersBookmarkCuisinePostsRepo.findAllByUserSortByCreatedDate(userId, pageable);
    }
}
