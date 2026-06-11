package com.example.angi_didau.ui.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.repository.LocationRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for {@link LocationListActivity}.
 */
public class LocationListViewModel extends ViewModel {

    private final LocationRepository locationRepository;

    private final MutableLiveData<List<Location>> locations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private DocumentSnapshot lastVisible = null;
    private boolean isLastPage = false;
    private boolean isFetching = false;
    private static final int PAGE_SIZE = 50;

    public LocationListViewModel() {
        locationRepository = LocationRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public LiveData<List<Location>> getLocations() {
        if (locations.getValue() == null || locations.getValue().isEmpty()) {
            loadNextPage();
        }
        return locations;
    }

    public boolean isFetching() { return isFetching; }
    public boolean isLastPage() { return isLastPage; }

    public void loadNextPage() {
        if (isLastPage || isFetching) return;
        isFetching = true;
        isLoading.setValue(true);

        locationRepository.getLocationsPage(lastVisible, PAGE_SIZE, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Location> newLocations = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Location location = doc.toObject(Location.class);
                    if (location != null) {
                        location.setId(doc.getId());
                        newLocations.add(location);
                    }
                }

                List<Location> current = locations.getValue();
                if (current == null) current = new ArrayList<>();
                current.addAll(newLocations);
                locations.setValue(current);

                if (task.getResult().size() < PAGE_SIZE) {
                    isLastPage = true;
                } else {
                    int lastIndex = task.getResult().size() - 1;
                    lastVisible = task.getResult().getDocuments().get(lastIndex);
                }
            }
            isFetching = false;
            isLoading.setValue(false);
        });
    }

    public void refresh() {
        lastVisible = null;
        isLastPage = false;
        isFetching = false;
        locations.setValue(new ArrayList<>());
        loadNextPage();
    }

    public void onDataLoaded() {
        // No-op
    }
}
