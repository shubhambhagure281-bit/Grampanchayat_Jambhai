package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.SchemeAdapter;
import com.example.grampanchayatjambhai.databinding.ActivitySchemeBinding;
import com.example.grampanchayatjambhai.models.Scheme;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemeActivity extends AppCompatActivity implements SchemeAdapter.OnSchemeClickListener {

    private ActivitySchemeBinding binding;
    private FirestoreRepository firestoreRepository;
    private SchemeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(this, GovernmentSchemesActivity.class);
        startActivity(intent);
        finish();
        super.onCreate(savedInstanceState);
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new SchemeAdapter(this);
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
                        schemes = getDefaultSchemes();
                    }

                    Collections.sort(schemes, (s1, s2) -> Long.compare(s2.getCreatedAt(), s1.getCreatedAt()));
                    adapter.setSchemeList(schemes);
                    binding.rvSchemes.setVisibility(View.VISIBLE);
                    binding.tvEmptyState.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    List<Scheme> defaultSchemes = getDefaultSchemes();
                    adapter.setSchemeList(defaultSchemes);
                    binding.rvSchemes.setVisibility(View.VISIBLE);
                });
    }

    private List<Scheme> getDefaultSchemes() {
        List<Scheme> schemes = new ArrayList<>();
        long now = System.currentTimeMillis();

        schemes.add(new Scheme(
                "1",
                "प्रधानमंत्री आवास योजना (ग्रामीण)",
                "बेघर व कच्च्या घरात राहणाऱ्या कुटुंबांना हक्काचे पक्के घर बांधण्यासाठी आर्थिक अनुदान.",
                "",
                "दारिद्र्यरेषेखालील (BPL) व सामाजिक-आर्थिक जात जनगणना (SECC) यादीतील नागरिक.",
                "१. आधार कार्ड\n२. रेशन कार्ड\n३. बँक पासबुक झेरॉक्स\n४. जागेचा ८/अ दाखला\n५. पासपोर्ट साईज फोटो",
                now
        ));

        schemes.add(new Scheme(
                "2",
                "जल जीवन मिशन (पाणी नळ जोडणी)",
                "ग्रामीण भागातील प्रत्येक घराला वैयक्तिक नळ जोडणीद्वारे शुद्ध पिण्याचे पाणी पुरवणे.",
                "",
                "ग्रामपंचायत हद्दीतील सर्व कायमस्वरूपी रहिवासी कुटुंबे.",
                "१. आधार कार्ड\n२. घरपट्टी/पाणीपट्टी भरल्याची पावती\n३. विहित नमून्यातील अर्ज",
                now - 86400000L
        ));

        schemes.add(new Scheme(
                "3",
                "संजय गांधी निराधार अनुदान योजना",
                "निराधार, अपंग, वृद्ध व गरजू नागरिकांना दरमहा आर्थिक सहाय्य योजना.",
                "",
                "६५ वर्षांवरील वृद्ध, विधवा, अपंग व घटस्फोटीत महिला ज्यांचे वार्षिक उत्पन्न अल्प आहे.",
                "१. वयाचा दाखला\n२. तहसीलदार यांचा उत्पन्नाचा दाखला\n३. रहिवासी दाखला\n४. आधार कार्ड व बँक डिटेल",
                now - 172800000L
        ));

        return schemes;
    }

    @Override
    public void onSchemeClick(Scheme scheme) {
        Intent intent = new Intent(this, SchemeDetailsActivity.class);
        intent.putExtra("scheme_id", scheme.getId());
        intent.putExtra("title", scheme.getTitle());
        intent.putExtra("description", scheme.getDescription());
        intent.putExtra("image_url", scheme.getImageUrl());
        intent.putExtra("eligibility", scheme.getEligibility());
        intent.putExtra("required_documents", scheme.getRequiredDocuments());
        startActivity(intent);
    }
}