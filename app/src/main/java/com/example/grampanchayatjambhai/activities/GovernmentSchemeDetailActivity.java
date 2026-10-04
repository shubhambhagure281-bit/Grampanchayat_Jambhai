package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.databinding.ActivityGovernmentSchemeDetailBinding;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class GovernmentSchemeDetailActivity extends AppCompatActivity {

    private ActivityGovernmentSchemeDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityGovernmentSchemeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        displaySchemeDetails();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void displaySchemeDetails() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String name = intent.getStringExtra("scheme_name");
        String department = intent.getStringExtra("department");
        String shortDesc = intent.getStringExtra("short_description");
        String eligibility = intent.getStringExtra("eligibility");
        String documents = intent.getStringExtra("documents");
        String process = intent.getStringExtra("application_process");
        String officialWebsite = intent.getStringExtra("official_website");
        int iconResId = intent.getIntExtra("icon_res_id", 0);

        binding.tvSchemeName.setText(TextUtils.isEmpty(name) ? "-" : name);
        binding.tvDepartmentBadge.setText(TextUtils.isEmpty(department) ? "ग्रामपंचायत योजना" : department);
        binding.tvShortDescription.setText(TextUtils.isEmpty(shortDesc) ? "-" : shortDesc);
        binding.tvEligibility.setText(TextUtils.isEmpty(eligibility) ? "माहिती उपलब्ध नाही." : eligibility);
        binding.tvDocuments.setText(TextUtils.isEmpty(documents) ? "माहिती उपलब्ध नाही." : documents);
        binding.tvApplicationProcess.setText(TextUtils.isEmpty(process) ? "माहिती उपलब्ध नाही." : process);

        if (iconResId != 0) {
            binding.ivSchemeIcon.setImageResource(iconResId);
        }

        // Official Website Button Logic
        if (!TextUtils.isEmpty(officialWebsite) && (officialWebsite.startsWith("http://") || officialWebsite.startsWith("https://"))) {
            binding.btnOfficialWebsite.setVisibility(View.VISIBLE);
            binding.tvNoWebsite.setVisibility(View.GONE);
            binding.btnOfficialWebsite.setOnClickListener(v -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(officialWebsite));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(GovernmentSchemeDetailActivity.this, "वेबसाइट उघडण्यात अक्षम.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            binding.btnOfficialWebsite.setVisibility(View.GONE);
            binding.tvNoWebsite.setVisibility(View.VISIBLE);
        }

        // Contact Grampanchayat Button
        binding.btnContactPanchayat.setOnClickListener(v -> {
            Intent contactIntent = new Intent(GovernmentSchemeDetailActivity.this, ContactActivity.class);
            startActivity(contactIntent);
        });
    }
}