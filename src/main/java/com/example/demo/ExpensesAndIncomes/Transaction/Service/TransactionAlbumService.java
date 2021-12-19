package com.example.demo.ExpensesAndIncomes.Transaction.Service;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionAlbum;
import com.example.demo.ExpensesAndIncomes.Transaction.Repo.TransactionAlbumRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionAlbumService {
    @Autowired
    private TransactionAlbumRepo transactionAlbumRepo;

    public TransactionAlbum save(TransactionAlbum transactionAlbum) {
        return transactionAlbumRepo.save(transactionAlbum);
    }

    @Transactional
    public void delete(TransactionAlbum transactionAlbum){
        transactionAlbum.setDeleted(true);
        save(transactionAlbum);
    }
}
