package com.example.angi_didau.data.model;

public class TimelineItem {
    private String timeCategory;
    private String title;
    private String price;
    private String description;
    private String location;
    // For fake image resource or URL. Let's use int resource for fake data
    private int imageResId;
    private int iconResId;
    private String entityId;
    private String entityType;

    // Empty constructor required for Firestore
    public TimelineItem() {
    }

    public TimelineItem(String timeCategory, String title, String price, String description, String location, int imageResId, int iconResId, String entityId, String entityType) {
        this.timeCategory = timeCategory;
        this.title = title;
        this.price = price;
        this.description = description;
        this.location = location;
        this.imageResId = imageResId;
        this.iconResId = iconResId;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public String getTimeCategory() { return timeCategory; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getImageResId() { return imageResId; }
    public int getIconResId() { return iconResId; }
    public String getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }

    public void setTimeCategory(String timeCategory) { this.timeCategory = timeCategory; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
}
