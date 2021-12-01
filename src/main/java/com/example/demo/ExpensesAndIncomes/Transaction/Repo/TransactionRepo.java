package com.example.demo.ExpensesAndIncomes.Transaction.Repo;

import com.example.demo.Event.Entity.Event;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    @Query(value = "SELECT * " +
            "FROM transactions " +
            "WHERE (is_deleted=FALSE " +
            "AND family_id=:familyId " +
            "AND (:title IS NULL OR :title='' OR LOWER(title)\\:\\:text LIKE '%' || :title\\:\\:text || '%') " +
            "AND (:from='' OR :to='' OR (cast(date as VARCHAR) >= :from AND cast(date as VARCHAR) <= :to))) " +
            "ORDER BY created_at DESC",
            countQuery = "SELECT COUNT(id) FROM transactions " +
                    "WHERE (is_deleted=FALSE " +
                    "AND family_id=:familyId " +
                    "AND (:title IS NULL OR :title='' OR LOWER(title)\\:\\:text LIKE '%' || :title\\:\\:text || '%') " +
                    "AND (:from='' OR :to='' OR (cast(date as VARCHAR) >= :from AND cast(date as VARCHAR) <= :to))) ",
            nativeQuery = true)
    ArrayList<Transaction> findAll(@Param("familyId") int familyId,
                                   @Param("title") String title,
                                   @Param("from") String from,
                                   @Param("to") String to,
                                   Pageable pageable);

    @Query(value = "SELECT * FROM transactions WHERE is_deleted=FALSE AND id=:id", nativeQuery = true)
    Transaction findById(@Param("id") int id);

    @Query(value = "SELECT * FROM transactions WHERE is_deleted=FALSE " +
            "AND (repeat_type IS NOT NULL OR NOT repeat_type='') " +
            "AND (cast(date as VARCHAR) >= :from AND cast(date as VARCHAR) <= :to)",
            nativeQuery = true)
    ArrayList<Transaction> findAllFromTo(@Param("from") String from,
                                         @Param("to") String to);

    @Query(value = "SELECT * FROM transactions WHERE is_deleted=FALSE AND family_id=:familyId " +
            "AND DATE_PART('month', date)=:month AND DATE_PART('year', date)=:year", nativeQuery = true)
    ArrayList<Transaction> findTransactionsInMonthYear(@Param("month") int month,
                                                       @Param("year") int year,
                                                       @Param("familyId") int familyId);
}
