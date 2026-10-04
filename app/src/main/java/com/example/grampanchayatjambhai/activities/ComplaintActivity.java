package com.example.grampanchayatjambhai.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityComplaintBinding;
import com.example.grampanchayatjambhai.models.Complaint;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;

public class ComplaintActivity extends AppCompatActivity {

    private ActivityComplaintBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private static final String[] COMPLAINT_TYPES = new String[]{
            "पाणी",
            "स्ट्रीट लाईट",
            "स्वच्छता",
            "रस्ता",
            "इतर"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupDropdown();
        setupImagePicker();
        loadUserProfile();

        binding.btnAttachPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        binding.btnRemovePhoto.setOnClickListener(v -> clearPhotoSelection());

        binding.btnSubmitComplaint.setOnClickListener(v -> performComplaintSubmission());
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                COMPLAINT_TYPES
        );
        binding.actvComplaintType.setAdapter(adapter);

        // Pre-fill type if passed in intent extra
        if (getIntent() != null && getIntent().hasExtra("complaint_type")) {
            String prefilledType = getIntent().getStringExtra("complaint_type");
            binding.actvComplaintType.setText(prefilledType, false);
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        binding.ivPhotoPreview.setImageURI(uri);
                        binding.ivPhotoPreview.setVisibility(View.VISIBLE);
                        binding.btnRemovePhoto.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void clearPhotoSelection() {
        selectedImageUri = null;
        binding.ivPhotoPreview.setImageURI(null);
        binding.ivPhotoPreview.setVisibility(View.GONE);
        binding.btnRemovePhoto.setVisibility(View.GONE);
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            firestoreRepository.getUserProfile(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (!TextUtils.isEmpty(user.getName())) {
                                    binding.etName.setText(user.getName());
                                }
                                if (!TextUtils.isEmpty(user.getMobile())) {
                                    binding.etMobile.setText(user.getMobile());
                                }
                            }
                        }
                    });
        }
    }

    private void performComplaintSubmission() {
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String mobile = binding.etMobile.getText() != null ? binding.etMobile.getText().toString().trim() : "";
        String complaintType = binding.actvComplaintType.getText() != null ? binding.actvComplaintType.getText().toString().trim() : "";
        String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";

        // Reset errors
        binding.tilName.setError(null);
        binding.tilMobile.setError(null);
        binding.tilComplaintType.setError(null);
        binding.tilDescription.setError(null);

        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError(getString(R.string.err_name_required));
            return;
        }

        if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
            binding.tilMobile.setError(getString(R.string.err_mobile_invalid));
            return;
        }

        if (TextUtils.isEmpty(complaintType)) {
            binding.tilComplaintType.setError(getString(R.string.error_occurred));
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.tilDescription.setError(getString(R.string.error_occurred));
            return;
        }

        FirebaseUser currentUser = authRepository.getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        String complaintId = "GPJ-COMP-" + System.currentTimeMillis();

        setLoadingState(true);

        if (selectedImageUri != null) {
            // Upload photo first then save complaint
            firestoreRepository.uploadComplaintPhoto(userId, complaintId, selectedImageUri)
                    .addOnSuccessListener(downloadUri -> {
                        String photoUrl = downloadUri != null ? downloadUri.toString() : "";
                        saveComplaintToFirestore(complaintId, userId, name, mobile, complaintType, description, photoUrl);
                    })
                    .addOnFailureListener(e -> {
                        // Fallback: save complaint without photo if upload fails
                        saveComplaintToFirestore(complaintId, userId, name, mobile, complaintType, description, "");
                    });
        } else {
            saveComplaintToFirestore(complaintId, userId, name, mobile, complaintType, description, "");
        }
    }

    private void saveComplaintToFirestore(String complaintId, String userId, String userName, String userMobile, String type, String description, String photoUrl) {
        long timestamp = System.currentTimeMillis();
        Complaint complaint = new Complaint(
                complaintId,
                userId,
                userName,
                userMobile,
                type,
                description,
                photoUrl,
                "प्रलंबित", // Status: Pending
                timestamp,
                timestamp
        );

        firestoreRepository.submitComplaintWithId(complaint)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    showSuccessDialog(complaintId);
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(ComplaintActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void showSuccessDialog(String complaintId) {
        new AlertDialog.Builder(this)
                .setTitle("तक्रार नोंदवली!")
                .setMessage("आपली तक्रार यशस्वीरीत्या नोंदवली आहे.\n\nतक्रार क्रमांक: " + complaintId)
                .setPositiveButton("ठीक आहे", (dialog, which) -> {
                    dialog.dismiss();
                    clearForm();
                })
                .setCancelable(false)
                .show();
    }

    private void clearForm() {
        binding.etDescription.setText("");
        binding.actvComplaintType.setText("", false);
        clearPhotoSelection();
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSubmitComplaint.setEnabled(!isLoading);
        binding.btnAttachPhoto.setEnabled(!isLoading);
        binding.btnRemovePhoto.setEnabled(!isLoading);
    }
}