package com.example.angi_didau.data.model;

/**
 * Represents a user review for a food item or location.
 * <p>
 * The {@code entityId} field stores the ID of the reviewed entity
 * (either a Food ID or a Location ID). Consider splitting into
 * {@code FoodReview} and {@code LocationReview} if review structures diverge.
 */
public class Review {
    private String id;
    private String userId;
    private String entityId;
    private String content;
    private float rating;
    private long timestamp;

    /** Required no-arg constructor for Firestore deserialization. */
    public Review() {}

    public Review(String id, String userId, String entityId,
                  String content, float rating, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.entityId = entityId;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
