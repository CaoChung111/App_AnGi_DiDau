package com.example.angi_didau.data.model;

/**
 * Represents a physical dining/eating location from Firestore collection "locations".
 * <p>
 * NOTE: Avoid naming this class "Location" to prevent shadowing
 * {@link android.location.Location} from the Android SDK.
 * Consider renaming to "Place" or "Restaurant" in a future Kotlin migration.
 */
public class Location {
    private String id;
    private String name;
    private String address;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private float averageRating;

    /** Required no-arg constructor for Firestore deserialization. */
    public Location() {}

    public Location(String id, String name, String address, String imageUrl,
                    double latitude, double longitude, float averageRating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageRating = averageRating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public float getAverageRating() { return averageRating; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }
}
