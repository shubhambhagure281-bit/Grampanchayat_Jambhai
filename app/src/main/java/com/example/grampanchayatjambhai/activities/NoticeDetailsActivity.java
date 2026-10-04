package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityNoticeDetailsBinding;
import com.example.grampanchayatjambhai.utils.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoticeDetailsActivity extends AppCompatActivity {

    private ActivityNoticeDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        displayNoticeDetails();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void displayNoticeDetails() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("image_url");
        boolean important = intent.getBooleanExtra("important", false);
        long createdAt = intent.getLongExtra("created_at", 0);

        binding.tvNoticeTitle.setText(TextUtils.isEmpty(title) ? "-" : title);
        binding.tvNoticeDescription.setText(TextUtils.isEmpty(description) ? "-" : description);

        if (important) {
            binding.tvImportantBadge.setVisibility(View.VISIBLE);
        } else {
            binding.tvImportantBadge.setVisibility(View.GONE);
        }

        if (createdAt > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            binding.tvNoticeDate.setText("प्रसिद्धी तारीख: " + sdf.format(new Date(createdAt)));
        } else {
            binding.tvNoticeDate.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            binding.cardImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_notice)
                    .error(R.drawable.ic_notice)
                    .into(binding.ivNoticeImage);
        } else {
            binding.cardImage.setVisibility(View.GONE);
        }
    }
}