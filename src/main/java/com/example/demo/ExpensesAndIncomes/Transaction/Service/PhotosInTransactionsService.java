package com.example.demo.ExpensesAndIncomes.Transaction.Service;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.PhotosInTransactions;
import com.example.demo.ExpensesAndIncomes.Transaction.Repo.PhotosInTransactionsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhotosInTransactionsService {
    @Autowired
    private PhotosInTransactionsRepo photosInTransactionsRepo;

    public PhotosInTransactions save(PhotosInTransactions photosInTransactions){
        return photosInTransactionsRepo.save(photosInTransactions);
    }

    @Transactional
    public void delete(PhotosInTransactions photosInTransactions){
        photosInTransactions.setDeleted(true);
        save(photosInTransactions);
    }

    @Transactional
    public int delete(int photoId, int albumId){
        return photosInTransactionsRepo.deletePhotosInTransactions(photoId, albumId);
    }
}
