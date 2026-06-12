package com.example.angi_didau.ui.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.auth.LoginActivity;

/**
 * Displayed when the device has no active internet connection.
 * Shows a user-friendly error with a retry button.
 */
public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        Button btnRetry = findViewById(R.id.btnRetry);
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                if (isConnected(this)) {
                    // Go back to Login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    android.widget.Toast.makeText(this,
                            "Vẫn chưa có kết nối mạng. Vui lòng kiểm tra lại.",
                            android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Call this from any Activity's onCreate to guard against no-network scenarios.
     * Returns true if there IS connectivity (caller should proceed normally).
     * Returns false if there is NO connectivity (caller should finish() itself).
     */
    public static boolean checkAndRedirect(android.app.Activity activity) {
        if (!isConnected(activity)) {
            Intent intent = new Intent(activity, NoInternetActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
            return false;
        }
        return true;
    }
}
