package com.example.demo.HomeCook.Service;

import com.example.demo.HomeCook.Entity.CookPost;
import com.example.demo.HomeCook.Repo.CookPostRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

    public CookPost findById(int id){
        return cookPostRepo.findById(id);
    }
}
