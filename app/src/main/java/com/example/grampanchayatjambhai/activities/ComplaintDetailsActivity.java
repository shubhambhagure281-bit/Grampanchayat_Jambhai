package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityComplaintDetailsBinding;
import com.example.grampanchayatjambhai.utils.Constants;
import com.example.grampanchayatjambhai.utils.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComplaintDetailsActivity extends AppCompatActivity {

    private ActivityComplaintDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityComplaintDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        displayComplaintDetails();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void displayComplaintDetails() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String complaintId = intent.getStringExtra("complaint_id");
        String type = intent.getStringExtra("type");
        String description = intent.getStringExtra("description");
        String status = intent.getStringExtra("status");
        String photoUrl = intent.getStringExtra("photo_url");
        long createdAt = intent.getLongExtra("created_at", 0);
        long updatedAt = intent.getLongExtra("updated_at", 0);
        String userName = intent.getStringExtra("user_name");
        String userMobile = intent.getStringExtra("user_mobile");

        binding.tvComplaintId.setText(TextUtils.isEmpty(complaintId) ? "-" : complaintId);
        binding.tvComplaintType.setText("प्रकार: " + (TextUtils.isEmpty(type) ? "-" : type));
        binding.tvComplaintDescription.setText(TextUtils.isEmpty(description) ? "-" : description);

        String citizenDetails = "नागरिक: " + (TextUtils.isEmpty(userName) ? "नागरिक" : userName);
        if (!TextUtils.isEmpty(userMobile)) {
            citizenDetails += " (" + userMobile + ")";
        }
        binding.tvCitizenInfo.setText(citizenDetails);

        String currentStatus = TextUtils.isEmpty(status) ? Constants.STATUS_PENDING : status;
        binding.tvComplaintStatus.setText(currentStatus);

        if (Constants.STATUS_RESOLVED.equals(currentStatus)) {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_resolved);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.status_resolved));
        } else if (Constants.STATUS_IN_PROGRESS.equals(currentStatus)) {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_pending);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.secondary));
        } else if (Constants.STATUS_REJECTED.equals(currentStatus)) {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_rejected);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.status_rejected));
        } else {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_pending);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.status_pending));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        if (createdAt > 0) {
            binding.tvCreatedAt.setText("नोंदणी तारीख: " + sdf.format(new Date(createdAt)));
        } else {
            binding.tvCreatedAt.setVisibility(View.GONE);
        }

        if (updatedAt > 0) {
            binding.tvUpdatedAt.setText("अंतिम अपडेट: " + sdf.format(new Date(updatedAt)));
        } else {
            binding.tvUpdatedAt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(photoUrl)) {
            binding.cardPhoto.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_notice)
                    .error(R.drawable.ic_notice)
                    .into(binding.ivComplaintPhoto);
        } else {
            binding.cardPhoto.setVisibility(View.GONE);
        }
    }
}