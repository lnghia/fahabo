package com.example.demo.ExpensesAndIncomes.Transaction.Repo;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface TransactionAlbumRepo extends JpaRepository<TransactionAlbum, Integer> {
}
