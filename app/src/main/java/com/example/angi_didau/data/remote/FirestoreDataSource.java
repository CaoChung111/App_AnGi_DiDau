package com.example.angi_didau.data.remote;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Singleton wrapper for the Firestore database instance.
 * <p>
 * Centralizes Firestore initialization so all Repositories share the same
 * instance instead of each calling {@link FirebaseFirestore#getInstance()} independently.
 */
public class FirestoreDataSource {

    private static FirestoreDataSource instance;
    private final FirebaseFirestore db;

    private FirestoreDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreDataSource getInstance() {
        if (instance == null) {
            instance = new FirestoreDataSource();
        }
        return instance;
    }

    /** Returns the Firestore database reference for use by Repositories. */
    public FirebaseFirestore getDb() {
        return db;
    }
}
