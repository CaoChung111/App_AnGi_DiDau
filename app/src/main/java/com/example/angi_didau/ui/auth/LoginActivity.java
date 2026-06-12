package com.example.angi_didau.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.angi_didau.R;
import com.example.angi_didau.common.util.SessionManager;
import com.example.angi_didau.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Login screen — first screen the user sees.
 * <p>
 * Delegates validation to {@link AuthViewModel} and Firebase Auth to
 * {@link com.example.angi_didau.data.repository.AuthRepository}.
 * Navigates to {@link HomeActivity} on successful login.
 */
public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check internet connectivity before doing anything
        if (!com.example.angi_didau.ui.common.NoInternetActivity.checkAndRedirect(this)) return;

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sessionManager = new SessionManager(this);

        // Skip login if already authenticated
        if (authViewModel.isUserLoggedIn()) {
            navigateToHome();
            return;
        }

        bindViews();
        setupListeners();
        observeViewModel();
    }

    private void bindViews() {
        tilEmail    = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);

        findViewById(R.id.tvGoToRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void observeViewModel() {
        // Show field-level validation errors
        authViewModel.getEmailError().observe(this, error ->
                tilEmail.setError(error));

        authViewModel.getPasswordError().observe(this, error ->
                tilPassword.setError(error));
    }

    private void attemptLogin() {
        String email    = etEmail.getText() != null ? etEmail.getText().toString() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        androidx.lifecycle.LiveData<Boolean> loginResult = authViewModel.login(email, password);
        if (loginResult == null) return; // Validation failed — errors shown by observer

        loginResult.observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                com.google.firebase.auth.FirebaseUser user = authViewModel.getCurrentUser();
                if (user != null) {
                    sessionManager.saveLoginState(true, user.getUid(),
                            user.getDisplayName() != null ? user.getDisplayName() : email);
                }
                navigateToHome();
            } else {
                Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish(); // Prevent back navigation to login screen
    }
}
