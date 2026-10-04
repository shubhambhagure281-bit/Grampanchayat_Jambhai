package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ActivityLoginBinding;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    private FirestoreRepository firestoreRepository;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        firestoreRepository = new FirestoreRepository();

        setupPhoneAuthCallbacks();
        setupClickListeners();
    }

    private void setupPhoneAuthCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                setLoadingState(false);
                signInWithPhoneCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                setLoadingState(false);
                String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                Toast.makeText(LoginActivity.this, "OTP त्रुटी: " + errorMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                setLoadingState(false);
                mVerificationId = verificationId;
                mResendToken = token;

                binding.layoutOtpSection.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "६ अंकी OTP तुमच्या मोबाईलवर पाठवण्यात आला आहे!", Toast.LENGTH_LONG).show();
            }
        };
    }

    private void setupClickListeners() {
        binding.btnSendOtp.setOnClickListener(v -> sendOtpToPhone(null));

        binding.btnVerifyOtp.setOnClickListener(v -> verifyOtpCode());

        binding.tvResendOtp.setOnClickListener(v -> sendOtpToPhone(mResendToken));

        binding.btnLogin.setOnClickListener(v -> performEmailLogin());

        binding.btnForgotPassword.setOnClickListener(v -> performForgotPassword());

        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.tvAdminLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }

    private void sendOtpToPhone(PhoneAuthProvider.ForceResendingToken resendToken) {
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";

        binding.tilPhone.setError(null);

        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            binding.tilPhone.setError(getString(R.string.err_mobile_invalid));
            return;
        }

        String fullPhoneNumber = "+91" + phone;
        setLoadingState(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks);

        if (resendToken != null) {
            builder.setForceResendingToken(resendToken);
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    private void verifyOtpCode() {
        String otpCode = binding.etOtpCode.getText() != null ? binding.etOtpCode.getText().toString().trim() : "";

        binding.tilOtp.setError(null);

        if (TextUtils.isEmpty(otpCode) || otpCode.length() != 6) {
            binding.tilOtp.setError("कृपया वैध ६ अंकी OTP टाका.");
            return;
        }

        if (TextUtils.isEmpty(mVerificationId)) {
            Toast.makeText(this, "कृपया आधी OTP मागवा.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
        signInWithPhoneCredential(credential);
    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        authRepository.signInWithPhoneCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        checkAndEnsureUserProfile(user);
                    } else {
                        setLoadingState(false);
                        Toast.makeText(LoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(LoginActivity.this, "OTP पडताळणी अयशस्वी: " + errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void checkAndEnsureUserProfile(FirebaseUser firebaseUser) {
        firestoreRepository.getUserProfile(firebaseUser.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
                        User newUser = new User(
                                firebaseUser.getUid(),
                                "नागरिक (" + phone + ")",
                                phone,
                                "",
                                "जांभई ग्रामपंचायत हद्द",
                                "",
                                "user",
                                System.currentTimeMillis()
                        );
                        firestoreRepository.saveUserProfile(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    setLoadingState(false);
                                    navigateToHome();
                                })
                                .addOnFailureListener(e -> {
                                    setLoadingState(false);
                                    navigateToHome();
                                });
                    } else {
                        setLoadingState(false);
                        navigateToHome();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    navigateToHome();
                });
    }

    private void performEmailLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.err_email_invalid));
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.err_password_short));
            return;
        }

        setLoadingState(true);

        authRepository.loginWithEmail(email, password)
                .addOnSuccessListener(authResult -> {
                    setLoadingState(false);
                    navigateToHome();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void performForgotPassword() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.err_email_invalid));
            return;
        }

        setLoadingState(true);
        authRepository.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    setLoadingState(false);
                    Toast.makeText(LoginActivity.this, getString(R.string.reset_password_sent), Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : getString(R.string.error_occurred);
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSendOtp.setEnabled(!isLoading);
        binding.btnVerifyOtp.setEnabled(!isLoading);
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnForgotPassword.setEnabled(!isLoading);
        binding.tvRegisterLink.setEnabled(!isLoading);
        binding.tvAdminLoginLink.setEnabled(!isLoading);
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}