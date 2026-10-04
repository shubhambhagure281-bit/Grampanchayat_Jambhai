package com.example.grampanchayatjambhai.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.models.SchemeCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SchemeCategoryAdapter extends RecyclerView.Adapter<SchemeCategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(SchemeCategory category);
    }

    private final List<SchemeCategory> categoryList;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public SchemeCategoryAdapter(List<SchemeCategory> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scheme_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        SchemeCategory category = categoryList.get(position);
        holder.bind(category, position == selectedPosition, pos -> {
            if (pos != RecyclerView.NO_POSITION) {
                int previous = selectedPosition;
                selectedPosition = pos;
                notifyItemChanged(previous);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onCategoryClick(categoryList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardCategory;
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }

        public void bind(SchemeCategory category, boolean isSelected, OnPositionClickListener selectListener) {
            if (category == null) return;

            if (tvCategoryName != null) {
                tvCategoryName.setText(category.getName() != null ? category.getName() : "");
            }

            if (ivCategoryIcon != null && category.getIconResId() != 0) {
                ivCategoryIcon.setImageResource(category.getIconResId());
            }

            if (cardCategory != null) {
                if (isSelected) {
                    cardCategory.setCardBackgroundColor(Color.parseColor("#EAF5EC"));
                    cardCategory.setStrokeColor(Color.parseColor("#176B2C"));
                    cardCategory.setStrokeWidth(4); // 2dp
                    if (ivCategoryIcon != null) ivCategoryIcon.setColorFilter(Color.parseColor("#176B2C"));
                    if (tvCategoryName != null) tvCategoryName.setTextColor(Color.parseColor("#176B2C"));
                } else {
                    cardCategory.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    cardCategory.setStrokeColor(Color.parseColor("#E1E5E2"));
                    cardCategory.setStrokeWidth(2); // 1dp
                    if (ivCategoryIcon != null) ivCategoryIcon.setColorFilter(Color.parseColor("#6B7280"));
                    if (tvCategoryName != null) tvCategoryName.setTextColor(Color.parseColor("#202124"));
                }
            }

            itemView.setOnClickListener(v -> {
                if (selectListener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        selectListener.onPositionClick(pos);
                    }
                }
            });
        }

        public interface OnPositionClickListener {
            void onPositionClick(int position);
        }
    }
}