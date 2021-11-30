package com.example.demo.ExpensesAndIncomes.TransactionCategory.Service;

import com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity.TransactionCategory;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Repo.TransactionCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TransactionCategoryService {
    @Autowired
    private TransactionCategoryRepo transactionCategoryRepo;

    public ArrayList<TransactionCategory> findAll(int familyId, String type) {
        return transactionCategoryRepo.findAll(familyId, type);
    }

    public TransactionCategory findById(int id) {
        return transactionCategoryRepo.findById(id);
    }

    public TransactionCategory save(TransactionCategory transactionCategory) {
        return transactionCategoryRepo.save(transactionCategory);
    }

    public TransactionCategory findById(int familyId, int id) {
        return transactionCategoryRepo.findById(familyId, id);
    }
}
