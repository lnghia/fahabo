package com.example.demo.HomeCook.Entity;

import com.example.demo.Helpers.Helper;
import com.example.demo.domain.User;
import com.google.rpc.Help;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

@Entity
@Table(name = "cuisine_posts")
public class CookPost {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    private User author;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "angry_vote")
    private int angry_vote = 0;

    @Column(name = "like_vote")
    private int like_vote = 0;

    @Column(name = "yummy_vote")
    private int yummy_vote = 0;

    @Column(name = "content")
    private String content;

    @Column(name = "ratings")
    private double ratings = 0.0f;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public CookPost() {
    }

    public CookPost(String title, String thumbnail, int angry_vote, int like_vote, int yummy_vote) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.angry_vote = angry_vote;
        this.like_vote = like_vote;
        this.yummy_vote = yummy_vote;
        this.createdAt = new Date();
        this.updatedAt = createdAt;
    }

    public CookPost(String title, User author, Date createdAt, Date updatedAt) {
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getAngry_vote() {
        return angry_vote;
    }

    public void setAngry_vote(int angry_vote) {
        this.angry_vote = angry_vote;
    }

    public int getLike_vote() {
        return like_vote;
    }

    public void setLike_vote(int like_vote) {
        this.like_vote = like_vote;
    }

    public int getYummy_vote() {
        return yummy_vote;
    }

    public void setYummy_vote(int yummy_vote) {
        this.yummy_vote = yummy_vote;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) throws IOException {
        Helper helper = Helper.getInstance();
        String htmlDirAbsPath = helper.COOK_POST_CONTENT_DIR_ABSOLUTE_PATH;
        helper.writeFile(htmlDirAbsPath + id, ".html", content);

        this.content = htmlDirAbsPath + id + ".html";
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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

    public HashMap<String, Object> getShortJson(String readyToViewThumbnail) {
        HashMap<String, Object> rs = new HashMap<>();

        rs.put("thumbnail", (readyToViewThumbnail != null) ? readyToViewThumbnail : thumbnail);
        rs.put("angry", angry_vote);
        rs.put("like", like_vote);
        rs.put("yummy", yummy_vote);
        rs.put("title", title);

        return rs;
    }

    public HashMap<String, Object> getJson() {
        HashMap<String, Object> rs = new HashMap<>();
        String postContent = null;

        try {
            postContent = Helper.getInstance().readFileAsStr(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        rs.put("angry", angry_vote);
        rs.put("like", like_vote);
        rs.put("yummy", yummy_vote);
        rs.put("title", title);
        rs.put("content", postContent);

        return rs;
    }

    public HashMap<String, Object> getJson(String thumbnailUri, String avatarUri, int userReactedType, String timezone, boolean isBookmarked) {
        HashMap<String, Object> rs = new HashMap<>();
        String postContent = null;

        try {
            postContent = Helper.getInstance().readFileAsStr(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        rs.put("title", title);
        rs.put("content", postContent);
        rs.put("cuisinePostId", id);
        rs.put("thumbnail", (thumbnailUri != null) ? thumbnailUri : thumbnail);
        rs.put("updatedAt", Helper.getInstance().formatDateWithTimeAsTimezone(updatedAt, timezone));
        rs.put("angryRatings", angry_vote);
        rs.put("likeRatings", like_vote);
        rs.put("yummyRatings", yummy_vote);
        rs.put("author", author.getShortJson(avatarUri));
        rs.put("userReactedType", userReactedType);
        rs.put("isBookmarked", isBookmarked);

        return rs;
    }

    public void calculateRank() {
        double score = (like_vote + 1.5f * yummy_vote) - angry_vote;
        double order = Math.log10(Math.max(Math.abs(score), 1));
        int sign = (score > 0) ? 1 : (score == 0) ? 0 : -1;
        long seconds = createdAt.getTime() - 1134028003l;

        ratings = Math.round(sign * order + seconds / 45000);
    }
}
