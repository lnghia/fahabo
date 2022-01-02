package com.example.demo.HomeCook.Service;

import com.example.demo.HomeCook.Entity.CookPost;
import com.example.demo.HomeCook.Repo.CookPostRepo;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CookPostService {
    @Autowired
    private CookPostRepo cookPostRepo;

    public ArrayList<CookPost> findAll() {
        return cookPostRepo.findAll();
    }

    public CookPost save(CookPost cookPost) {
        return cookPostRepo.save(cookPost);
    }

    public CookPost findById(int id) {
        return cookPostRepo.findById(id);
    }

    public ArrayList<CookPost> findAll(String searchText, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return cookPostRepo.findAll(searchText, pageable);
    }

    public ArrayList<CookPost> findAllByAuthor(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return cookPostRepo.findAllByAuthor(userId, pageable);
    }
}
