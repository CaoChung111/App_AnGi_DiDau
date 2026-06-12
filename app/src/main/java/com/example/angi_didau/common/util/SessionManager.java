package com.example.angi_didau.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.angi_didau.common.constant.AppConstants;

/**
 * Manages user session state using SharedPreferences.
 * <p>
 * Stores only non-sensitive session metadata (login state, userId, displayName).
 * Passwords are NEVER stored here — handled exclusively by Firebase Authentication.
 */
public class SessionManager {

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        // Use application context to avoid Activity memory leaks
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(AppConstants.KEY_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Persists the user's login state and basic profile info.
     *
     * @param isLoggedIn true if the user just logged in successfully
     * @param userId     Firebase Auth UID
     * @param userName   display name to show in the UI
     */
    public void saveLoginState(boolean isLoggedIn, String userId, String userName) {
        sharedPreferences.edit()
                .putBoolean(AppConstants.KEY_IS_LOGGED_IN, isLoggedIn)
                .putString(AppConstants.KEY_USER_ID, userId)
                .putString(AppConstants.KEY_USER_NAME, userName)
                .putBoolean("is_guest", false)
                .apply();
    }

    /** Clears all session data on logout. */
    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

    public void setGuestMode(boolean isGuest) {
        sharedPreferences.edit()
                .putBoolean("is_guest", isGuest)
                .apply();
    }

    public boolean isGuestMode() {
        return sharedPreferences.getBoolean("is_guest", false);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(AppConstants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return sharedPreferences.getString(AppConstants.KEY_USER_ID, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(AppConstants.KEY_USER_NAME, null);
    }
}
