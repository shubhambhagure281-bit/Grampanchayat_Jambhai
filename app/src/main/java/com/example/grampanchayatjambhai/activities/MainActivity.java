package com.example.grampanchayatjambhai.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityMainBinding;
import com.example.grampanchayatjambhai.fragments.ComplaintsFragment;
import com.example.grampanchayatjambhai.fragments.HomeFragment;
import com.example.grampanchayatjambhai.fragments.NoticesFragment;
import com.example.grampanchayatjambhai.fragments.ServicesFragment;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_notices) {
                loadFragment(new NoticesFragment());
                return true;
            } else if (itemId == R.id.nav_services) {
                loadFragment(new ServicesFragment());
                return true;
            } else if (itemId == R.id.nav_complaints) {
                loadFragment(new ComplaintsFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}