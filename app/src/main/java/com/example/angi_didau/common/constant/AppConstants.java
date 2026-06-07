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
    public static final String COLLECTION_FOODS     = "foods";
    public static final String COLLECTION_LOCATIONS = "locations";
    public static final String COLLECTION_REVIEWS   = "Reviews";
    public static final String COLLECTION_USERS     = "Users";
    public static final String COLLECTION_FAVORITES = "Favorites";

    // ──────────────────────────────────────────
    //  Firestore Query Limits
    // ──────────────────────────────────────────
    public static final long TRENDING_FOODS_LIMIT        = 5L;
    public static final long RECOMMENDED_LOCATIONS_LIMIT = 5L;
    public static final long LIST_PAGE_LIMIT             = 20L;

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
    public static final String EXTRA_FOOD_ID       = "extra_food_id";
    public static final String EXTRA_LOCATION_ID   = "extra_location_id";
    public static final String EXTRA_PLAN_ID = "extra_plan_id";
    public static final String EXTRA_ENTITY_TYPE   = "extra_entity_type";
    public static final String EXTRA_ENTITY_NAME   = "extra_entity_name";

    // ──────────────────────────────────────────
    //  Entity Types (for Reviews & Favorites)
    // ──────────────────────────────────────────
    public static final String ENTITY_TYPE_FOOD     = "food";
    public static final String ENTITY_TYPE_LOCATION = "location";

    // ──────────────────────────────────────────
    //  Search
    // ──────────────────────────────────────────
    /** Debounce delay in ms before triggering search query (avoids excessive Firestore reads). */
    public static final long SEARCH_DEBOUNCE_MS = 500L;
}
