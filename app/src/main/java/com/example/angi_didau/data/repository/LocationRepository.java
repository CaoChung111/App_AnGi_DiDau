package com.example.angi_didau.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.remote.FirestoreDataSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for {@link Location} data.
 * <p>
 * Single source of truth for location/restaurant data. ViewModels observe LiveData
 * from this class to remain decoupled from Firestore implementation details.
 */
public class LocationRepository {

    private static final String TAG = "LocationRepository";

    private static LocationRepository instance;
    private final FirebaseFirestore db;

    private LocationRepository() {
        db = FirestoreDataSource.getInstance().getDb();
    }

    public static synchronized LocationRepository getInstance() {
        if (instance == null) {
            instance = new LocationRepository();
        }
        return instance;
    }

    /**
     * Fetches recommended locations sorted by rating descending.
     *
     * @return LiveData emitting a list of recommended locations. Emits empty list on error.
     */
    public LiveData<List<Location>> getRecommendedLocations() {
        MutableLiveData<List<Location>> liveData = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_LOCATIONS)
                .orderBy("averageRating", Query.Direction.DESCENDING)
                .limit(AppConstants.RECOMMENDED_LOCATIONS_LIMIT)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Location> locations = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Location location = doc.toObject(Location.class);
                        if (location != null) {
                            location.setId(doc.getId()); // Firestore ID must be set manually
                            locations.add(location);
                        }
                    }
                    liveData.setValue(locations);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch recommended locations", e);
                    liveData.setValue(new ArrayList<>());
                });

        return liveData;
    }

    /**
     * Fetches all locations from Firestore (up to {@link AppConstants#LIST_PAGE_LIMIT}).
     *
     * @return LiveData emitting a list of all locations.
     */
    public LiveData<List<Location>> getAllLocations() {
        MutableLiveData<List<Location>> liveData = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_LOCATIONS)
                .limit(AppConstants.LIST_PAGE_LIMIT)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Location> locations = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Location location = doc.toObject(Location.class);
                        if (location != null) {
                            location.setId(doc.getId());
                            locations.add(location);
                        }
                    }
                    liveData.setValue(locations);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch all locations", e);
                    liveData.setValue(new ArrayList<>());
                });

        return liveData;
    }

    /**
     * Fetches a single location document by its Firestore ID.
     *
     * @param locationId Firestore document ID
     * @return LiveData emitting the Location object, or null if not found.
     */
    public LiveData<Location> getLocationById(String locationId) {
        MutableLiveData<Location> liveData = new MutableLiveData<>();

        if (locationId == null || locationId.isEmpty()) {
            liveData.setValue(null);
            return liveData;
        }

        db.collection(AppConstants.COLLECTION_LOCATIONS)
                .document(locationId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Location location = doc.toObject(Location.class);
                        if (location != null) location.setId(doc.getId());
                        liveData.setValue(location);
                    } else {
                        Log.w(TAG, "Location document not found: " + locationId);
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch location: " + locationId, e);
                    liveData.setValue(null);
                });

        return liveData;
    }

    /**
     * Searches locations by name using Firestore range query (prefix match).
     *
     * @param query Search term (prefix match on "name" field)
     * @return LiveData emitting matching location items.
     */
    public LiveData<List<Location>> searchLocations(String query) {
        MutableLiveData<List<Location>> liveData = new MutableLiveData<>();

        if (query == null || query.trim().isEmpty()) {
            liveData.setValue(new ArrayList<>());
            return liveData;
        }

        String trimmedQuery = query.trim();
        String endQuery = trimmedQuery + "\uF8FF";

        db.collection(AppConstants.COLLECTION_LOCATIONS)
                .orderBy("name")
                .startAt(trimmedQuery)
                .endAt(endQuery)
                .limit(AppConstants.LIST_PAGE_LIMIT)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Location> locations = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Location location = doc.toObject(Location.class);
                        if (location != null) {
                            location.setId(doc.getId());
                            locations.add(location);
                        }
                    }
                    liveData.setValue(locations);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Location search failed for query: " + query, e);
                    liveData.setValue(new ArrayList<>());
                });

        return liveData;
    }
}
