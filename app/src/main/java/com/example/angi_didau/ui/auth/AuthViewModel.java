package com.example.angi_didau.ui.auth;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel for {@link LoginActivity} and {@link RegisterActivity}.
 * <p>
 * Handles input validation and delegates auth operations to {@link AuthRepository}.
 * The Activity is only responsible for showing error messages and navigating on success.
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    /** Emits error message string, or null if no error. */
    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
    }

    // ──────────────────────────────────────────
    //  Exposed LiveData
    // ──────────────────────────────────────────

    public LiveData<String> getEmailError() { return emailError; }
    public LiveData<String> getPasswordError() { return passwordError; }

    // ──────────────────────────────────────────
    //  Auth Actions
    // ──────────────────────────────────────────

    /**
     * Validates input then attempts Firebase login.
     *
     * @return LiveData emitting true on success, false on failure. Returns null if validation fails.
     */
    public LiveData<Boolean> login(String email, String password) {
        if (!validateLoginInput(email, password)) {
            return null;
        }
        return authRepository.login(email.trim(), password);
    }

    /**
     * Validates input then attempts Firebase user registration.
     *
     * @return LiveData emitting the new FirebaseUser on success, null on failure.
     */
    public LiveData<FirebaseUser> register(String email, String password, String confirmPassword) {
        if (!validateRegisterInput(email, password, confirmPassword)) {
            return null;
        }
        return authRepository.register(email.trim(), password);
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    // ──────────────────────────────────────────
    //  Validation (Business Logic — stays in ViewModel)
    // ──────────────────────────────────────────

    private boolean validateLoginInput(String email, String password) {
        boolean isValid = true;

        if (email == null || email.trim().isEmpty()) {
            emailError.setValue("Email không được để trống");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError.setValue("Email không hợp lệ");
            isValid = false;
        } else {
            emailError.setValue(null);
        }

        if (password == null || password.isEmpty()) {
            passwordError.setValue("Mật khẩu không được để trống");
            isValid = false;
        } else if (password.length() < 6) {
            passwordError.setValue("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            passwordError.setValue(null);
        }

        return isValid;
    }

    private boolean validateRegisterInput(String email, String password, String confirmPassword) {
        boolean isValid = validateLoginInput(email, password);

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            passwordError.setValue("Mật khẩu xác nhận không khớp");
            isValid = false;
        }

        return isValid;
    }
}
