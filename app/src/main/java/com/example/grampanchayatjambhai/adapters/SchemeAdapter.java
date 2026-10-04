package com.example.grampanchayatjambhai.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ItemSchemeBinding;
import com.example.grampanchayatjambhai.models.Scheme;

import java.util.ArrayList;
import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {

    public interface OnSchemeClickListener {
        void onSchemeClick(Scheme scheme);
    }

    private final List<Scheme> schemeList = new ArrayList<>();
    private final OnSchemeClickListener listener;

    public SchemeAdapter(OnSchemeClickListener listener) {
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
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSchemeBinding binding = ItemSchemeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new SchemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder holder, int position) {
        Scheme scheme = schemeList.get(position);
        holder.bind(scheme, listener);
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    static class SchemeViewHolder extends RecyclerView.ViewHolder {

        private final ItemSchemeBinding binding;

        public SchemeViewHolder(@NonNull ItemSchemeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Scheme scheme, OnSchemeClickListener listener) {
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

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSchemeClick(scheme);
                }
            });
        }
    }
}