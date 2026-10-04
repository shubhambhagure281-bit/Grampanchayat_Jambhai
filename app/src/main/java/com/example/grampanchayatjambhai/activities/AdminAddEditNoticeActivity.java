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
import com.example.grampanchayatjambhai.databinding.ActivityAdminAddEditNoticeBinding;
import com.example.grampanchayatjambhai.models.Notice;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class AdminAddEditNoticeActivity extends AppCompatActivity {

    private ActivityAdminAddEditNoticeBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private String editingNoticeId = null;
    private String existingImageUrl = "";
    private long existingCreatedAt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddEditNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupImagePicker();
        checkEditingMode();

        binding.btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.btnRemoveImage.setOnClickListener(v -> clearImageSelection());
        binding.btnSaveNotice.setOnClickListener(v -> performNoticeSave());
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
                        binding.ivNoticeImagePreview.setImageURI(uri);
                        binding.ivNoticeImagePreview.setVisibility(View.VISIBLE);
                        binding.btnRemoveImage.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void clearImageSelection() {
        selectedImageUri = null;
        existingImageUrl = "";
        binding.ivNoticeImagePreview.setImageURI(null);
        binding.ivNoticeImagePreview.setVisibility(View.GONE);
        binding.btnRemoveImage.setVisibility(View.GONE);
    }

    private void checkEditingMode() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("notice_id")) {
            editingNoticeId = intent.getStringExtra("notice_id");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            existingImageUrl = intent.getStringExtra("image_url");
            boolean important = intent.getBooleanExtra("important", false);
            existingCreatedAt = intent.getLongExtra("created_at", System.currentTimeMillis());

            binding.topAppBar.setTitle("सूचना अपडेट करा (Edit Notice)");
            binding.etNoticeTitle.setText(title);
            binding.etNoticeDescription.setText(description);
            binding.cbImportant.setChecked(important);

            if (!TextUtils.isEmpty(existingImageUrl)) {
                binding.ivNoticeImagePreview.setVisibility(View.VISIBLE);
                binding.btnRemoveImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(existingImageUrl)
                        .placeholder(R.drawable.ic_notice)
                        .into(binding.ivNoticeImagePreview);
            }
        }
    }

    private void performNoticeSave() {
        String title = binding.etNoticeTitle.getText() != null ? binding.etNoticeTitle.getText().toString().trim() : "";
        String description = binding.etNoticeDescription.getText() != null ? binding.etNoticeDescription.getText().toString().trim() : "";
        boolean isImportant = binding.cbImportant.isChecked();

        binding.tilNoticeTitle.setError(null);
        binding.tilNoticeDescription.setError(null);

        if (TextUtils.isEmpty(title)) {
            binding.tilNoticeTitle.setError("कृपया सूचनेचे शीर्षक प्रविष्ट करा.");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.tilNoticeDescription.setError("कृपया सूचनेचे वर्णन प्रविष्ट करा.");
            return;
        }

        setLoadingState(true);

        String noticeId = TextUtils.isEmpty(editingNoticeId) ? "NOTICE-" + System.currentTimeMillis() : editingNoticeId;
        long createdAt = existingCreatedAt > 0 ? existingCreatedAt : System.currentTimeMillis();

        if (selectedImageUri != null) {
            // Upload photo then save notice
            firestoreRepository.uploadNoticeImage(noticeId, selectedImageUri)
                    .addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri != null ? downloadUri.toString() : "";
                        saveNoticeToFirestore(noticeId, title, description, imageUrl, isImportant, createdAt);
                    })
                    .addOnFailureListener(e -> {
                        // Fallback save without photo if upload fails
                        saveNoticeToFirestore(noticeId, title, description, existingImageUrl, isImportant, createdAt);
                    });
        } else {
            saveNoticeToFirestore(noticeId, title, description, existingImageUrl, isImportant, createdAt);
        }
    }

    private void saveNoticeToFirestore(String noticeId, String title, String description, String imageUrl, boolean important, long createdAt) {
        Notice notice = new Notice(noticeId, title, description, imageUrl, important, createdAt);

        firestoreRepository.saveNotice(notice)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    Toast.makeText(AdminAddEditNoticeActivity.this, "सूचना यशस्वीरीत्या प्रसिद्ध केली!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(AdminAddEditNoticeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveNotice.setEnabled(!isLoading);
        binding.btnSelectImage.setEnabled(!isLoading);
        binding.btnRemoveImage.setEnabled(!isLoading);
    }
}