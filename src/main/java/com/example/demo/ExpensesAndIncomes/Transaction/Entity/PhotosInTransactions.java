package com.example.demo.ExpensesAndIncomes.Transaction.Entity;

import com.example.demo.ExpensesAndIncomes.Transaction.IdClass.PhotosInTransactionsIdClass;
import com.example.demo.Album.Entity.Photo;

import javax.persistence.*;

@Entity
@Table(name = "photos_in_transactions")
@IdClass(PhotosInTransactionsIdClass.class)
public class PhotosInTransactions {
    @Id
    @Column(name = "photo_id")
    private int photoId;

    @Id
    @Column(name = "transaction_album_id")
    private int transactionAlbumId;

    @ManyToOne
    @JoinColumn(name = "transaction_album", referencedColumnName = "id")
    private TransactionAlbum transactionAlbum;

    @ManyToOne
    @JoinColumn(name = "photo", referencedColumnName = "id")
    private Photo photo;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public PhotosInTransactions() {
    }

    public PhotosInTransactions(Photo photo, TransactionAlbum transactionAlbum) {
        this.photo = photo;
        this.transactionAlbum = transactionAlbum;
        this.photoId = photo.getId();
        this.transactionAlbumId = transactionAlbum.getId();
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        this.photoId = photo.getId();
    }

    public TransactionAlbum getTransactionAlbum() {
        return transactionAlbum;
    }

    public void setTransactionAlbum(TransactionAlbum transactionAlbum) {
        this.transactionAlbum = transactionAlbum;
        this.transactionAlbumId = transactionAlbum.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
