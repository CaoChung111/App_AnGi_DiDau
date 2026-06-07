package com.example.angi_didau.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.angi_didau.data.model.Review;
import com.example.angi_didau.data.repository.ReviewRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserReviewsViewModel extends ViewModel {

    private final ReviewRepository reviewRepository;
    private LiveData<List<Review>> userReviews;

    public UserReviewsViewModel() {
        reviewRepository = ReviewRepository.getInstance();
    }

    public LiveData<List<Review>> getUserReviews() {
        if (userReviews == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userReviews = reviewRepository.getReviewsByUserId(user.getUid());
            } else {
                MutableLiveData<List<Review>> empty = new MutableLiveData<>();
                userReviews = empty;
            }
        }
        return userReviews;
    }
}
