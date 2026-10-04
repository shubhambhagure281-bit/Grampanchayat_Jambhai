package com.example.grampanchayatjambhai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.activities.NoticeDetailsActivity;
import com.example.grampanchayatjambhai.adapters.NoticeAdapter;
import com.example.grampanchayatjambhai.databinding.FragmentNoticesBinding;
import com.example.grampanchayatjambhai.models.Notice;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoticesFragment extends Fragment implements NoticeAdapter.OnNoticeClickListener {

    private FragmentNoticesBinding binding;
    private FirestoreRepository firestoreRepository;
    private NoticeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoticesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreRepository = new FirestoreRepository();
        adapter = new NoticeAdapter(this);
        binding.rvNotices.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotices.setAdapter(adapter);

        loadNoticesFromFirestore();
    }

    private void loadNoticesFromFirestore() {
        firestoreRepository.getNotices()
                .addOnSuccessListener(querySnapshot -> {
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
                        notices = getDefaultNotices();
                    }

                    Collections.sort(notices, (n1, n2) -> Long.compare(n2.getCreatedAt(), n1.getCreatedAt()));
                    adapter.setNoticeList(notices);
                })
                .addOnFailureListener(e -> adapter.setNoticeList(getDefaultNotices()));
    }

    private List<Notice> getDefaultNotices() {
        List<Notice> notices = new ArrayList<>();
        long now = System.currentTimeMillis();
        notices.add(new Notice("1", "विशेष ग्रामसभा बैठक सूचना", "दिनांक १५ ऑगस्ट रोजी स्वातंत्र्य दिनानिमित्त विशेष ग्रामसभेचे आयोजन करण्यात आले आहे. सर्व ग्रामस्थांनी उपस्थित राहावे.", "", true, now));
        notices.add(new Notice("2", "पाणी पुरवठा दुरुस्तीबाबत", "मुख्य जलवाहिनी दुरुस्तीच्या कामासाठी उद्या सकाळी ८ ते १ या वेळेत पाणी पुरवठा बंद राहील.", "", false, now - 86400000L));
        return notices;
    }

    @Override
    public void onNoticeClick(Notice notice) {
        Intent intent = new Intent(requireContext(), NoticeDetailsActivity.class);
        intent.putExtra("notice_id", notice.getId());
        intent.putExtra("title", notice.getTitle());
        intent.putExtra("description", notice.getDescription());
        intent.putExtra("image_url", notice.getImageUrl());
        intent.putExtra("important", notice.isImportant());
        intent.putExtra("created_at", notice.getCreatedAt());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}