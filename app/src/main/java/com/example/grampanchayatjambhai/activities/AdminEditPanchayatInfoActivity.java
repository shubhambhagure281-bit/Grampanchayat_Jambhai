package com.example.grampanchayatjambhai.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityAdminEditPanchayatInfoBinding;
import com.example.grampanchayatjambhai.models.PanchayatInfo;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class AdminEditPanchayatInfoActivity extends AppCompatActivity {

    private ActivityAdminEditPanchayatInfoBinding binding;
    private FirestoreRepository firestoreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminEditPanchayatInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        loadExistingPanchayatInfo();

        binding.btnSavePanchayatInfo.setOnClickListener(v -> savePanchayatInfoData());
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void loadExistingPanchayatInfo() {
        setLoadingState(true);

        firestoreRepository.getPanchayatInfo()
                .addOnSuccessListener(documentSnapshot -> {
                    setLoadingState(false);
                    if (documentSnapshot.exists()) {
                        PanchayatInfo info = documentSnapshot.toObject(PanchayatInfo.class);
                        if (info != null) {
                            bindDataToInputs(info);
                        } else {
                            bindDefaultInputs();
                        }
                    } else {
                        bindDefaultInputs();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    bindDefaultInputs();
                });
    }

    private void bindDataToInputs(PanchayatInfo info) {
        binding.etPanchayatName.setText(info.getPanchayatName());
        binding.etVillage.setText(info.getVillage());
        binding.etTaluka.setText(info.getTaluka());
        binding.etDistrict.setText(info.getDistrict());
        binding.etEstablishedYear.setText(info.getEstablishedYear());
        binding.etOfficeHours.setText(info.getOfficeHours());
        binding.etOfficeAddress.setText(info.getOfficeAddress());
        binding.etContactPhone.setText(info.getContactPhone());
        binding.etContactEmail.setText(info.getContactEmail());
    }

    private void bindDefaultInputs() {
        binding.etPanchayatName.setText(getString(R.string.app_name));
        binding.etVillage.setText("जांभई");
        binding.etTaluka.setText("जांभई परिसर");
        binding.etDistrict.setText("मुख्य जिल्हा");
        binding.etEstablishedYear.setText("१९६५");
        binding.etOfficeHours.setText("सकाळी १०:०० ते सायंकाळी ५:००");
        binding.etOfficeAddress.setText("ग्रामपंचायत कार्यालय, मु. पो. जांभई");
        binding.etContactPhone.setText("+९१ ९८७६५४३२१०");
        binding.etContactEmail.setText("grampanchayat.jambhai@gov.in");
    }

    private void savePanchayatInfoData() {
        String name = binding.etPanchayatName.getText() != null ? binding.etPanchayatName.getText().toString().trim() : "";
        String village = binding.etVillage.getText() != null ? binding.etVillage.getText().toString().trim() : "";
        String taluka = binding.etTaluka.getText() != null ? binding.etTaluka.getText().toString().trim() : "";
        String district = binding.etDistrict.getText() != null ? binding.etDistrict.getText().toString().trim() : "";
        String establishedYear = binding.etEstablishedYear.getText() != null ? binding.etEstablishedYear.getText().toString().trim() : "";
        String officeHours = binding.etOfficeHours.getText() != null ? binding.etOfficeHours.getText().toString().trim() : "";
        String officeAddress = binding.etOfficeAddress.getText() != null ? binding.etOfficeAddress.getText().toString().trim() : "";
        String contactPhone = binding.etContactPhone.getText() != null ? binding.etContactPhone.getText().toString().trim() : "";
        String contactEmail = binding.etContactEmail.getText() != null ? binding.etContactEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            binding.tilPanchayatName.setError("कृपया ग्रामपंचायतीचे नाव प्रविष्ट करा.");
            return;
        }

        setLoadingState(true);

        PanchayatInfo info = new PanchayatInfo(
                name,
                village,
                taluka,
                district,
                establishedYear,
                officeHours,
                officeAddress,
                contactPhone,
                contactEmail
        );

        firestoreRepository.savePanchayatInfo(info)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    Toast.makeText(AdminEditPanchayatInfoActivity.this, "ग्रामपंचायत माहिती यशस्वीरीत्या अपडेट झाली!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(AdminEditPanchayatInfoActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSavePanchayatInfo.setEnabled(!isLoading);
    }
}