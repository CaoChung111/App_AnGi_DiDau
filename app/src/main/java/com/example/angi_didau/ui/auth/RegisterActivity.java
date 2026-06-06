package com.example.angi_didau.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.angi_didau.R;
import com.example.angi_didau.common.util.SessionManager;
import com.example.angi_didau.data.model.User;
import com.example.angi_didau.data.repository.UserRepository;
import com.example.angi_didau.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Registration screen — creates a new Firebase Auth account.
 * <p>
 * Delegates all validation and Firebase calls to {@link AuthViewModel}.
 * On success:
 * 1. Creates a Firestore user document via {@link UserRepository}
 * 2. Saves login state to SessionManager
 * 3. Navigates directly to {@link HomeActivity} (no need to go back to login)
 */
public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel  authViewModel;
    private SessionManager sessionManager;

    private TextInputLayout      tilEmail;
    private TextInputLayout      tilPassword;
    private TextInputEditText    etEmail;
    private TextInputEditText    etPassword;
    private TextInputEditText    etConfirmPassword;
    private MaterialButton       btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel  = new ViewModelProvider(this).get(AuthViewModel.class);
        sessionManager = new SessionManager(this);

        bindViews();
        setupListeners();
        observeViewModel();
    }

    private void bindViews() {
        tilEmail          = findViewById(R.id.tilEmail);
        tilPassword       = findViewById(R.id.tilPassword);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);

        // Navigate back to login when "Already have account?" is tapped
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void observeViewModel() {
        authViewModel.getEmailError().observe(this, error ->
                tilEmail.setError(error));

        authViewModel.getPasswordError().observe(this, error ->
                tilPassword.setError(error));
    }

    private void attemptRegister() {
        String email           = etEmail.getText()           != null ? etEmail.getText().toString()           : "";
        String password        = etPassword.getText()        != null ? etPassword.getText().toString()        : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        androidx.lifecycle.LiveData<com.google.firebase.auth.FirebaseUser> registerResult =
                authViewModel.register(email, password, confirmPassword);
        if (registerResult == null) return; // Validation failed

        registerResult.observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Use Full Name if provided, else fallback to email prefix
                TextInputEditText etFullName = findViewById(R.id.etFullName);
                String fullName = etFullName != null && etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
                String displayName = fullName.isEmpty() ? email.split("@")[0] : fullName;

                User newUser = new User(
                        firebaseUser.getUid(),
                        displayName,
                        email.trim(),
                        "",
                        System.currentTimeMillis()
                );
                UserRepository.getInstance().saveUserToFirestore(newUser);

                // Save session locally
                sessionManager.saveLoginState(true, firebaseUser.getUid(), displayName);

                Toast.makeText(this, "Đăng ký thành công! Chào mừng bạn 🎉", Toast.LENGTH_SHORT).show();

                // Navigate to Home, clear back stack
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Đăng ký thất bại. Email này có thể đã được đăng ký, vui lòng thử đăng nhập!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
