package com.example.angi_didau.data.model;

public class Favorite {
    private String entityId;
    private String type;
    private String name;
    private String imageUrl;
    private String note;
    private long savedAt;
    private boolean isCustom;

    // Required empty constructor for Firestore
    public Favorite() {
    }

    public Favorite(String entityId, String type, String name, String imageUrl, String note, long savedAt, boolean isCustom) {
        this.entityId = entityId;
        this.type = type;
        this.name = name;
        this.imageUrl = imageUrl;
        this.note = note;
        this.savedAt = savedAt;
        this.isCustom = isCustom;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
