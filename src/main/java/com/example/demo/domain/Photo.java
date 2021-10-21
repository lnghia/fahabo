package com.example.demo.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String uri;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    private boolean isDeleted = false;

//    @ManyToOne
//    @JoinColumn(name = "created_by", referencedColumnName = "id")
////    @CreatedBy
//    private User createdBy;

    @OneToMany(mappedBy = "photo")
    private Set<AlbumsPhotos> photoInAlbums = new HashSet<>();

    @Column(name = "description")
    private String description;

    public Photo(){}

    public Photo(Date createdAt, Date updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Photo(String uri, Date createdAt, Date updatedAt) {
        this.uri = uri;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

//    public Photo(String uri, User createdBy) {
//        this.uri = uri;
//        this.createdBy = createdBy;
//    }
//
    public Photo(String uri, Set<AlbumsPhotos> photoInAlbums) {
        this.uri = uri;
//        this.createdBy = createdBy;
        this.photoInAlbums = photoInAlbums;
    }

    public int getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Set<AlbumsPhotos> getPhotoInAlbums() {
        return photoInAlbums;
    }

    public void setPhotoInAlbums(Set<AlbumsPhotos> photoInAlbums) {
        this.photoInAlbums = photoInAlbums;
    }

    public void addAlbum(AlbumsPhotos albumsPhotos){
        photoInAlbums.add(albumsPhotos);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //    public User getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(User createdBy) {
//        this.createdBy = createdBy;
//    }
}
