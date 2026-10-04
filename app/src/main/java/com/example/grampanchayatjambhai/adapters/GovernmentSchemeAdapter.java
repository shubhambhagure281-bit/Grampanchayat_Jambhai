package com.example.grampanchayatjambhai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.models.GovernmentScheme;

import java.util.ArrayList;
import java.util.List;

public class GovernmentSchemeAdapter extends RecyclerView.Adapter<GovernmentSchemeAdapter.SchemeViewHolder> {

    public interface OnSchemeClickListener {
        void onSchemeClick(GovernmentScheme scheme);
    }

    private final List<GovernmentScheme> schemeList = new ArrayList<>();
    private final OnSchemeClickListener listener;

    public GovernmentSchemeAdapter(OnSchemeClickListener listener) {
        this.listener = listener;
    }

    public void setSchemeList(List<GovernmentScheme> schemes) {
        this.schemeList.clear();
        if (schemes != null) {
            this.schemeList.addAll(schemes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_government_scheme, parent, false);
        return new SchemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder holder, int position) {
        GovernmentScheme scheme = schemeList.get(position);
        holder.bind(scheme, listener);
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    public static class SchemeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSchemeName;
        private final TextView tvDepartment;
        private final TextView tvShortDescription;
        private final ImageView ivSchemeIcon;

        public SchemeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSchemeName = itemView.findViewById(R.id.tvSchemeName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvShortDescription = itemView.findViewById(R.id.tvShortDescription);
            ivSchemeIcon = itemView.findViewById(R.id.ivSchemeIcon);
        }

        public void bind(GovernmentScheme scheme, OnSchemeClickListener listener) {
            if (scheme == null) return;

            if (tvSchemeName != null) {
                tvSchemeName.setText(scheme.getName() != null ? scheme.getName() : "");
            }

            if (tvDepartment != null) {
                tvDepartment.setText("विभाग: " + (scheme.getDepartment() != null ? scheme.getDepartment() : "-"));
            }

            if (tvShortDescription != null) {
                tvShortDescription.setText(scheme.getShortDescription() != null ? scheme.getShortDescription() : "");
            }

            if (ivSchemeIcon != null && scheme.getIconResId() != 0) {
                ivSchemeIcon.setImageResource(scheme.getIconResId());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSchemeClick(scheme);
                }
            });
        }
    }
}