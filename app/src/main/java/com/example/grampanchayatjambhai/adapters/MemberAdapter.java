package com.example.grampanchayatjambhai.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.databinding.ItemMemberBinding;
import com.example.grampanchayatjambhai.models.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final List<Member> memberList = new ArrayList<>();

    public void setMemberList(List<Member> members) {
        this.memberList.clear();
        if (members != null) {
            this.memberList.addAll(members);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMemberBinding binding = ItemMemberBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MemberViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {

        private final ItemMemberBinding binding;

        public MemberViewHolder(@NonNull ItemMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Member member) {
            binding.tvMemberName.setText(member.getName());
            binding.tvMemberPosition.setText(member.getPosition());
            if (!TextUtils.isEmpty(member.getPhone())) {
                binding.tvMemberPhone.setText(member.getPhone());
                binding.tvMemberPhone.setVisibility(View.VISIBLE);
            } else {
                binding.tvMemberPhone.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(member.getPhotoUrl())) {
                Glide.with(binding.getRoot().getContext())
                        .load(member.getPhotoUrl())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(binding.ivMemberPhoto);
            } else {
                binding.ivMemberPhoto.setImageResource(R.drawable.ic_person);
            }
        }
    }
}