package com.example.angi_didau.ui.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.repository.LocationRepository;
import java.util.List;

/**
 * ViewModel for {@link LocationListActivity}.
 */
public class LocationListViewModel extends ViewModel {

    private final LocationRepository locationRepository;

    private LiveData<List<Location>> locations;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public LocationListViewModel() {
        locationRepository = LocationRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public LiveData<List<Location>> getLocations() {
        if (locations == null) {
            locations = locationRepository.getAllLocations();
        }
        return locations;
    }

    public void refresh() {
        isLoading.setValue(true);
        locations = locationRepository.getAllLocations();
    }

    public void onDataLoaded() {
        isLoading.setValue(false);
    }
}
