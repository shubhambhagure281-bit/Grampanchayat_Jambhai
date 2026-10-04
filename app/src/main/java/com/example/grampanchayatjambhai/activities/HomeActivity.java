package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.BannerAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityHomeBinding;
import com.example.grampanchayatjambhai.models.BannerItem;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private int bannerListSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        loadUserProfile();
        setupToolbarNavigation();
        setupBannerSlider();
        setupDashboardGridListeners();
        setupBottomNavigation();
    }

    private void setupToolbarNavigation() {
        binding.topAppBar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupBannerSlider() {
        List<BannerItem> banners = new ArrayList<>();

        banners.add(new BannerItem("", "", R.drawable.grampanchayat));
        banners.add(new BannerItem("", "", R.drawable.shivsmarak));
        banners.add(new BannerItem("", "", R.drawable.devimandir));
        banners.add(new BannerItem("", "", R.drawable.hanumanmandir));
        banners.add(new BannerItem("", "", R.drawable.zpground));
        banners.add(new BannerItem("", "", R.drawable.vrukshwalli));

        bannerListSize = banners.size();
        BannerAdapter bannerAdapter = new BannerAdapter(banners);
        binding.viewPagerBanner.setAdapter(bannerAdapter);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerListSize > 0) {
                    int currentItem = binding.viewPagerBanner.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerListSize;
                    binding.viewPagerBanner.setCurrentItem(nextItem, true);
                    sliderHandler.postDelayed(this, 3000); // Auto slide every 3 seconds
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            firestoreRepository.getUserProfile(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null && user.getName() != null && !user.getName().trim().isEmpty()) {
                                binding.tvWelcomeGreeting.setText("नमस्कार, " + user.getName() + "!");
                            } else {
                                binding.tvWelcomeGreeting.setText("नमस्कार!");
                            }
                        }
                    })
                    .addOnFailureListener(e -> binding.tvWelcomeGreeting.setText("नमस्कार!"));
        }
    }

    private void setupDashboardGridListeners() {
        binding.cardGramInfo.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GramPanchayatInfoActivity.class);
            startActivity(intent);
        });

        binding.cardServices.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CitizenServicesActivity.class);
            startActivity(intent);
        });

        binding.cardNewComplaint.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ComplaintActivity.class);
            startActivity(intent);
        });

        binding.cardMyComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MyComplaintsActivity.class);
            startActivity(intent);
        });

        binding.cardNotices.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NoticeActivity.class);
            startActivity(intent);
        });

        binding.cardSchemes.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GovernmentSchemesActivity.class);
            startActivity(intent);
        });

        binding.cardContact.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showDashboardGrid();
                return true;
            } else if (itemId == R.id.nav_notices) {
                Intent intent = new Intent(HomeActivity.this, NoticeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void showDashboardGrid() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        }
        binding.svDashboardContent.setVisibility(View.VISIBLE);
    }
}