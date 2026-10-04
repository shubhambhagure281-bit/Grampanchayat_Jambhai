package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.CitizenServiceAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityCitizenServicesBinding;
import com.example.grampanchayatjambhai.models.CitizenService;
import com.example.grampanchayatjambhai.utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class CitizenServicesActivity extends AppCompatActivity implements CitizenServiceAdapter.OnServiceClickListener {

    private ActivityCitizenServicesBinding binding;
    private CitizenServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityCitizenServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupEmergencyDialButtons();
        setupRecyclerView();
        loadServicesList();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupEmergencyDialButtons() {
        binding.btnDial108.setOnClickListener(v -> dialNumber("108"));
        binding.btnDial112.setOnClickListener(v -> dialNumber("112"));
        binding.btnDial101.setOnClickListener(v -> dialNumber("101"));
        binding.btnDial1912.setOnClickListener(v -> dialNumber("1912"));
        binding.btnDialOffice.setOnClickListener(v -> dialNumber("+919876543210"));
    }

    private void dialNumber(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "कॉल करण्यात अक्षम.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new CitizenServiceAdapter(this);
        binding.rvCitizenServices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCitizenServices.setAdapter(adapter);
    }

    private void loadServicesList() {
        List<CitizenService> services = new ArrayList<>();

        // 1. Cast Certificate
        services.add(new CitizenService("1", "जात प्रमाणपत्र", "जात नोंदणी व दाखल्यासाठी ऑनलाईन अर्ज", R.drawable.ic_certificate, "CERTIFICATE"));

        // 2. Residence Certificate
        services.add(new CitizenService("2", "रहिवासी दाखला", "रहिवासी / डोमोसाइल दाखला ऑनलाईन अर्ज", R.drawable.ic_certificate, "CERTIFICATE"));

        // 3. Income Certificate
        services.add(new CitizenService("3", "उत्पन्न दाखला", "वार्षिक उत्पन्न दाखल्यासाठी ऑनलाईन अर्ज", R.drawable.ic_certificate, "CERTIFICATE"));

        // 4. Water Complaint
        services.add(new CitizenService("4", "पाणी तक्रार", "पिण्याचे पाणी, नळ जोडणी किंवा पाइपलाइन दुरुस्ती तक्रार", R.drawable.ic_water, "COMPLAINT"));

        // 5. Street Light Complaint
        services.add(new CitizenService("5", "स्ट्रीट लाईट तक्रार", "बंद पथदिवे / स्ट्रीट लाईट बंद असणेबाबत तक्रार", R.drawable.ic_light, "COMPLAINT"));

        // 6. Sanitation Complaint
        services.add(new CitizenService("6", "स्वच्छता तक्रार", "कचरा व्यवस्थापन, गटार स्वच्छता व आरोग्य तक्रार", R.drawable.ic_clean, "COMPLAINT"));

        // 7. Road Complaint
        services.add(new CitizenService("7", "रस्ता तक्रार", "रस्ता दुरुस्ती, खड्डे व नवीन रस्ता बांधकाम तक्रार", R.drawable.ic_road, "COMPLAINT"));

        // 8. Other Complaint
        services.add(new CitizenService("8", "इतर तक्रार", "ग्रामपंचायताशी संबंधित इतर सर्व तक्रारी व चौकशी", R.drawable.ic_complaint, "COMPLAINT"));

        adapter.setServiceList(services);
    }

    @Override
    public void onServiceClick(CitizenService service) {
        if ("COMPLAINT".equals(service.getCategory())) {
            Intent intent = new Intent(this, ComplaintActivity.class);
            intent.putExtra("complaint_type", service.getTitle().replace(" तक्रार", ""));
            startActivity(intent);
        } else {
            Toast.makeText(this, service.getTitle() + " - अर्ज लवकरच ऑनलाईन स्वीकारले जातील.", Toast.LENGTH_SHORT).show();
        }
    }
}