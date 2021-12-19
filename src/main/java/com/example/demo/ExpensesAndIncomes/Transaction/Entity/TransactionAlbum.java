package com.example.demo.ExpensesAndIncomes.Transaction.Entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transaction_albums")
public class TransactionAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private Transaction transaction;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "transactionAlbum")
    private Set<PhotosInTransactions> photos = new HashSet<>();

    public TransactionAlbum() {
    }

    public TransactionAlbum(Transaction transaction) {
        this.transaction = transaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Set<PhotosInTransactions> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<PhotosInTransactions> photos) {
        this.photos = photos;
    }
}
