package com.example.demo.Event.Entity;

import com.example.demo.domain.IdClasses.PhotoInEventId;
import com.example.demo.domain.Photo;

import javax.persistence.*;

@Entity
@Table(name = "photos_in_events")
@IdClass(PhotoInEventId.class)
public class PhotoInEvent {
    @Id
    @Column(name = "photo_id")
    private int photoId;

    @Id
    @Column(name = "event_album_id")
    private int eventAlbumId;

    @ManyToOne
    @JoinColumn(name = "photos", referencedColumnName = "id")
    private Photo photo;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "event_albums", referencedColumnName = "id")
    private EventAlbum album;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getEventAlbumId() {
        return eventAlbumId;
    }

    public void setEventAlbumId(int eventAlbumId) {
        this.eventAlbumId = eventAlbumId;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        photoId = photo.getId();
    }

    public EventAlbum getAlbum() {
        return album;
    }

    public void setAlbum(EventAlbum album) {
        this.album = album;
        eventAlbumId = album.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
