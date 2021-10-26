package com.example.demo.domain;

import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;

import javax.persistence.*;

@Entity
@Table(name = "chore_album")
public class ChoreAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "chore_id", referencedColumnName = "id")
    private Chore chore;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public ChoreAlbum() {
    }

    public ChoreAlbum(Chore chore, boolean isDeleted) {
        this.chore = chore;
        this.isDeleted = isDeleted;
    }

    public ChoreAlbum(Chore chore) {
        this.chore = chore;
    }

    public int getId() {
        return id;
    }

    public Chore getChore() {
        return chore;
    }

    public void setChore(Chore chore) {
        this.chore = chore;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
