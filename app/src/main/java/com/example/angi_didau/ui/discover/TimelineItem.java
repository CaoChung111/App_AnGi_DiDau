package com.example.angi_didau.ui.discover;

public class TimelineItem {
    private String timeCategory;
    private String title;
    private String price;
    private String description;
    private String location;
    // For fake image resource or URL. Let's use int resource for fake data
    private int imageResId;
    private int iconResId;

    public TimelineItem(String timeCategory, String title, String price, String description, String location, int imageResId, int iconResId) {
        this.timeCategory = timeCategory;
        this.title = title;
        this.price = price;
        this.description = description;
        this.location = location;
        this.imageResId = imageResId;
        this.iconResId = iconResId;
    }

    public String getTimeCategory() { return timeCategory; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getImageResId() { return imageResId; }
    public int getIconResId() { return iconResId; }
}
