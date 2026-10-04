package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.AdminNoticeAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityAdminNoticeManagementBinding;
import com.example.grampanchayatjambhai.models.Notice;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminNoticeManagementActivity extends AppCompatActivity implements AdminNoticeAdapter.OnAdminNoticeActionListener {

    private ActivityAdminNoticeManagementBinding binding;
    private FirestoreRepository firestoreRepository;
    private AdminNoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminNoticeManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();

        binding.fabAddNotice.setOnClickListener(v -> {
            Intent intent = new Intent(AdminNoticeManagementActivity.this, AdminAddEditNoticeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotices();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AdminNoticeAdapter(this);
        binding.rvNotices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotices.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadNotices);
    }

    private void loadNotices() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setVisibility(View.GONE);

        firestoreRepository.getNotices()
                .addOnSuccessListener(querySnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);

                    List<Notice> notices = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Notice notice = Notice.parseNotice(doc);
                            if (notice != null) {
                                notices.add(notice);
                            }
                        }
                    }

                    if (notices.isEmpty()) {
                        showEmptyState();
                    } else {
                        Collections.sort(notices, (n1, n2) -> Long.compare(n2.getCreatedAt(), n1.getCreatedAt()));
                        adapter.setNoticeList(notices);
                        binding.rvNotices.setVisibility(View.VISIBLE);
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
        binding.rvNotices.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditNotice(Notice notice) {
        Intent intent = new Intent(this, AdminAddEditNoticeActivity.class);
        intent.putExtra("notice_id", notice.getId());
        intent.putExtra("title", notice.getTitle());
        intent.putExtra("description", notice.getDescription());
        intent.putExtra("image_url", notice.getImageUrl());
        intent.putExtra("important", notice.isImportant());
        intent.putExtra("created_at", notice.getCreatedAt());
        startActivity(intent);
    }

    @Override
    public void onDeleteNotice(Notice notice) {
        new AlertDialog.Builder(this)
                .setTitle("सूचना हटवा")
                .setMessage("तुम्हाला खात्री आहे की तुम्ही ही सूचना हटवू इच्छिता?")
                .setPositiveButton("होय, हटवा", (dialog, which) -> {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    firestoreRepository.deleteNotice(notice.getId())
                            .addOnSuccessListener(aVoid -> {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(AdminNoticeManagementActivity.this, "सूचना यशस्वीरीत्या हटवली!", Toast.LENGTH_SHORT).show();
                                loadNotices();
                            })
                            .addOnFailureListener(e -> {
                                binding.progressBar.setVisibility(View.GONE);
                                String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                                Toast.makeText(AdminNoticeManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("रद्द करा", (dialog, which) -> dialog.dismiss())
                .show();
    }
}