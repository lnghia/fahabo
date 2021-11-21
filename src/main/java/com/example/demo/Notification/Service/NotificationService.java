package com.example.demo.Notification.Service;

import com.example.demo.Notification.Entity.Notification;
import com.example.demo.Notification.Repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepo notificationRepo;

    public ArrayList<Notification> getNotificationsByUserIdAndFamilyId(int userId, int familyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return notificationRepo.getNotificationsByUserIdAndFamilyId(userId, familyId, pageable);
    }

    public Notification saveNotification(Notification notification){
        return notificationRepo.save(notification);
    }

    public Notification getByUserIdAndId(int userId, int id){
        return notificationRepo.getByUserIdAndId(userId, id);
    }
}
