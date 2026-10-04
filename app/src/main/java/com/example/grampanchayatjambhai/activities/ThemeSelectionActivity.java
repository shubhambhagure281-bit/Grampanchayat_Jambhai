package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.ThemeColorAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityThemeSelectionBinding;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class ThemeSelectionActivity extends AppCompatActivity {

    private ActivityThemeSelectionBinding binding;
    private ThemeColorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityThemeSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupDisplayModeRadioGroup();
        setupThemeColorsRecyclerView();
        setupApplyButton();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDisplayModeRadioGroup() {
        int currentNightMode = ThemeManager.getNightMode(this);
        if (currentNightMode == ThemeManager.MODE_LIGHT) {
            binding.rbModeLight.setChecked(true);
        } else if (currentNightMode == ThemeManager.MODE_DARK) {
            binding.rbModeDark.setChecked(true);
        } else {
            binding.rbModeSystem.setChecked(true);
        }
    }

    private void setupThemeColorsRecyclerView() {
        int currentColorTheme = ThemeManager.getColorTheme(this);
        adapter = new ThemeColorAdapter(currentColorTheme, themeIndex -> {
            // Theme selection listener
        });
        binding.rvThemeColors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvThemeColors.setAdapter(adapter);
    }

    private void setupApplyButton() {
        binding.btnApplyTheme.setOnClickListener(v -> {
            // 1. Determine Night Mode
            int selectedNightMode = ThemeManager.MODE_SYSTEM;
            int checkedRadioId = binding.rgDisplayMode.getCheckedRadioButtonId();
            if (checkedRadioId == R.id.rbModeLight) {
                selectedNightMode = ThemeManager.MODE_LIGHT;
            } else if (checkedRadioId == R.id.rbModeDark) {
                selectedNightMode = ThemeManager.MODE_DARK;
            }

            // 2. Determine Color Theme
            int selectedColorIndex = adapter.getSelectedThemeIndex();

            // 3. Save Preferences
            ThemeManager.setNightMode(this, selectedNightMode);
            ThemeManager.setColorTheme(this, selectedColorIndex);

            Toast.makeText(this, "नवीन थीम व कलर यशस्वीरीत्या लागू केले!", Toast.LENGTH_SHORT).show();

            // 4. Restart to apply globally
            Intent intent = new Intent(ThemeSelectionActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}