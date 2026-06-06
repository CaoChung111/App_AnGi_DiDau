package com.example.angi_didau.data.model;

import java.util.List;

/**
 * Represents a saved plan/itinerary from the user.
 */
public class SavedPlan {
    private String id;
    private String userId;
    private String title;
    private String totalCost;
    private List<TimelineItem> items;
    private long timestamp;

    public SavedPlan() {}

    public SavedPlan(String id, String userId, String title, String totalCost, List<TimelineItem> items, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.totalCost = totalCost;
        this.items = items;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTotalCost() { return totalCost; }
    public void setTotalCost(String totalCost) { this.totalCost = totalCost; }

    public List<TimelineItem> getItems() { return items; }
    public void setItems(List<TimelineItem> items) { this.items = items; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
