package com.example.angi_didau.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.adapter.SearchResultAdapter;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.ui.food.FoodDetailActivity;
import com.example.angi_didau.ui.location.LocationDetailActivity;

/**
 * Full-screen search experience.
 * <p>
 * Implements real-time search with 500ms debouncing via {@link SearchViewModel}.
 * Results are displayed in a combined RecyclerView showing foods and locations.
 * Navigates to the appropriate detail screen on item click.
 */
public class SearchActivity extends AppCompatActivity {

    private SearchViewModel      viewModel;
    private SearchResultAdapter  adapter;

    // Views
    private EditText    etSearch;
    private RecyclerView rvResults;
    private View         progressBar;
    private TextView     tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        bindViews();
        setupSearchBar();
        setupResultList();
        observeViewModel();
    }

    private void bindViews() {
        etSearch    = findViewById(R.id.etSearchQuery);
        rvResults   = findViewById(R.id.rvSearchResults);

        // Back button
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Auto-focus search input
        if (etSearch != null) etSearch.requestFocus();
    }

    private void setupSearchBar() {
        if (etSearch == null) return;

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupResultList() {
        if (rvResults == null) return;

        adapter = new SearchResultAdapter();
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        // Food click → FoodDetailActivity
        adapter.setFoodClickListener(food -> {
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_FOOD_ID, food.getId());
            startActivity(intent);
        });

        // Location click → LocationDetailActivity
        adapter.setLocationClickListener(location -> {
            Intent intent = new Intent(this, LocationDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_LOCATION_ID, location.getId());
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        // Combine both result sets whenever either changes
        viewModel.getFoodResults().observe(this, foods -> {
            if (adapter != null) {
                adapter.submitResults(foods, viewModel.getLocationResults().getValue());
            }
        });

        viewModel.getLocationResults().observe(this, locations -> {
            if (adapter != null) {
                adapter.submitResults(viewModel.getFoodResults().getValue(), locations);
            }
        });
    }
}
