package com.example.grampanchayatjambhai.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.FragmentComplaintsBinding;

public class ComplaintsFragment extends Fragment {

    private FragmentComplaintsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComplaintsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSubmitComplaint.setOnClickListener(v -> {
            String subject = binding.etSubject.getText() != null ? binding.etSubject.getText().toString().trim() : "";
            String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";

            if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(description)) {
                Toast.makeText(requireContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(requireContext(), getString(R.string.success_submitted), Toast.LENGTH_SHORT).show();
            binding.etSubject.setText("");
            binding.etDescription.setText("");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}