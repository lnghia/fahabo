package com.example.demo.domain;

import com.example.demo.domain.Family.Family;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "familyId", referencedColumnName = "id")
    private Family family;

    @OneToMany(mappedBy = "album")
    private Set<AlbumsPhotos> photosInAlbum = new HashSet<>();

    @Column(name = "description")
    private String description;

    public Album() {
    }

    public Album(String title) {
        this.title = title;
    }

    public Album(String title, Date createdAt, Date updatedAt) {
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAtAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return formatter.format(createdAt);
    }

    public String getUpdatedAtAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return formatter.format(updatedAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
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

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Set<AlbumsPhotos> getPhotosInAlbum() {
        return photosInAlbum;
    }

    public void setPhotosInAlbum(Set<AlbumsPhotos> photosInAlbum) {
        this.photosInAlbum = photosInAlbum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addPhoto(AlbumsPhotos albumsPhotos) {
        photosInAlbum.add(albumsPhotos);
    }

    public HashMap<String, Object> getJson() {
        return new HashMap<>() {{
            put("id", id);
            put("title", title);
            put("description", description);
            put("totalPhotos", photosInAlbum.size());
        }};
    }
}
