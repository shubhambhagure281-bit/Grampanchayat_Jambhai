package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityAdminComplaintDetailsBinding;
import com.example.grampanchayatjambhai.models.NotificationModel;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.Constants;
import com.example.grampanchayatjambhai.utils.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminComplaintDetailsActivity extends AppCompatActivity {

    private ActivityAdminComplaintDetailsBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    private String complaintId;
    private String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminComplaintDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        displayComplaintDetails();
        setupUpdateStatusListener();
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

        complaintId = intent.getStringExtra("complaint_id");
        targetUserId = intent.getStringExtra("user_id");
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
        updateStatusPillUI(currentStatus);

        // Pre-check RadioButton
        if (Constants.STATUS_RESOLVED.equals(currentStatus)) {
            binding.rbResolved.setChecked(true);
        } else if (Constants.STATUS_IN_PROGRESS.equals(currentStatus)) {
            binding.rbInProgress.setChecked(true);
        } else {
            binding.rbPending.setChecked(true);
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

    private void updateStatusPillUI(String status) {
        binding.tvComplaintStatus.setText(status);
        if (Constants.STATUS_RESOLVED.equals(status)) {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_resolved);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.status_resolved));
        } else if (Constants.STATUS_IN_PROGRESS.equals(status)) {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_pending);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.secondary));
        } else {
            binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_pending);
            binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.status_pending));
        }
    }

    private void setupUpdateStatusListener() {
        binding.btnUpdateStatus.setOnClickListener(v -> {
            if (TextUtils.isEmpty(complaintId)) return;

            String newStatus = Constants.STATUS_PENDING;
            int selectedRadioId = binding.rgStatus.getCheckedRadioButtonId();
            if (selectedRadioId == R.id.rbResolved) {
                newStatus = Constants.STATUS_RESOLVED;
            } else if (selectedRadioId == R.id.rbInProgress) {
                newStatus = Constants.STATUS_IN_PROGRESS;
            }

            final String updatedStatus = newStatus;
            setLoadingState(true);

            firestoreRepository.updateComplaintStatus(complaintId, updatedStatus)
                    .addOnSuccessListener(aVoid -> {
                        setLoadingState(false);
                        updateStatusPillUI(updatedStatus);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                        binding.tvUpdatedAt.setText("अंतिम अपडेट: " + sdf.format(new Date()));
                        binding.tvUpdatedAt.setVisibility(View.VISIBLE);
                        Toast.makeText(AdminComplaintDetailsActivity.this, "तक्रार स्टेटस यशस्वीरीत्या अपडेट झाला!", Toast.LENGTH_SHORT).show();

                        // Log notification for targeted citizen
                        if (!TextUtils.isEmpty(targetUserId)) {
                            String notifId = "NOTIF-" + System.currentTimeMillis();
                            NotificationModel notification = new NotificationModel(
                                    notifId,
                                    targetUserId,
                                    "तक्रार स्थिती अपडेट (" + complaintId + ")",
                                    "तुमच्या तक्रारीची स्थिती आता '" + updatedStatus + "' करण्यात आली आहे.",
                                    "COMPLAINT_STATUS",
                                    complaintId,
                                    false,
                                    System.currentTimeMillis()
                            );
                            firestoreRepository.logNotification(notification);
                        }
                    })
                    .addOnFailureListener(e -> {
                        setLoadingState(false);
                        String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                        Toast.makeText(AdminComplaintDetailsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnUpdateStatus.setEnabled(!isLoading);
    }
}