package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.grampanchayatjambhai.adapters.DignitaryAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityDignitariesGalleryBinding;
import com.example.grampanchayatjambhai.models.Dignitary;
import com.example.grampanchayatjambhai.utils.DignitaryDataProvider;
import com.example.grampanchayatjambhai.utils.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DignitariesGalleryActivity extends AppCompatActivity implements DignitaryAdapter.OnDignitaryClickListener {

    private ActivityDignitariesGalleryBinding binding;
    private DignitaryAdapter adapter;

    private String currentDistrict = "छत्रपती संभाजीनगर";
    private String currentTaluka = "सिल्लोड";
    private String currentGP = "जांभई";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityDignitariesGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        loadDignitaries();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new DignitaryAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup(2));

        binding.rvDignitaries.setLayoutManager(layoutManager);
        binding.rvDignitaries.setAdapter(adapter);
    }

    private void loadDignitaries() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("district")) currentDistrict = intent.getStringExtra("district");
            if (intent.hasExtra("taluka")) currentTaluka = intent.getStringExtra("taluka");
            if (intent.hasExtra("gram_panchayat")) currentGP = intent.getStringExtra("gram_panchayat");
        }

        binding.tvLocationSubtitle.setText("ग्रामपंचायत " + currentGP + " | तालुका " + currentTaluka + " | जिल्हा " + currentDistrict);

        List<Dignitary> list = DignitaryDataProvider.getDignitariesForLocation(currentDistrict, currentTaluka, currentGP);
        adapter.setDignitaries(list);
    }

    @Override
    public void onDignitaryClick(Dignitary dignitary) {
        if (dignitary == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = dignitary.getLastVerifiedAt() > 0 ? sdf.format(new Date(dignitary.getLastVerifiedAt())) : "अद्ययावत";

        StringBuilder message = new StringBuilder();
        message.append("नाम: ").append(dignitary.getName()).append("\n\n");
        message.append("पदनाम: ").append(dignitary.getDesignation()).append("\n\n");
        if (!"-".equals(dignitary.getDistrict())) {
            message.append("जिल्हा: ").append(dignitary.getDistrict()).append("\n");
        }
        if (!"-".equals(dignitary.getTaluka())) {
            message.append("तालुका: ").append(dignitary.getTaluka()).append("\n");
        }
        if (!"-".equals(dignitary.getGramPanchayat())) {
            message.append("ग्रामपंचायत: ").append(dignitary.getGramPanchayat()).append("\n");
        }
        message.append("\nअधिकृत स्रोत: ").append(TextUtils.isEmpty(dignitary.getSourceName()) ? "महाराष्ट्र शासन नोंद" : dignitary.getSourceName());
        message.append("\nअंतिम पडताळणी तारीख: ").append(dateStr);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("पदाधिकारी अधिकृत तपशील")
                .setMessage(message.toString())
                .setPositiveButton("ठीक आहे", (dialog, which) -> dialog.dismiss());

        if (!TextUtils.isEmpty(dignitary.getSourceUrl()) && dignitary.getSourceUrl().startsWith("http")) {
            builder.setNeutralButton("अधिकृत पोर्टल उघडा 🌐", (dialog, which) -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dignitary.getSourceUrl()));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(DignitariesGalleryActivity.this, "पोर्टल उघडण्यात अक्षम.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        builder.show();
    }
}