package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.ComplaintAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityMyComplaintsBinding;
import com.example.grampanchayatjambhai.models.Complaint;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyComplaintsActivity extends AppCompatActivity implements ComplaintAdapter.OnComplaintClickListener {

    private ActivityMyComplaintsBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;
    private ComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMyComplaintsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        loadUserComplaints();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ComplaintAdapter(this);
        binding.rvMyComplaints.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyComplaints.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadUserComplaints);
    }

    private void loadUserComplaints() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            showEmptyState("कृपया तक्रारी पाहण्यासाठी लॉगिन करा.");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setVisibility(View.GONE);

        firestoreRepository.getUserComplaints(currentUser.getUid())
                .addOnSuccessListener(querySnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);

                    List<Complaint> complaints = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Complaint complaint = doc.toObject(Complaint.class);
                            if (complaint != null) {
                                complaints.add(complaint);
                            }
                        }
                    }

                    if (complaints.isEmpty()) {
                        showEmptyState("तुमच्या कोणत्याही तक्रारी नोंदवलेल्या नाहीत.");
                    } else {
                        // Sort by createdAt descending
                        Collections.sort(complaints, (c1, c2) -> Long.compare(c2.getCreatedAt(), c1.getCreatedAt()));
                        adapter.setComplaintList(complaints);
                        binding.rvMyComplaints.setVisibility(View.VISIBLE);
                        binding.tvEmptyState.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    showEmptyState(errorMsg);
                });
    }

    private void showEmptyState(String message) {
        binding.rvMyComplaints.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setText(message);
    }

    @Override
    public void onComplaintClick(Complaint complaint) {
        Intent intent = new Intent(this, ComplaintDetailsActivity.class);
        intent.putExtra("complaint_id", complaint.getComplaintId());
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