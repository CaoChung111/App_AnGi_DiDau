package com.example.angi_didau.common.constant;

/**
 * Application-wide constants.
 * <p>
 * Centralizes all magic strings and configuration values to prevent typos
 * and make future changes easy (single point of truth).
 */
public final class AppConstants {

    // Private constructor prevents instantiation
    private AppConstants() {}

    // ──────────────────────────────────────────
    //  API / Backend
    // ──────────────────────────────────────────
    /** Base URL for future REST API integration. Must use HTTPS in production. */
    public static final String BASE_API_URL = "https://api.example.com/v1/";

    // ──────────────────────────────────────────
    //  Firestore Collection Names
    // ──────────────────────────────────────────
    public static final String COLLECTION_FOODS     = "Foods";
    public static final String COLLECTION_LOCATIONS = "Locations";
    public static final String COLLECTION_REVIEWS   = "Reviews";
    public static final String COLLECTION_USERS     = "Users";

    // ──────────────────────────────────────────
    //  Firestore Query Limits
    // ──────────────────────────────────────────
    public static final long TRENDING_FOODS_LIMIT        = 5L;
    public static final long RECOMMENDED_LOCATIONS_LIMIT = 5L;

    // ──────────────────────────────────────────
    //  SharedPreferences Keys
    // ──────────────────────────────────────────
    public static final String KEY_PREF_NAME      = "AnGiDiDauSession";
    public static final String KEY_IS_LOGGED_IN   = "isLoggedIn";
    public static final String KEY_USER_ID        = "userId";
    public static final String KEY_USER_NAME      = "userName";

    // ──────────────────────────────────────────
    //  Intent Extra Keys
    // ──────────────────────────────────────────
    public static final String EXTRA_FOOD_ID     = "extra_food_id";
    public static final String EXTRA_LOCATION_ID = "extra_location_id";
}
