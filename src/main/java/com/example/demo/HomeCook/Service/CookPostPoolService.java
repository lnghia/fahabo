package com.example.demo.HomeCook.Service;

import com.example.demo.HomeCook.Entity.CookPostPool;
import com.example.demo.HomeCook.Repo.CookPostPoolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CookPostPoolService {
    @Autowired
    private CookPostPoolRepo cookPostPoolRepo;

    public ArrayList<CookPostPool> findAllByUser(int userId, String searchText, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return cookPostPoolRepo.findAllByUser(userId, searchText, pageable);
    }

    public CookPostPool save(CookPostPool cookPostPool){
        return cookPostPoolRepo.save(cookPostPool);
    }
}
