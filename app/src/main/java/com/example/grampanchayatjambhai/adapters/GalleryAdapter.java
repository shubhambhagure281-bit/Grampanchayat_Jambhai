package com.example.grampanchayatjambhai.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.models.GalleryItem;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public interface OnGalleryItemActionListener {
        void onDeleteClick(GalleryItem item);
    }

    private final List<GalleryItem> galleryList = new ArrayList<>();
    private final OnGalleryItemActionListener listener;

    public GalleryAdapter(OnGalleryItemActionListener listener) {
        this.listener = listener;
    }

    public void setGalleryList(List<GalleryItem> list) {
        galleryList.clear();
        if (list != null) {
            galleryList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_photo, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        GalleryItem item = galleryList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivGalleryImage;
        private final TextView tvGalleryTitle;
        private final TextView tvGalleryCategory;
        private final View btnDeletePhoto;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryImage = itemView.findViewById(R.id.ivGalleryImage);
            tvGalleryTitle = itemView.findViewById(R.id.tvGalleryTitle);
            tvGalleryCategory = itemView.findViewById(R.id.tvGalleryCategory);
            btnDeletePhoto = itemView.findViewById(R.id.btnDeletePhoto);
        }

        public void bind(GalleryItem item, OnGalleryItemActionListener listener) {
            if (item == null) return;

            Context context = itemView.getContext();

            if (tvGalleryTitle != null) {
                tvGalleryTitle.setText(TextUtils.isEmpty(item.getTitle()) ? "ग्रामपंचायत फोटो" : item.getTitle());
            }

            if (tvGalleryCategory != null) {
                tvGalleryCategory.setText(TextUtils.isEmpty(item.getCategory()) ? "ग्रामपंचायत" : item.getCategory());
            }

            if (ivGalleryImage != null) {
                String imgUrl = item.getImageUrl();
                if (!TextUtils.isEmpty(imgUrl)) {
                    if (imgUrl.startsWith("data:image")) {
                        try {
                            String base64Data = imgUrl.substring(imgUrl.indexOf(",") + 1);
                            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            if (bitmap != null) {
                                ivGalleryImage.setImageBitmap(bitmap);
                            } else {
                                ivGalleryImage.setImageResource(R.drawable.bg_village_field);
                            }
                        } catch (Exception e) {
                            ivGalleryImage.setImageResource(R.drawable.bg_village_field);
                        }
                    } else if (imgUrl.startsWith("http")) {
                        Glide.with(context.getApplicationContext())
                                .load(imgUrl)
                                .placeholder(R.drawable.bg_village_field)
                                .error(R.drawable.bg_village_field)
                                .into(ivGalleryImage);
                    } else {
                        ivGalleryImage.setImageResource(R.drawable.bg_village_field);
                    }
                } else {
                    ivGalleryImage.setImageResource(R.drawable.bg_village_field);
                }
            }

            if (btnDeletePhoto != null) {
                btnDeletePhoto.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(item);
                    }
                });
            }
        }
    }
}