package com.example.demo.Service.Photo;

import com.example.demo.Repo.PhotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private PhotoRepo photoRepo;
}
