package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityRegisterBinding;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        binding.btnRegister.setOnClickListener(v -> performRegistration());

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String mobile = binding.etMobile.getText() != null ? binding.etMobile.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString().trim() : "";
        String address = binding.etAddress.getText() != null ? binding.etAddress.getText().toString().trim() : "";

        // Reset errors
        binding.tilFullName.setError(null);
        binding.tilMobile.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
        binding.tilAddress.setError(null);

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError(getString(R.string.err_name_required));
            return;
        }

        if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
            binding.tilMobile.setError(getString(R.string.err_mobile_invalid));
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.err_email_invalid));
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.err_password_short));
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.err_password_mismatch));
            return;
        }

        if (TextUtils.isEmpty(address)) {
            binding.tilAddress.setError(getString(R.string.err_address_required));
            return;
        }

        setLoadingState(true);

        authRepository.registerWithEmail(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        User newUser = new User(
                                firebaseUser.getUid(),
                                fullName,
                                mobile,
                                email,
                                address,
                                "",
                                "user", // Default role
                                System.currentTimeMillis()
                        );
                        saveUserProfile(newUser);
                    } else {
                        setLoadingState(false);
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserProfile(User user) {
        firestoreRepository.saveUserProfile(user)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    navigateToHome();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!isLoading);
        binding.tvLoginLink.setEnabled(!isLoading);
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}