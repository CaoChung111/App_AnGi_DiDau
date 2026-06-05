package com.example.angi_didau.ui.search;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.angi_didau.R;

/**
 * Full-screen search experience.
 * <p>
 * Previously called {@code SearchPreviewActivity} and incorrectly used
 * {@code fragment_search.xml} (a Fragment layout) as the Activity's content view.
 * This class now uses its own proper Activity layout.
 * <p>
 * TODO: Implement real-time search with Firestore queries as user types.
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
