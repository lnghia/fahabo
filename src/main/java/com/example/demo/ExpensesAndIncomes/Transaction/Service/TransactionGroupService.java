package com.example.demo.ExpensesAndIncomes.Transaction.Service;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionGroups;
import com.example.demo.ExpensesAndIncomes.Transaction.Repo.TransactionGroupRepo;
import com.example.demo.ExpensesAndIncomes.Transaction.Repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
public class TransactionGroupService {
    @Autowired
    private TransactionGroupRepo transactionGroupRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    public TransactionGroups save(TransactionGroups transactionGroups){
        return transactionGroupRepo.save(transactionGroups);
    }

    @Transactional
    public int deleteTransactionInGroup(int headId, int subId){
        TransactionGroups transactionGroups = findTransactionGroupBySubId(subId);
        transactionGroups.getSub().setIs_deleted(true);
        transactionRepo.save(transactionGroups.getSub());
        return transactionGroupRepo.deleteTransactionInGroup(headId, subId);
    }

    public TransactionGroups findTransactionGroupBySubId(int subId){
        return transactionGroupRepo.findTransactionGroupBySubId(subId);
    }

    public ArrayList<TransactionGroups> findAllTransactionsInGroup(int headId){
        return transactionGroupRepo.findAllTransactionsInGroup(headId);
    }

    @Transactional
    public int deleteAllTransactionsInGroup(int headTransactionId){
        ArrayList<TransactionGroups> transactionGroups = findAllTransactionsInGroup(headTransactionId);

        for(var item : transactionGroups){
            item.getSub().setIs_deleted(true);
            transactionRepo.save(item.getSub());
        }

        return transactionGroupRepo.deleteAllTransactionsInGroup(headTransactionId);
    }
}
