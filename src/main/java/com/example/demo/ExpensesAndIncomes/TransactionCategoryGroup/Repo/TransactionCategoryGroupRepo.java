package com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Repo;

import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Entity.TransactionCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransactionCategoryGroupRepo extends JpaRepository<TransactionCategoryGroup, Integer> {
    @Query(value = "SELECT * FROM transaction_category_groups WHERE is_deleted=FALSE", nativeQuery = true)
    ArrayList<TransactionCategoryGroup> findAll();

    @Query(value = "SELECT * FROM transaction_category_groups WHERE is_deleted=FALSE AND id=:id", nativeQuery = true)
    TransactionCategoryGroup findById(@Param("id") int id);
}
