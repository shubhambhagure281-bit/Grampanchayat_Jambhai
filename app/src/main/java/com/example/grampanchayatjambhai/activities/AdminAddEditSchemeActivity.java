package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityAdminAddEditSchemeBinding;
import com.example.grampanchayatjambhai.models.Scheme;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class AdminAddEditSchemeActivity extends AppCompatActivity {

    private ActivityAdminAddEditSchemeBinding binding;
    private FirestoreRepository firestoreRepository;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private String editingSchemeId = null;
    private String existingImageUrl = "";
    private long existingCreatedAt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddEditSchemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupImagePicker();
        checkEditingMode();

        binding.btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.btnRemoveImage.setOnClickListener(v -> clearImageSelection());
        binding.btnSaveScheme.setOnClickListener(v -> performSchemeSave());
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        binding.ivSchemeImagePreview.setImageURI(uri);
                        binding.ivSchemeImagePreview.setVisibility(View.VISIBLE);
                        binding.btnRemoveImage.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void clearImageSelection() {
        selectedImageUri = null;
        existingImageUrl = "";
        binding.ivSchemeImagePreview.setImageURI(null);
        binding.ivSchemeImagePreview.setVisibility(View.GONE);
        binding.btnRemoveImage.setVisibility(View.GONE);
    }

    private void checkEditingMode() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("scheme_id")) {
            editingSchemeId = intent.getStringExtra("scheme_id");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            existingImageUrl = intent.getStringExtra("image_url");
            String eligibility = intent.getStringExtra("eligibility");
            String requiredDocuments = intent.getStringExtra("required_documents");
            existingCreatedAt = intent.getLongExtra("created_at", System.currentTimeMillis());

            binding.topAppBar.setTitle("योजना अपडेट करा (Edit Scheme)");
            binding.etSchemeTitle.setText(title);
            binding.etSchemeDescription.setText(description);
            binding.etEligibility.setText(eligibility);
            binding.etRequiredDocuments.setText(requiredDocuments);

            if (!TextUtils.isEmpty(existingImageUrl)) {
                binding.ivSchemeImagePreview.setVisibility(View.VISIBLE);
                binding.btnRemoveImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(existingImageUrl)
                        .placeholder(R.drawable.ic_scheme)
                        .into(binding.ivSchemeImagePreview);
            }
        }
    }

    private void performSchemeSave() {
        String title = binding.etSchemeTitle.getText() != null ? binding.etSchemeTitle.getText().toString().trim() : "";
        String description = binding.etSchemeDescription.getText() != null ? binding.etSchemeDescription.getText().toString().trim() : "";
        String eligibility = binding.etEligibility.getText() != null ? binding.etEligibility.getText().toString().trim() : "";
        String requiredDocuments = binding.etRequiredDocuments.getText() != null ? binding.etRequiredDocuments.getText().toString().trim() : "";

        binding.tilSchemeTitle.setError(null);
        binding.tilSchemeDescription.setError(null);
        binding.tilEligibility.setError(null);
        binding.tilRequiredDocuments.setError(null);

        if (TextUtils.isEmpty(title)) {
            binding.tilSchemeTitle.setError("कृपया योजनेचे नाव प्रविष्ट करा.");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.tilSchemeDescription.setError("कृपया योजनेची सविस्तर माहिती प्रविष्ट करा.");
            return;
        }

        if (TextUtils.isEmpty(eligibility)) {
            binding.tilEligibility.setError("कृपया पात्रता व अटी प्रविष्ट करा.");
            return;
        }

        if (TextUtils.isEmpty(requiredDocuments)) {
            binding.tilRequiredDocuments.setError("कृपया आवश्यक कागदपत्रे प्रविष्ट करा.");
            return;
        }

        setLoadingState(true);

        String schemeId = TextUtils.isEmpty(editingSchemeId) ? "SCHEME-" + System.currentTimeMillis() : editingSchemeId;
        long createdAt = existingCreatedAt > 0 ? existingCreatedAt : System.currentTimeMillis();

        if (selectedImageUri != null) {
            firestoreRepository.uploadSchemeImage(schemeId, selectedImageUri)
                    .addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri != null ? downloadUri.toString() : "";
                        saveSchemeToFirestore(schemeId, title, description, imageUrl, eligibility, requiredDocuments, createdAt);
                    })
                    .addOnFailureListener(e -> {
                        saveSchemeToFirestore(schemeId, title, description, existingImageUrl, eligibility, requiredDocuments, createdAt);
                    });
        } else {
            saveSchemeToFirestore(schemeId, title, description, existingImageUrl, eligibility, requiredDocuments, createdAt);
        }
    }

    private void saveSchemeToFirestore(String schemeId, String title, String description, String imageUrl, String eligibility, String requiredDocuments, long createdAt) {
        Scheme scheme = new Scheme(schemeId, title, description, imageUrl, eligibility, requiredDocuments, createdAt);

        firestoreRepository.saveScheme(scheme)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    Toast.makeText(AdminAddEditSchemeActivity.this, "योजना यशस्वीरीत्या सेव्ह केली!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(AdminAddEditSchemeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveScheme.setEnabled(!isLoading);
        binding.btnSelectImage.setEnabled(!isLoading);
        binding.btnRemoveImage.setEnabled(!isLoading);
    }
}