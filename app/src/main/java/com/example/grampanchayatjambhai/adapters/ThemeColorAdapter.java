package com.example.grampanchayatjambhai.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grampanchayatjambhai.databinding.ItemThemeColorBinding;
import com.example.grampanchayatjambhai.utils.ThemeManager;

public class ThemeColorAdapter extends RecyclerView.Adapter<ThemeColorAdapter.ThemeViewHolder> {

    public interface OnThemeSelectListener {
        void onThemeSelected(int themeIndex);
    }

    private int selectedThemeIndex;
    private final OnThemeSelectListener listener;

    public ThemeColorAdapter(int initialSelectedTheme, OnThemeSelectListener listener) {
        this.selectedThemeIndex = initialSelectedTheme;
        this.listener = listener;
    }

    public int getSelectedThemeIndex() {
        return selectedThemeIndex;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemThemeColorBinding binding = ItemThemeColorBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ThemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        holder.bind(position, selectedThemeIndex, themeIndex -> {
            selectedThemeIndex = themeIndex;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onThemeSelected(themeIndex);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ThemeManager.THEME_NAMES.length;
    }

    static class ThemeViewHolder extends RecyclerView.ViewHolder {

        private final ItemThemeColorBinding binding;

        public ThemeViewHolder(@NonNull ItemThemeColorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position, int selectedIndex, OnThemeSelectListener selectListener) {
            binding.tvThemeName.setText(ThemeManager.THEME_NAMES[position]);

            // Set color circle
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            try {
                shape.setColor(Color.parseColor(ThemeManager.THEME_HEX_COLORS[position]));
            } catch (Exception e) {
                shape.setColor(Color.GREEN);
            }
            binding.vColorPreview.setBackground(shape);

            // Selection state
            boolean isSelected = (position == selectedIndex);
            binding.ivSelectedCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            binding.getRoot().setOnClickListener(v -> {
                if (selectListener != null) {
                    selectListener.onThemeSelected(position);
                }
            });
        }
    }
}