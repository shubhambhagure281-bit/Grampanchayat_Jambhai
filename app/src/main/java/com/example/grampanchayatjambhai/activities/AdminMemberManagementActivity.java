package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.MemberAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityAdminMemberManagementBinding;
import com.example.grampanchayatjambhai.models.Member;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminMemberManagementActivity extends AppCompatActivity {

    private ActivityAdminMemberManagementBinding binding;
    private FirestoreRepository firestoreRepository;
    private MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMemberManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();

        binding.fabAddMember.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMemberManagementActivity.this, AdminAddEditMemberActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMembers();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new MemberAdapter();
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMembers.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadMembers);
    }

    private void loadMembers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setVisibility(View.GONE);

        firestoreRepository.getPanchayatMembers()
                .addOnSuccessListener(querySnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);

                    List<Member> members = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Member m = doc.toObject(Member.class);
                            if (m != null) {
                                m.setId(doc.getId());
                                members.add(m);
                            }
                        }
                    }

                    if (members.isEmpty()) {
                        showEmptyState();
                    } else {
                        adapter.setMemberList(members);
                        binding.rvMembers.setVisibility(View.VISIBLE);
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
        binding.rvMembers.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.VISIBLE);
    }
}