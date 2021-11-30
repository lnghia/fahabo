package com.example.demo.ExpensesAndIncomes.Transaction.Repo;

import com.example.demo.ExpensesAndIncomes.Transaction.Entity.PhotosInTransactions;
import com.example.demo.ExpensesAndIncomes.Transaction.IdClass.PhotosInTransactionsIdClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotosInTransactionsRepo extends JpaRepository<PhotosInTransactions, PhotosInTransactionsIdClass> {
    @Modifying
    @Query(value = "UPDATE photos_in_transactions SET is_deleted=TRUE WHERE photo_id=:photoId AND transaction_album_id=:albumId", nativeQuery = true)
    int deletePhotosInTransactions(@Param("photoId") int photoId,
                                   @Param("albumId") int albumId);
}
