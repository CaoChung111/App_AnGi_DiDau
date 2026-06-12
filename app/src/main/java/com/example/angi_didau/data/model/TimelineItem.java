package com.example.angi_didau.data.model;

public class TimelineItem {
    private String timeCategory;
    private String title;
    private String price;
    private String description;
    private String location;
    // imageUrl can be a URL string or empty (use default icon then)
    private String imageUrl;
    private Long imageResId; // For backward compatibility with old Firebase data
    private int iconResId;
    private String entityId;
    private String entityType;

    // Empty constructor required for Firestore
    public TimelineItem() {
    }

    // New constructor using imageUrl (String) instead of imageResId (int)
    public TimelineItem(String timeCategory, String title, String price, String description, String location, String imageUrl, int iconResId, String entityId, String entityType) {
        this.timeCategory = timeCategory;
        this.title = title;
        this.price = price;
        this.description = description;
        this.location = location;
        this.imageUrl = imageUrl;
        this.iconResId = iconResId;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public String getTimeCategory() { return timeCategory; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
    public Long getImageResId() { return imageResId; }
    public int getIconResId() { return iconResId; }
    public String getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }

    public void setTimeCategory(String timeCategory) { this.timeCategory = timeCategory; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setImageResId(Long imageResId) { this.imageResId = imageResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
}
