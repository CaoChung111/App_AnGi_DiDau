package com.example.angi_didau.ui.model;

public class StaggeredItem {
    private String id;
    private String title;
    private String subtitle;
    private float rating;
    private String imageUrl;
    private String priceLabel;

    public StaggeredItem(String id, String title, String subtitle, float rating, String imageUrl) {
        this(id, title, subtitle, rating, imageUrl, null);
    }
    
    public StaggeredItem(String id, String title, String subtitle, float rating, String imageUrl, String priceLabel) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.priceLabel = priceLabel;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public float getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public String getPriceLabel() { return priceLabel; }
}
