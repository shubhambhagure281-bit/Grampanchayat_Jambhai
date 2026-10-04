package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityAdminLoginBinding;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private ActivityAdminLoginBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        binding.btnAdminLogin.setOnClickListener(v -> performAdminLogin());

        binding.tvCitizenLoginLink.setOnClickListener(v -> finish());
    }

    private void performAdminLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.err_email_invalid));
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.err_password_short));
            return;
        }

        setLoadingState(true);

        authRepository.loginWithEmail(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        verifyAdminRoleAndNavigate(user.getUid());
                    } else {
                        setLoadingState(false);
                        Toast.makeText(AdminLoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(AdminLoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void verifyAdminRoleAndNavigate(String uid) {
        firestoreRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    setLoadingState(false);
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                            navigateToAdminDashboard();
                        } else {
                            authRepository.logout();
                            Toast.makeText(AdminLoginActivity.this, "तुम्हाला प्रशासकीय लॉगिनची परवानगी नाही.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        authRepository.logout();
                        Toast.makeText(AdminLoginActivity.this, "तुम्हाला प्रशासकीय लॉगिनची परवानगी नाही.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    authRepository.logout();
                    Toast.makeText(AdminLoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnAdminLogin.setEnabled(!isLoading);
        binding.tvCitizenLoginLink.setEnabled(!isLoading);
    }

    private void navigateToAdminDashboard() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}