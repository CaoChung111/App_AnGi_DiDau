package com.example.angi_didau.data.model;

/**
 * Represents a food/dish item from Firestore collection "foods".
 * <p>
 * Field names must match Firestore document field names exactly for auto-deserialization.
 */
public class Food {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private float averageRating;

    /** Required no-arg constructor for Firestore deserialization. */
    public Food() {}

    public Food(String id, String name, String description, double price,
                String imageUrl, float averageRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public float getAverageRating() { return averageRating; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }
}
