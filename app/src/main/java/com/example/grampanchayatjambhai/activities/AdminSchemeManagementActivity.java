package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.AdminSchemeAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityAdminSchemeManagementBinding;
import com.example.grampanchayatjambhai.models.Scheme;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminSchemeManagementActivity extends AppCompatActivity implements AdminSchemeAdapter.OnAdminSchemeActionListener {

    private ActivityAdminSchemeManagementBinding binding;
    private FirestoreRepository firestoreRepository;
    private AdminSchemeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSchemeManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();

        binding.fabAddScheme.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSchemeManagementActivity.this, AdminAddEditSchemeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchemes();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AdminSchemeAdapter(this);
        binding.rvSchemes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSchemes.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadSchemes);
    }

    private void loadSchemes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setVisibility(View.GONE);

        firestoreRepository.getSchemes()
                .addOnSuccessListener(querySnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);

                    List<Scheme> schemes = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Scheme scheme = doc.toObject(Scheme.class);
                            if (scheme != null) {
                                scheme.setId(doc.getId());
                                schemes.add(scheme);
                            }
                        }
                    }

                    if (schemes.isEmpty()) {
                        showEmptyState();
                    } else {
                        Collections.sort(schemes, (s1, s2) -> Long.compare(s2.getCreatedAt(), s1.getCreatedAt()));
                        adapter.setSchemeList(schemes);
                        binding.rvSchemes.setVisibility(View.VISIBLE);
                        binding.tvEmptyState.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    showEmptyState();
                });
    }

    private void showEmptyState() {
        binding.rvSchemes.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditScheme(Scheme scheme) {
        Intent intent = new Intent(this, AdminAddEditSchemeActivity.class);
        intent.putExtra("scheme_id", scheme.getId());
        intent.putExtra("title", scheme.getTitle());
        intent.putExtra("description", scheme.getDescription());
        intent.putExtra("image_url", scheme.getImageUrl());
        intent.putExtra("eligibility", scheme.getEligibility());
        intent.putExtra("required_documents", scheme.getRequiredDocuments());
        startActivity(intent);
    }

    @Override
    public void onDeleteScheme(Scheme scheme) {
        new AlertDialog.Builder(this)
                .setTitle("योजना हटवा")
                .setMessage("तुम्हाला खात्री आहे की तुम्ही ही योजना हटवू इच्छिता?")
                .setPositiveButton("होय, हटवा", (dialog, which) -> {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    firestoreRepository.deleteScheme(scheme.getId())
                            .addOnSuccessListener(aVoid -> {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(AdminSchemeManagementActivity.this, "योजना यशस्वीरीत्या हटवली!", Toast.LENGTH_SHORT).show();
                                loadSchemes();
                            })
                            .addOnFailureListener(e -> {
                                binding.progressBar.setVisibility(View.GONE);
                                String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                                Toast.makeText(AdminSchemeManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("रद्द करा", (dialog, which) -> dialog.dismiss())
                .show();
    }
}