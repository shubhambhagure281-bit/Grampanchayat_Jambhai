package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityProfileBinding;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    private User currentUserModel = null;
    private ActivityResultLauncher<String> photoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupPhotoPicker();
        loadUserProfile();
        setupButtonListeners();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupPhotoPicker() {
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadProfileImage(uri);
                    }
                }
        );
    }

    private void loadUserProfile() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) {
            performLogout();
            return;
        }

        setLoadingState(true);

        firestoreRepository.getUserProfile(firebaseUser.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    setLoadingState(false);
                    if (documentSnapshot.exists()) {
                        currentUserModel = documentSnapshot.toObject(User.class);
                        if (currentUserModel != null) {
                            bindUserData(currentUserModel);
                        }
                    } else {
                        // Document doesn't exist yet, bind fallback from Auth
                        binding.etEmail.setText(firebaseUser.getEmail());
                        binding.tvProfileEmail.setText(firebaseUser.getEmail());
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(ProfileActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                });
    }

    private void bindUserData(User user) {
        binding.tvProfileName.setText(TextUtils.isEmpty(user.getName()) ? getString(R.string.citizen) : user.getName());
        binding.tvProfileEmail.setText(TextUtils.isEmpty(user.getEmail()) ? "" : user.getEmail());

        binding.etName.setText(user.getName());
        binding.etMobile.setText(user.getMobile());
        binding.etEmail.setText(user.getEmail());
        binding.etAddress.setText(user.getAddress());

        if (!TextUtils.isEmpty(user.getProfileImage())) {
            Glide.with(this)
                    .load(user.getProfileImage())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivProfilePhoto);
        }
    }

    private void setupButtonListeners() {
        binding.btnChangePhoto.setOnClickListener(v -> photoPickerLauncher.launch("image/*"));

        binding.btnSaveProfile.setOnClickListener(v -> saveProfileChanges());

        binding.btnMyComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MyComplaintsActivity.class);
            startActivity(intent);
        });

        binding.btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ThemeSelectionActivity.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void uploadProfileImage(Uri imageUri) {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) return;

        setLoadingState(true);

        firestoreRepository.uploadProfilePhoto(firebaseUser.getUid(), imageUri)
                .addOnSuccessListener(downloadUri -> {
                    String photoUrl = downloadUri.toString();
                    binding.ivProfilePhoto.setImageURI(imageUri);

                    // Update profileImage in Firestore
                    if (currentUserModel == null) {
                        currentUserModel = new User();
                        currentUserModel.setUid(firebaseUser.getUid());
                        currentUserModel.setEmail(firebaseUser.getEmail());
                    }
                    currentUserModel.setProfileImage(photoUrl);

                    firestoreRepository.saveUserProfile(currentUserModel)
                            .addOnSuccessListener(aVoid -> {
                                setLoadingState(false);
                                Toast.makeText(ProfileActivity.this, "प्रोफाइल फोटो अपडेट झाला!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> setLoadingState(false));
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(ProfileActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileChanges() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) return;

        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String mobile = binding.etMobile.getText() != null ? binding.etMobile.getText().toString().trim() : "";
        String address = binding.etAddress.getText() != null ? binding.etAddress.getText().toString().trim() : "";

        binding.tilName.setError(null);
        binding.tilMobile.setError(null);
        binding.tilAddress.setError(null);

        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError(getString(R.string.err_name_required));
            return;
        }

        if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
            binding.tilMobile.setError(getString(R.string.err_mobile_invalid));
            return;
        }

        if (TextUtils.isEmpty(address)) {
            binding.tilAddress.setError(getString(R.string.err_address_required));
            return;
        }

        setLoadingState(true);

        String photoUrl = currentUserModel != null && currentUserModel.getProfileImage() != null ? currentUserModel.getProfileImage() : "";
        String role = currentUserModel != null && currentUserModel.getRole() != null ? currentUserModel.getRole() : "user";
        long createdAt = currentUserModel != null && currentUserModel.getCreatedAt() > 0 ? currentUserModel.getCreatedAt() : System.currentTimeMillis();

        User updatedUser = new User(
                firebaseUser.getUid(),
                name,
                mobile,
                firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                address,
                photoUrl,
                role,
                createdAt
        );

        firestoreRepository.saveUserProfile(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    currentUserModel = updatedUser;
                    binding.tvProfileName.setText(name);
                    Toast.makeText(ProfileActivity.this, "माहिती यशस्वीरीत्या अपडेट केली!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("लॉगआउट")
                .setMessage("तुम्हाला खात्री आहे की तुम्ही लॉगआउट करू इच्छिता?")
                .setPositiveButton("होय", (dialog, which) -> performLogout())
                .setNegativeButton("नाही", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        authRepository.logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveProfile.setEnabled(!isLoading);
        binding.btnChangePhoto.setEnabled(!isLoading);
    }
}