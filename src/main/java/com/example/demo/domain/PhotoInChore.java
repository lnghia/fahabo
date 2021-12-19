package com.example.demo.domain;

import com.example.demo.domain.IdClasses.PhotoInChoreIdClass;

import javax.persistence.*;

@Entity
@Table(name = "photos_in_chore_albums")
@IdClass(PhotoInChoreIdClass.class)
public class PhotoInChore {
    @Id
    @Column(name = "photo_id")
    private int photoId;

    @Id
    @Column(name = "chore_album_id")
    private int choreAlbumId;

    @ManyToOne
    @JoinColumn(name = "photos", referencedColumnName = "id")
    private Photo photo;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "chore_albums", referencedColumnName = "id")
    private ChoreAlbum album;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public PhotoInChore() {
    }

    public PhotoInChore(int photoId, int choreAlbumId, Photo photo, ChoreAlbum album) {
        this.photoId = photoId;
        this.choreAlbumId = choreAlbumId;
        this.photo = photo;
        this.album = album;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getChoreAlbumId() {
        return choreAlbumId;
    }

    public void setChoreAlbumId(int choreAlbumId) {
        this.choreAlbumId = choreAlbumId;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        this.photoId = photo.getId();
    }

    public ChoreAlbum getAlbum() {
        return album;
    }

    public void setAlbum(ChoreAlbum album) {
        this.album = album;
        this.choreAlbumId = album.getId();
    }
}
