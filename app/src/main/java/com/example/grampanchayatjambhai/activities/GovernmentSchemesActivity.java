package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.adapters.GovernmentSchemeAdapter;
import com.example.grampanchayatjambhai.adapters.SchemeCategoryAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityGovernmentSchemesBinding;
import com.example.grampanchayatjambhai.models.GovernmentScheme;
import com.example.grampanchayatjambhai.models.SchemeCategory;
import com.example.grampanchayatjambhai.utils.SchemeDataProvider;
import com.example.grampanchayatjambhai.utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class GovernmentSchemesActivity extends AppCompatActivity implements
        SchemeCategoryAdapter.OnCategoryClickListener,
        GovernmentSchemeAdapter.OnSchemeClickListener {

    private ActivityGovernmentSchemesBinding binding;

    private List<SchemeCategory> categoryList;
    private List<GovernmentScheme> allSchemesList;

    private SchemeCategoryAdapter categoryAdapter;
    private GovernmentSchemeAdapter schemeAdapter;

    private String selectedCategoryName = "सर्व";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityGovernmentSchemesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        loadData();
        setupRecyclerViews();
        setupSearchFilter();
        applyFilter();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void loadData() {
        categoryList = SchemeDataProvider.getCategories();
        allSchemesList = SchemeDataProvider.getAllSchemes();
    }

    private void setupRecyclerViews() {
        // Horizontal Categories
        categoryAdapter = new SchemeCategoryAdapter(categoryList, this);
        binding.rvCategories.setAdapter(categoryAdapter);

        // Vertical Schemes
        schemeAdapter = new GovernmentSchemeAdapter(this);
        binding.rvSchemes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSchemes.setAdapter(schemeAdapter);
    }

    private void setupSearchFilter() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void applyFilter() {
        String searchQuery = binding.etSearch.getText() != null ?
                binding.etSearch.getText().toString().trim().toLowerCase() : "";

        List<GovernmentScheme> filteredList = new ArrayList<>();

        for (GovernmentScheme scheme : allSchemesList) {
            boolean matchesCategory = TextUtils.isEmpty(selectedCategoryName)
                    || "सर्व".equals(selectedCategoryName)
                    || (scheme.getCategory() != null && scheme.getCategory().contains(selectedCategoryName));

            boolean matchesSearch = TextUtils.isEmpty(searchQuery)
                    || (scheme.getName() != null && scheme.getName().toLowerCase().contains(searchQuery))
                    || (scheme.getCategory() != null && scheme.getCategory().toLowerCase().contains(searchQuery))
                    || (scheme.getDepartment() != null && scheme.getDepartment().toLowerCase().contains(searchQuery))
                    || (scheme.getShortDescription() != null && scheme.getShortDescription().toLowerCase().contains(searchQuery));

            if (matchesCategory && matchesSearch) {
                filteredList.add(scheme);
            }
        }

        binding.tvSchemeCount.setText("एकूण योजना: " + filteredList.size());

        if (filteredList.isEmpty()) {
            binding.rvSchemes.setVisibility(View.GONE);
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.rvSchemes.setVisibility(View.VISIBLE);
            binding.layoutEmptyState.setVisibility(View.GONE);
            schemeAdapter.setSchemeList(filteredList);
        }
    }

    @Override
    public void onCategoryClick(SchemeCategory category) {
        if (category != null) {
            selectedCategoryName = category.getName();
            applyFilter();
        }
    }

    @Override
    public void onSchemeClick(GovernmentScheme scheme) {
        if (scheme == null) return;
        Intent intent = new Intent(this, GovernmentSchemeDetailActivity.class);
        intent.putExtra("scheme_id", scheme.getId());
        intent.putExtra("scheme_name", scheme.getName());
        intent.putExtra("category", scheme.getCategory());
        intent.putExtra("department", scheme.getDepartment());
        intent.putExtra("short_description", scheme.getShortDescription());
        intent.putExtra("eligibility", scheme.getEligibility());
        intent.putExtra("documents", scheme.getDocuments());
        intent.putExtra("application_process", scheme.getApplicationProcess());
        intent.putExtra("official_website", scheme.getOfficialWebsite());
        intent.putExtra("icon_res_id", scheme.getIconResId());
        startActivity(intent);
    }
}