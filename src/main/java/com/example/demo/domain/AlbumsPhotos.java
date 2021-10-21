package com.example.demo.domain;

import com.example.demo.domain.IdClasses.AlbumsPhotosIdClass;
import liquibase.pro.packaged.C;

import javax.persistence.*;

@Entity
@Table(name = "photos_in_albums")
@IdClass(AlbumsPhotosIdClass.class)
public class AlbumsPhotos {
    @Id
    @Column(name = "album_id")
    private int albumId;

    @Id
    @Column(name = "photo_id")
    private int photoId;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "albums", referencedColumnName = "id")
    private Album album;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "photos", referencedColumnName = "id")
    private Photo photo;

    public AlbumsPhotos(Album album, Photo photo) {
        this.album = album;
        this.photo = photo;
        this.albumId = album.getId();
        this.photoId = photo.getId();
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
        this.albumId = album.getId();
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        this.photoId = photo.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
