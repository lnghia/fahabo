package com.example.demo.ExpensesAndIncomes.Transaction.Service;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.Transaction;
import com.example.demo.ExpensesAndIncomes.Transaction.Repo.TransactionRepo;
import com.example.demo.Helpers.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;

    public Transaction save(Transaction transaction){
        return transactionRepo.save(transaction);
    }

    public ArrayList<Transaction> findAllByFamily(int familyId, String title, Date from, Date to, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        ArrayList<Transaction> ans = transactionRepo.findAll(
                familyId,
                title,
                (from != null) ? Helper.getInstance().formatDateWithoutTimeForQuery(from) : "",
                (to != null) ? Helper.getInstance().formatDateWithoutTimeForQuery(to) : "",
                pageable);

        return ans;
    }

    public Transaction findById(int id){
        return transactionRepo.findById(id);
    }

    @Transactional
    public void delete(Transaction transaction){
        transaction.setIs_deleted(true);
        save(transaction);
    }

    public ArrayList<Transaction> findAllFromTo(String from, String to){
        return transactionRepo.findAllFromTo(from, to);
    }

    public ArrayList<Transaction> findTransactionsInMonthYear(int month, int year, int familyId){
        return transactionRepo.findTransactionsInMonthYear(month, year, familyId);
    }
}
