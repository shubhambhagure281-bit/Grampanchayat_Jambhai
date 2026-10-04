package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.NoticeAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityNoticeBinding;
import com.example.grampanchayatjambhai.models.Notice;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoticeActivity extends AppCompatActivity implements NoticeAdapter.OnNoticeClickListener {

    private ActivityNoticeBinding binding;
    private FirestoreRepository firestoreRepository;
    private NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        loadNotices();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new NoticeAdapter(this);
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
                        // Sort newest notices first
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
    public void onNoticeClick(Notice notice) {
        Intent intent = new Intent(this, NoticeDetailsActivity.class);
        intent.putExtra("notice_id", notice.getId());
        intent.putExtra("title", notice.getTitle());
        intent.putExtra("description", notice.getDescription());
        intent.putExtra("image_url", notice.getImageUrl());
        intent.putExtra("important", notice.isImportant());
        intent.putExtra("created_at", notice.getCreatedAt());
        startActivity(intent);
    }
}