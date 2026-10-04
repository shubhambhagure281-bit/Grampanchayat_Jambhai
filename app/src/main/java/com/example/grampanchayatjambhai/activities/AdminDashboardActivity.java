package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityAdminDashboardBinding;
import com.example.grampanchayatjambhai.models.Complaint;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.Constants;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        verifyAdminAuthorization();
        setupSwipeRefresh();
        setupClickListeners();
    }

    private void verifyAdminAuthorization() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) {
            blockUnauthorizedAccess();
            return;
        }

        firestoreRepository.getUserProfile(firebaseUser.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                            if (!TextUtils.isEmpty(user.getName())) {
                                binding.tvAdminName.setText(user.getName());
                            } else {
                                binding.tvAdminName.setText("मा. सरपंच / ग्रामसेवक");
                            }
                            loadDashboardStatisticsCounts();
                        } else {
                            blockUnauthorizedAccess();
                        }
                    } else {
                        blockUnauthorizedAccess();
                    }
                })
                .addOnFailureListener(e -> blockUnauthorizedAccess());
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadDashboardStatisticsCounts);
    }

    private void loadDashboardStatisticsCounts() {
        binding.swipeRefresh.setRefreshing(true);

        // 1. Load Complaints Counts
        firestoreRepository.getAllComplaints()
                .addOnSuccessListener(querySnapshot -> {
                    int total = 0;
                    int pending = 0;
                    int inProgress = 0;
                    int resolved = 0;

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        total = querySnapshot.size();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Complaint c = doc.toObject(Complaint.class);
                            if (c != null) {
                                String status = c.getStatus();
                                if (Constants.STATUS_RESOLVED.equals(status) || "मार्गी लावले".equals(status) || "पूर्ण".equals(status) || "Resolved".equalsIgnoreCase(status)) {
                                    resolved++;
                                } else if (Constants.STATUS_IN_PROGRESS.equals(status) || "प्रगतीपथावर".equals(status) || "काम सुरू".equals(status) || "In Progress".equalsIgnoreCase(status)) {
                                    inProgress++;
                                } else {
                                    pending++;
                                }
                            }
                        }
                    }

                    binding.tvTotalComplaintsCount.setText(String.valueOf(total));
                    binding.tvPendingComplaintsCount.setText(String.valueOf(pending));
                    binding.tvInProgressComplaintsCount.setText(String.valueOf(inProgress));
                    binding.tvResolvedComplaintsCount.setText(String.valueOf(resolved));
                    checkFinishRefresh();
                })
                .addOnFailureListener(e -> checkFinishRefresh());

        // 2. Load Notices Count
        firestoreRepository.getNotices()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot != null ? querySnapshot.size() : 0;
                    binding.tvNoticesCount.setText(String.valueOf(count));
                    checkFinishRefresh();
                })
                .addOnFailureListener(e -> checkFinishRefresh());

        // 3. Load Schemes Count
        firestoreRepository.getSchemes()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot != null ? querySnapshot.size() : 0;
                    binding.tvSchemesCount.setText(String.valueOf(count));
                    checkFinishRefresh();
                })
                .addOnFailureListener(e -> checkFinishRefresh());

        // 4. Load Users Count
        firestoreRepository.getAllUsers()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot != null ? querySnapshot.size() : 0;
                    binding.tvUsersCount.setText(String.valueOf(count));
                    checkFinishRefresh();
                })
                .addOnFailureListener(e -> checkFinishRefresh());
    }

    private void checkFinishRefresh() {
        binding.swipeRefresh.setRefreshing(false);
    }

    private void blockUnauthorizedAccess() {
        Toast.makeText(this, "अनधिकृत प्रवेश! प्रवेश नाकारला.", Toast.LENGTH_LONG).show();
        authRepository.logout();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        // --- Stat Cards Click Listeners ---
        binding.cardTotalComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminComplaintsActivity.class);
            startActivity(intent);
        });

        binding.cardPendingComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminComplaintsActivity.class);
            intent.putExtra("filter_status", Constants.STATUS_PENDING);
            startActivity(intent);
        });

        binding.cardInProgressComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminComplaintsActivity.class);
            intent.putExtra("filter_status", Constants.STATUS_IN_PROGRESS);
            startActivity(intent);
        });

        binding.cardResolvedComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminComplaintsActivity.class);
            intent.putExtra("filter_status", Constants.STATUS_RESOLVED);
            startActivity(intent);
        });

        binding.cardNoticesCount.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminNoticeManagementActivity.class);
            startActivity(intent);
        });

        binding.cardSchemesCount.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminSchemeManagementActivity.class);
            startActivity(intent);
        });

        binding.cardUsersCount.setOnClickListener(v ->
                Toast.makeText(AdminDashboardActivity.this, "वापरकर्ता व्यवस्थापन सुविधा लवकरच उपलब्ध होत आहे.", Toast.LENGTH_SHORT).show()
        );

        // --- Admin Menu Cards Click Listeners ---
        // 1. Complaint Management
        binding.cardManageComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminComplaintsActivity.class);
            startActivity(intent);
        });

        // 2. Notice Management
        binding.cardManageNotices.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminNoticeManagementActivity.class);
            startActivity(intent);
        });

        // 3. Scheme Management
        binding.cardManageSchemes.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminSchemeManagementActivity.class);
            startActivity(intent);
        });

        // 4. User Management
        binding.cardManageUsers.setOnClickListener(v ->
                Toast.makeText(AdminDashboardActivity.this, "वापरकर्ता व्यवस्थापन सुविधा लवकरच उपलब्ध होत आहे.", Toast.LENGTH_SHORT).show()
        );

        // 5. Panchayat Info
        binding.cardPanchayatInfo.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminEditPanchayatInfoActivity.class);
            startActivity(intent);
        });

        // 6. Member Management
        binding.cardManageMembers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminMemberManagementActivity.class);
            startActivity(intent);
        });

        // 7. Gallery Management
        binding.cardManageGallery.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminGalleryActivity.class);
            startActivity(intent);
        });

        binding.btnAdminLogout.setOnClickListener(v -> confirmAdminLogout());
    }

    private void confirmAdminLogout() {
        new AlertDialog.Builder(this)
                .setTitle("प्रशासन लॉगआउट")
                .setMessage("तुम्ही प्रशासन लोगिनमधून लॉगआउट करू इच्छिता?")
                .setPositiveButton("होय", (dialog, which) -> {
                    authRepository.logout();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("नाही", (dialog, which) -> dialog.dismiss())
                .show();
    }
}