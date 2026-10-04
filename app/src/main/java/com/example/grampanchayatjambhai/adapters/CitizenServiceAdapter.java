package com.example.grampanchayatjambhai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grampanchayatjambhai.databinding.ItemCitizenServiceBinding;
import com.example.grampanchayatjambhai.models.CitizenService;

import java.util.ArrayList;
import java.util.List;

public class CitizenServiceAdapter extends RecyclerView.Adapter<CitizenServiceAdapter.ServiceViewHolder> {

    public interface OnServiceClickListener {
        void onServiceClick(CitizenService service);
    }

    private final List<CitizenService> serviceList = new ArrayList<>();
    private final OnServiceClickListener listener;

    public CitizenServiceAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    public void setServiceList(List<CitizenService> services) {
        this.serviceList.clear();
        if (services != null) {
            this.serviceList.addAll(services);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCitizenServiceBinding binding = ItemCitizenServiceBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ServiceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        CitizenService service = serviceList.get(position);
        holder.bind(service, listener);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        private final ItemCitizenServiceBinding binding;

        public ServiceViewHolder(@NonNull ItemCitizenServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CitizenService service, OnServiceClickListener listener) {
            binding.tvServiceTitle.setText(service.getTitle());
            binding.tvServiceDescription.setText(service.getDescription());
            if (service.getIconRes() != 0) {
                binding.ivServiceIcon.setImageResource(service.getIconRes());
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onServiceClick(service);
                }
            });
        }
    }
}