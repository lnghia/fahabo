package com.example.demo.ExpensesAndIncomes.Transaction.IdClass;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionAlbum;
import com.example.demo.domain.Photo;

import java.io.Serializable;

public class PhotosInTransactionsIdClass implements Serializable {
    private int photoId;
    private int transactionAlbumId;
}
