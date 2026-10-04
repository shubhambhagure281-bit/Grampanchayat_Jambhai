package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivitySchemeDetailsBinding;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class SchemeDetailsActivity extends AppCompatActivity {

    private ActivitySchemeDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivitySchemeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        displaySchemeDetails();

        binding.btnContactOffice.setOnClickListener(v ->
                Toast.makeText(SchemeDetailsActivity.this, getString(R.string.phone_label) + "\n" + getString(R.string.email_label), Toast.LENGTH_LONG).show()
        );
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

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("image_url");
        String eligibility = intent.getStringExtra("eligibility");
        String requiredDocuments = intent.getStringExtra("required_documents");

        binding.tvSchemeTitle.setText(TextUtils.isEmpty(title) ? "-" : title);
        binding.tvSchemeDescription.setText(TextUtils.isEmpty(description) ? "-" : description);
        binding.tvEligibility.setText(TextUtils.isEmpty(eligibility) ? "माहिती उपलब्ध नाही." : eligibility);
        binding.tvRequiredDocuments.setText(TextUtils.isEmpty(requiredDocuments) ? "माहिती उपलब्ध नाही." : requiredDocuments);

        if (!TextUtils.isEmpty(imageUrl)) {
            binding.cardImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_scheme)
                    .error(R.drawable.ic_scheme)
                    .into(binding.ivSchemeImage);
        } else {
            binding.cardImage.setVisibility(View.GONE);
        }
    }
}