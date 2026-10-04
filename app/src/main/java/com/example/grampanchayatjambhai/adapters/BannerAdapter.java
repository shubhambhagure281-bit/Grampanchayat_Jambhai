package com.example.grampanchayatjambhai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grampanchayatjambhai.databinding.ItemBannerSlideBinding;
import com.example.grampanchayatjambhai.models.BannerItem;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<BannerItem> bannerList;

    public BannerAdapter(List<BannerItem> bannerList) {
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerSlideBinding binding = ItemBannerSlideBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem item = bannerList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        private final ItemBannerSlideBinding binding;

        public BannerViewHolder(@NonNull ItemBannerSlideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BannerItem item) {
            if (item != null && item.getImageResId() != 0) {
                binding.ivBannerImage.setImageResource(item.getImageResId());
            } else {
                binding.ivBannerImage.setImageDrawable(null);
            }
        }
    }
}