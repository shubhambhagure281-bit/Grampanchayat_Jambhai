package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.AdminComplaintAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityAdminComplaintsBinding;
import com.example.grampanchayatjambhai.models.Complaint;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.Constants;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminComplaintsActivity extends AppCompatActivity implements AdminComplaintAdapter.OnAdminComplaintClickListener {

    private ActivityAdminComplaintsBinding binding;
    private FirestoreRepository firestoreRepository;
    private AdminComplaintAdapter adapter;

    private final List<Complaint> allComplaintsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminComplaintsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupSearchAndFilters();
        setupSwipeRefresh();
        loadAllComplaints();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AdminComplaintAdapter(this);
        binding.rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        binding.rvComplaints.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("filter_status")) {
            String filterStatus = intent.getStringExtra("filter_status");
            if (Constants.STATUS_PENDING.equals(filterStatus)) {
                binding.chipPending.setChecked(true);
            } else if (Constants.STATUS_IN_PROGRESS.equals(filterStatus)) {
                binding.chipInProgress.setChecked(true);
            } else if (Constants.STATUS_RESOLVED.equals(filterStatus)) {
                binding.chipResolved.setChecked(true);
            }
        }

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applySearchAndFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> applySearchAndFilter());
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadAllComplaints);
    }

    private void loadAllComplaints() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setVisibility(View.GONE);

        firestoreRepository.getAllComplaints()
                .addOnSuccessListener(querySnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);

                    allComplaintsList.clear();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Complaint c = doc.toObject(Complaint.class);
                            if (c != null) {
                                if (TextUtils.isEmpty(c.getComplaintId())) {
                                    c.setComplaintId(doc.getId());
                                }
                                allComplaintsList.add(c);
                            }
                        }
                    }

                    Collections.sort(allComplaintsList, (c1, c2) -> Long.compare(c2.getCreatedAt(), c1.getCreatedAt()));

                    applySearchAndFilter();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    showEmptyState("माहिती लोड करण्यात त्रुटी आली.");
                });
    }

    private void applySearchAndFilter() {
        String searchQuery = binding.etSearch.getText() != null ? binding.etSearch.getText().toString().trim().toLowerCase() : "";

        String selectedStatusFilter = null;
        int checkedChipId = binding.chipGroupStatus.getCheckedChipId();
        if (checkedChipId == R.id.chipPending) {
            selectedStatusFilter = Constants.STATUS_PENDING;
        } else if (checkedChipId == R.id.chipInProgress) {
            selectedStatusFilter = Constants.STATUS_IN_PROGRESS;
        } else if (checkedChipId == R.id.chipResolved) {
            selectedStatusFilter = Constants.STATUS_RESOLVED;
        }

        List<Complaint> filteredList = new ArrayList<>();

        for (Complaint c : allComplaintsList) {
            boolean matchesSearch = TextUtils.isEmpty(searchQuery)
                    || (c.getComplaintId() != null && c.getComplaintId().toLowerCase().contains(searchQuery))
                    || (c.getUserName() != null && c.getUserName().toLowerCase().contains(searchQuery))
                    || (c.getUserMobile() != null && c.getUserMobile().contains(searchQuery))
                    || (c.getType() != null && c.getType().toLowerCase().contains(searchQuery))
                    || (c.getDescription() != null && c.getDescription().toLowerCase().contains(searchQuery));

            boolean matchesStatus = selectedStatusFilter == null || selectedStatusFilter.equals(c.getStatus());

            if (matchesSearch && matchesStatus) {
                filteredList.add(c);
            }
        }

        if (filteredList.isEmpty()) {
            showEmptyState("कोणतीही तक्रार आढळली नाही.");
        } else {
            adapter.setComplaintList(filteredList);
            binding.rvComplaints.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String msg) {
        binding.rvComplaints.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setText(msg);
    }

    @Override
    public void onComplaintClick(Complaint complaint) {
        Intent intent = new Intent(this, AdminComplaintDetailsActivity.class);
        intent.putExtra("complaint_id", complaint.getComplaintId());
        intent.putExtra("user_id", complaint.getUserId());
        intent.putExtra("type", complaint.getType());
        intent.putExtra("description", complaint.getDescription());
        intent.putExtra("status", complaint.getStatus());
        intent.putExtra("photo_url", complaint.getPhotoUrl());
        intent.putExtra("created_at", complaint.getCreatedAt());
        intent.putExtra("updated_at", complaint.getUpdatedAt());
        intent.putExtra("user_name", complaint.getUserName());
        intent.putExtra("user_mobile", complaint.getUserMobile());
        startActivity(intent);
    }
}