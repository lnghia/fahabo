package com.example.demo.ExpensesAndIncomes.Transaction.Repo;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionGroups;
import com.example.demo.ExpensesAndIncomes.Transaction.IdClass.TransactionGroupIdClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransactionGroupRepo extends JpaRepository<TransactionGroups, TransactionGroupIdClass> {
    @Query(value = "UPDATE transaction_groups SET is_deleted=TRUE WHERE head_id=:headId AND sub_id=:subId", nativeQuery = true)
    @Modifying
    int deleteTransactionInGroup(@Param("headId") int headId,
                                 @Param("subId") int subId);

    @Query(value = "UPDATE transaction_groups SET is_deleted=TRUE WHERE head_id=:headId", nativeQuery = true)
    @Modifying
    int deleteAllTransactionsInGroup(@Param("headId") int headId);

    @Query(value = "SELECT * FROM transaction_groups WHERE is_deleted=FALSE AND head_id=:headId", nativeQuery = true)
    ArrayList<TransactionGroups> findAllTransactionsInGroup(@Param("headId") int headId);

    @Query(value = "SELECT * FROM transaction_groups WHERE sub_id=:subId", nativeQuery = true)
    TransactionGroups findTransactionGroupBySubId(@Param("subId") int subId);
}
