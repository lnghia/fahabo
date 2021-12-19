package com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Service;

import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Entity.TransactionCategoryGroup;
import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Repo.TransactionCategoryGroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TransactionCategoryGroupService {
    @Autowired
    private TransactionCategoryGroupRepo transactionCategoryGroupRepo;

    public ArrayList<TransactionCategoryGroup> findAll(){
        return transactionCategoryGroupRepo.findAll();
    }

    public TransactionCategoryGroup findById(int id){
        return transactionCategoryGroupRepo.findById(id);
    }
}
