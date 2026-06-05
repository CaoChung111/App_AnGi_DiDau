package com.example.angi_didau.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.angi_didau.R;
import com.example.angi_didau.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Registration screen — creates a new Firebase Auth account.
 * <p>
 * Delegates all validation and Firebase calls to {@link AuthViewModel}.
 * On success, navigates directly to {@link HomeActivity} (no need to go back to login).
 */
public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

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
        String email           = etEmail.getText() != null ? etEmail.getText().toString() : "";
        String password        = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString() : "";

        androidx.lifecycle.LiveData<com.google.firebase.auth.FirebaseUser> registerResult =
                authViewModel.register(email, password, confirmPassword);
        if (registerResult == null) return; // Validation failed

        registerResult.observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // Navigate directly to home, clear back stack
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
