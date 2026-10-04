package com.example.grampanchayatjambhai.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ItemAdminSchemeBinding;
import com.example.grampanchayatjambhai.models.Scheme;

import java.util.ArrayList;
import java.util.List;

public class AdminSchemeAdapter extends RecyclerView.Adapter<AdminSchemeAdapter.AdminSchemeViewHolder> {

    public interface OnAdminSchemeActionListener {
        void onEditScheme(Scheme scheme);
        void onDeleteScheme(Scheme scheme);
    }

    private final List<Scheme> schemeList = new ArrayList<>();
    private final OnAdminSchemeActionListener listener;

    public AdminSchemeAdapter(OnAdminSchemeActionListener listener) {
        this.listener = listener;
    }

    public void setSchemeList(List<Scheme> schemes) {
        this.schemeList.clear();
        if (schemes != null) {
            this.schemeList.addAll(schemes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminSchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminSchemeBinding binding = ItemAdminSchemeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AdminSchemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminSchemeViewHolder holder, int position) {
        Scheme scheme = schemeList.get(position);
        holder.bind(scheme, listener);
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    static class AdminSchemeViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminSchemeBinding binding;

        public AdminSchemeViewHolder(@NonNull ItemAdminSchemeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Scheme scheme, OnAdminSchemeActionListener listener) {
            binding.tvSchemeTitle.setText(scheme.getTitle());
            binding.tvSchemeDescription.setText(scheme.getDescription());

            if (!TextUtils.isEmpty(scheme.getImageUrl())) {
                binding.ivSchemeImage.setVisibility(View.VISIBLE);
                Glide.with(binding.getRoot().getContext())
                        .load(scheme.getImageUrl())
                        .placeholder(R.drawable.ic_scheme)
                        .error(R.drawable.ic_scheme)
                        .into(binding.ivSchemeImage);
            } else {
                binding.ivSchemeImage.setVisibility(View.GONE);
            }

            binding.btnEditScheme.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditScheme(scheme);
                }
            });

            binding.btnDeleteScheme.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteScheme(scheme);
                }
            });
        }
    }
}