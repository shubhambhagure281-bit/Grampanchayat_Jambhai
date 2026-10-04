package com.example.grampanchayatjambhai.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ItemAdminNoticeBinding;
import com.example.grampanchayatjambhai.models.Notice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminNoticeAdapter extends RecyclerView.Adapter<AdminNoticeAdapter.AdminNoticeViewHolder> {

    public interface OnAdminNoticeActionListener {
        void onEditNotice(Notice notice);
        void onDeleteNotice(Notice notice);
    }

    private final List<Notice> noticeList = new ArrayList<>();
    private final OnAdminNoticeActionListener listener;

    public AdminNoticeAdapter(OnAdminNoticeActionListener listener) {
        this.listener = listener;
    }

    public void setNoticeList(List<Notice> notices) {
        this.noticeList.clear();
        if (notices != null) {
            this.noticeList.addAll(notices);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminNoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminNoticeBinding binding = ItemAdminNoticeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AdminNoticeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.bind(notice, listener);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class AdminNoticeViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminNoticeBinding binding;

        public AdminNoticeViewHolder(@NonNull ItemAdminNoticeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notice notice, OnAdminNoticeActionListener listener) {
            binding.tvNoticeTitle.setText(notice.getTitle());
            binding.tvNoticeDescription.setText(notice.getDescription());

            if (notice.isImportant()) {
                binding.tvImportantBadge.setVisibility(View.VISIBLE);
            } else {
                binding.tvImportantBadge.setVisibility(View.GONE);
            }

            if (notice.getCreatedAt() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.tvNoticeDate.setText("दिनांक: " + sdf.format(new Date(notice.getCreatedAt())));
            } else {
                binding.tvNoticeDate.setText("");
            }

            if (!TextUtils.isEmpty(notice.getImageUrl())) {
                binding.ivNoticeImage.setVisibility(View.VISIBLE);
                Glide.with(binding.getRoot().getContext())
                        .load(notice.getImageUrl())
                        .placeholder(R.drawable.ic_notice)
                        .error(R.drawable.ic_notice)
                        .into(binding.ivNoticeImage);
            } else {
                binding.ivNoticeImage.setVisibility(View.GONE);
            }

            binding.btnEditNotice.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditNotice(notice);
                }
            });

            binding.btnDeleteNotice.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteNotice(notice);
                }
            });
        }
    }
}