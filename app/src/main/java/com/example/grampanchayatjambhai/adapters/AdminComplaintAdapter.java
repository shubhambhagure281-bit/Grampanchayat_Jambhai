package com.example.grampanchayatjambhai.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ItemAdminComplaintBinding;
import com.example.grampanchayatjambhai.models.Complaint;
import com.example.grampanchayatjambhai.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminComplaintAdapter extends RecyclerView.Adapter<AdminComplaintAdapter.ComplaintViewHolder> {

    public interface OnAdminComplaintClickListener {
        void onComplaintClick(Complaint complaint);
    }

    private final List<Complaint> complaintList = new ArrayList<>();
    private final OnAdminComplaintClickListener listener;

    public AdminComplaintAdapter(OnAdminComplaintClickListener listener) {
        this.listener = listener;
    }

    public void setComplaintList(List<Complaint> complaints) {
        this.complaintList.clear();
        if (complaints != null) {
            this.complaintList.addAll(complaints);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminComplaintBinding binding = ItemAdminComplaintBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ComplaintViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.bind(complaint, listener);
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminComplaintBinding binding;

        public ComplaintViewHolder(@NonNull ItemAdminComplaintBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Complaint complaint, OnAdminComplaintClickListener listener) {
            Context context = binding.getRoot().getContext();

            binding.tvComplaintId.setText("क्र: " + complaint.getComplaintId());

            String citizen = TextUtils.isEmpty(complaint.getUserName()) ? "नागरिक" : complaint.getUserName();
            if (!TextUtils.isEmpty(complaint.getUserMobile())) {
                citizen += " (" + complaint.getUserMobile() + ")";
            }
            binding.tvCitizenNameMobile.setText("नागरिक: " + citizen);

            binding.tvComplaintType.setText("प्रकार: " + complaint.getType());
            binding.tvComplaintDescription.setText(complaint.getDescription());

            String status = TextUtils.isEmpty(complaint.getStatus()) ? Constants.STATUS_PENDING : complaint.getStatus();
            binding.tvComplaintStatus.setText(status);

            if (Constants.STATUS_RESOLVED.equals(status) || "मार्गी लावले".equals(status) || "पूर्ण".equals(status) || "Resolved".equalsIgnoreCase(status)) {
                binding.tvComplaintStatus.setText(status);
                binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_resolved);
                binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(context, R.color.status_resolved));
            } else if (Constants.STATUS_IN_PROGRESS.equals(status) || "प्रगतीपथावर".equals(status) || "काम सुरू".equals(status) || "In Progress".equalsIgnoreCase(status)) {
                binding.tvComplaintStatus.setText(status);
                binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_pending);
                binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(context, R.color.secondary));
            } else if (Constants.STATUS_REJECTED.equals(status) || "नाकारली".equals(status) || "Rejected".equalsIgnoreCase(status)) {
                binding.tvComplaintStatus.setText(status);
                binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(context, R.color.status_rejected));
            } else {
                binding.tvComplaintStatus.setText(status);
                binding.tvComplaintStatus.setBackgroundResource(R.drawable.bg_status_pending);
                binding.tvComplaintStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
            }

            if (complaint.getCreatedAt() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                binding.tvComplaintDate.setText("तारीख: " + sdf.format(new Date(complaint.getCreatedAt())));
            } else {
                binding.tvComplaintDate.setText("");
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onComplaintClick(complaint);
                }
            });
        }
    }
}