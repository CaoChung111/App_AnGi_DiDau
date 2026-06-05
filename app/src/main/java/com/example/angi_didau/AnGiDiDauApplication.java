package com.example.angi_didau;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;

/**
 * Application class for AnGi Di Dau.
 * <p>
 * Registered in {@code AndroidManifest.xml} via {@code android:name=".AnGiDiDauApplication"}.
 * Use this class for:
 * <ul>
 *   <li>App-level initialization (Firebase, logging libraries, etc.)</li>
 *   <li>Providing application-scoped singletons (if not using Hilt)</li>
 * </ul>
 */
public class AnGiDiDauApplication extends Application {

    private static final String TAG = "AnGiDiDauApp";

    @Override
    public void onCreate() {
        super.onCreate();
        initFirebase();
        Log.d(TAG, "Application initialized");
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        Log.d(TAG, "Firebase initialized");
    }
}
