package com.example.grampanchayatjambhai.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ItemNoticeBinding;
import com.example.grampanchayatjambhai.models.Notice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    public interface OnNoticeClickListener {
        void onNoticeClick(Notice notice);
    }

    private final List<Notice> noticeList = new ArrayList<>();
    private final OnNoticeClickListener listener;

    public NoticeAdapter() {
        this.listener = null;
    }

    public NoticeAdapter(OnNoticeClickListener listener) {
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
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoticeBinding binding = ItemNoticeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NoticeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.bind(notice, listener);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoticeBinding binding;

        public NoticeViewHolder(@NonNull ItemNoticeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notice notice, OnNoticeClickListener listener) {
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

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoticeClick(notice);
                }
            });
        }
    }
}