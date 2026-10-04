package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityContactBinding;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityContactBinding binding;

    // Gram Panchayat Jambhai Coordinates
    private static final double LATITUDE = 19.8762;
    private static final double LONGITUDE = 75.3421;
    private static final String PHONE_NUMBER = "+919876543210";
    private static final String EMAIL_ADDRESS = "grampanchayat.jambhai@gov.in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupMapFragment();
        setupActionButtons();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupActionButtons() {
        // Phone Call Intent
        binding.btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(callIntent);
        });

        // Email Intent
        binding.btnEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + EMAIL_ADDRESS));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ग्रामपंचायत जांभई नागरिक चौकशी");
            startActivity(emailIntent);
        });

        // External Google Maps Intent
        binding.btnOpenMap.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + LATITUDE + "," + LONGITUDE + "?q=" + LATITUDE + "," + LONGITUDE + "(ग्रामपंचायत जांभई)");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to web browser maps URL
                Uri webMapUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + LATITUDE + "," + LONGITUDE);
                Intent webMapIntent = new Intent(Intent.ACTION_VIEW, webMapUri);
                startActivity(webMapIntent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng jambhaiLocation = new LatLng(LATITUDE, LONGITUDE);
        googleMap.addMarker(new MarkerOptions()
                .position(jambhaiLocation)
                .title(getString(R.string.app_name)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jambhaiLocation, 15f));
    }
}