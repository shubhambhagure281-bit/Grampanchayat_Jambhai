package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityAdminAddEditMemberBinding;
import com.example.grampanchayatjambhai.models.Member;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class AdminAddEditMemberActivity extends AppCompatActivity {

    private ActivityAdminAddEditMemberBinding binding;
    private FirestoreRepository firestoreRepository;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private String editingMemberId = null;
    private String existingPhotoUrl = "";

    private static final String[] MEMBER_POSITIONS = new String[]{
            "सरपंच",
            "उपसरपंच",
            "ग्रामसेवक",
            "ग्रामपंचायत सदस्य",
            "लिपीक / कर्मचारी"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddEditMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupDropdown();
        setupImagePicker();
        checkEditingMode();

        binding.btnSelectPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.btnRemovePhoto.setOnClickListener(v -> clearPhotoSelection());
        binding.btnSaveMember.setOnClickListener(v -> performMemberSave());
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                MEMBER_POSITIONS
        );
        binding.actvPosition.setAdapter(adapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        binding.ivPhotoPreview.setImageURI(uri);
                        binding.cardPhotoPreview.setVisibility(View.VISIBLE);
                        binding.btnRemovePhoto.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void clearPhotoSelection() {
        selectedImageUri = null;
        existingPhotoUrl = "";
        binding.ivPhotoPreview.setImageURI(null);
        binding.cardPhotoPreview.setVisibility(View.GONE);
        binding.btnRemovePhoto.setVisibility(View.GONE);
    }

    private void checkEditingMode() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("member_id")) {
            editingMemberId = intent.getStringExtra("member_id");
            String name = intent.getStringExtra("name");
            String position = intent.getStringExtra("position");
            String phone = intent.getStringExtra("phone");
            existingPhotoUrl = intent.getStringExtra("photo_url");

            binding.topAppBar.setTitle("सदस्य माहिती अपडेट करा");
            binding.etName.setText(name);
            binding.actvPosition.setText(position, false);
            binding.etPhone.setText(phone);

            if (!TextUtils.isEmpty(existingPhotoUrl)) {
                binding.cardPhotoPreview.setVisibility(View.VISIBLE);
                binding.btnRemovePhoto.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(existingPhotoUrl)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.ivPhotoPreview);
            }
        }
    }

    private void performMemberSave() {
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String position = binding.actvPosition.getText() != null ? binding.actvPosition.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";

        binding.tilName.setError(null);
        binding.tilPosition.setError(null);

        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError(getString(R.string.err_name_required));
            return;
        }

        if (TextUtils.isEmpty(position)) {
            binding.tilPosition.setError("कृपया पद निवडा.");
            return;
        }

        setLoadingState(true);

        String memberId = TextUtils.isEmpty(editingMemberId) ? "MEMBER-" + System.currentTimeMillis() : editingMemberId;

        if (selectedImageUri != null) {
            firestoreRepository.uploadMemberPhoto(memberId, selectedImageUri)
                    .addOnSuccessListener(downloadUri -> {
                        String photoUrl = downloadUri != null ? downloadUri.toString() : "";
                        saveMemberToFirestore(memberId, name, position, photoUrl, phone);
                    })
                    .addOnFailureListener(e -> saveMemberToFirestore(memberId, name, position, existingPhotoUrl, phone));
        } else {
            saveMemberToFirestore(memberId, name, position, existingPhotoUrl, phone);
        }
    }

    private void saveMemberToFirestore(String memberId, String name, String position, String photoUrl, String phone) {
        Member member = new Member(memberId, name, position, photoUrl, phone);

        firestoreRepository.saveMember(member)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    Toast.makeText(AdminAddEditMemberActivity.this, "सदस्य माहिती यशस्वीरीत्या सेव्ह झाली!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(AdminAddEditMemberActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveMember.setEnabled(!isLoading);
        binding.btnSelectPhoto.setEnabled(!isLoading);
        binding.btnRemovePhoto.setEnabled(!isLoading);
    }
}