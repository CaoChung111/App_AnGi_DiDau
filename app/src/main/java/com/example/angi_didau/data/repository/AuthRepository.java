package com.example.angi_didau.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Repository for Firebase Authentication operations.
 * <p>
 * Centralizes all auth logic (login, register, logout) in one place.
 * Activities and ViewModels never call FirebaseAuth directly.
 */
public class AuthRepository {

    private static final String TAG = "AuthRepository";

    private static AuthRepository instance;
    private final FirebaseAuth firebaseAuth;

    private AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    /**
     * Returns the currently signed-in Firebase user, or null if not authenticated.
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Returns true if a user is currently signed in.
     */
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Attempts to sign in with email and password.
     *
     * @param email    User's email address
     * @param password User's password (handled securely by Firebase, never stored locally)
     * @return LiveData emitting true on success, false on failure
     */
    public LiveData<Boolean> login(String email, String password) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Login successful for: " + email);
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login failed for: " + email, e);
                    result.setValue(false);
                });

        return result;
    }

    /**
     * Registers a new user with email and password via Firebase Authentication.
     *
     * @param email    New user's email address
     * @param password New user's password
     * @return LiveData emitting the new FirebaseUser on success, or null on failure
     */
    public LiveData<FirebaseUser> register(String email, String password) {
        MutableLiveData<FirebaseUser> result = new MutableLiveData<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Registration successful for: " + email);
                    result.setValue(authResult.getUser());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Registration failed for: " + email, e);
                    result.setValue(null);
                });

        return result;
    }

    /**
     * Signs out the current user from Firebase Authentication.
     */
    public void logout() {
        firebaseAuth.signOut();
    }
}
