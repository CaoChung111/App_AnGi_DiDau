package com.example.angi_didau.models;

public class Review {
    private int id;
    private int userId;
    private int entityId;
    private String content;
    private float rating;

    public Review() {}

    public Review(int id, int userId, int entityId, String content, float rating) {
        this.id = id;
        this.userId = userId;
        this.entityId = entityId;
        this.content = content;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}
