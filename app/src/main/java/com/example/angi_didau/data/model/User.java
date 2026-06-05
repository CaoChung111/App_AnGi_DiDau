package com.example.angi_didau.data.model;

/**
 * Represents a food/dish item from Firestore collection "Foods".
 * <p>
 * SECURITY NOTE: Passwords must NEVER be stored in this model or Firestore.
 * Firebase Authentication handles credentials separately.
 * <p>
 * Naming follows JavaBean convention for Firestore deserialization compatibility.
 */
public class User {
    private String id;
    private String username;
    private String email;
    private String avatarUrl;
    private long createdAt;

    /** Required no-arg constructor for Firestore deserialization. */
    public User() {}

    public User(String id, String username, String email, String avatarUrl, long createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
