package com.example.demo.ExpensesAndIncomes.TransactionCategory.Repo;

import com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransactionCategoryRepo extends JpaRepository<TransactionCategory, Integer> {
    @Query(value = "SELECT * FROM transaction_categories WHERE is_deleted=FALSE " +
            "AND (type=CAST(:type as text) OR :type IS NULL OR CAST(:type as text)='') " +
            "AND (family_id IS NULL OR family_id=:familyId)", nativeQuery = true)
    ArrayList<TransactionCategory> findAll(@Param("familyId") int familyId,
                                           @Param("type") String type);

    @Query(value = "SELECT * FROM transaction_categories WHERE is_deleted=FALSE AND (family_id IS NULL OR family_id=:familyId) AND id=:id", nativeQuery = true)
    TransactionCategory findById(@Param("familyId") int familyId,
                                 @Param("id") int id);
}
