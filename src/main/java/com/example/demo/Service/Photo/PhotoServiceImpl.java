package com.example.demo.Service.Photo;

import com.example.demo.Repo.PhotoRepo;
import com.example.demo.domain.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private PhotoRepo photoRepo;

    @Override
    public Photo savePhoto(Photo photo) {
        return  photoRepo.save(photo);
    }

    @Override
    public Photo getByName(String name) {
        return photoRepo.getByName(name);
    }

    @Override
    public Photo getById(int id) {
        return photoRepo.getById(id);
    }
}
