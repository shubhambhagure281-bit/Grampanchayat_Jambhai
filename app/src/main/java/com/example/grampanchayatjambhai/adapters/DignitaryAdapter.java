package com.example.grampanchayatjambhai.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.models.Dignitary;

import java.util.ArrayList;
import java.util.List;

public class DignitaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_CARD = 1;

    public interface OnDignitaryClickListener {
        void onDignitaryClick(Dignitary dignitary);
    }

    public static class DisplayItem {
        public int type;
        public String headerTitle;
        public Dignitary dignitary;

        public DisplayItem(String headerTitle) {
            this.type = VIEW_TYPE_HEADER;
            this.headerTitle = headerTitle;
        }

        public DisplayItem(Dignitary dignitary) {
            this.type = VIEW_TYPE_CARD;
            this.dignitary = dignitary;
        }
    }

    private final List<DisplayItem> displayList = new ArrayList<>();
    private final OnDignitaryClickListener listener;

    public DignitaryAdapter(OnDignitaryClickListener listener) {
        this.listener = listener;
    }

    public void setDignitaries(List<Dignitary> list) {
        displayList.clear();
        if (list == null || list.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        String currentLevel = "";
        for (Dignitary d : list) {
            if (!d.getLevel().equals(currentLevel)) {
                currentLevel = d.getLevel();
                String headerText;
                switch (currentLevel) {
                    case "STATE":
                        headerText = "महाराष्ट्र राज्य";
                        break;
                    case "DISTRICT":
                        headerText = "जिल्हास्तर (" + (TextUtils.isEmpty(d.getDistrict()) ? "छत्रपती संभाजीनगर" : d.getDistrict()) + ")";
                        break;
                    case "TALUKA":
                        headerText = "पंचायत समिती / तालुका स्तर (" + (TextUtils.isEmpty(d.getTaluka()) ? "सिल्लोड" : d.getTaluka()) + ")";
                        break;
                    case "GRAM_PANCHAYAT":
                    default:
                        headerText = "ग्रामपंचायत स्तर (" + (TextUtils.isEmpty(d.getGramPanchayat()) ? "जांभई" : d.getGramPanchayat()) + ")";
                        break;
                }
                displayList.add(new DisplayItem(headerText));
            }
            displayList.add(new DisplayItem(d));
        }
        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup(int spanCount) {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getItemViewType(position) == VIEW_TYPE_HEADER) {
                    return spanCount;
                }
                return 1;
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_dignitary_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_dignitary_card, parent, false);
            return new CardViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DisplayItem item = displayList.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.headerTitle);
        } else if (holder instanceof CardViewHolder) {
            ((CardViewHolder) holder).bind(item.dignitary, listener);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSectionTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
        }

        public void bind(String title) {
            if (tvSectionTitle != null) {
                tvSectionTitle.setText(title);
            }
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private final TextView tvName;
        private final TextView tvDesignation;
        private final TextView tvJurisdiction;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvJurisdiction = itemView.findViewById(R.id.tvJurisdiction);
        }

        public void bind(Dignitary d, OnDignitaryClickListener listener) {
            if (d == null) return;

            Context context = itemView.getContext();

            if (tvName != null) {
                tvName.setText(TextUtils.isEmpty(d.getName()) ? "माहिती उपलब्ध नाही" : d.getName());
            }

            if (tvDesignation != null) {
                tvDesignation.setText(TextUtils.isEmpty(d.getDesignation()) ? "-" : d.getDesignation());
            }

            if (tvJurisdiction != null) {
                String loc;
                if ("GRAM_PANCHAYAT".equals(d.getLevel())) {
                    loc = "ग्रामपंचायत " + d.getGramPanchayat();
                } else if ("TALUKA".equals(d.getLevel())) {
                    loc = "तालुका " + d.getTaluka();
                } else if ("DISTRICT".equals(d.getLevel())) {
                    loc = "जिल्हा " + d.getDistrict();
                } else {
                    loc = "महाराष्ट्र राज्य";
                }
                tvJurisdiction.setText(loc);
            }

            if (ivPhoto != null) {
                int fallbackRes = d.getPhotoResId() != 0 ? d.getPhotoResId() : R.drawable.ic_dignitary_placeholder;
                ivPhoto.setImageResource(fallbackRes);

                String photoUrl = d.getPhotoUrl();
                if (!TextUtils.isEmpty(photoUrl) && photoUrl.startsWith("http")) {
                    try {
                        GlideUrl glideUrl = new GlideUrl(photoUrl, new LazyHeaders.Builder()
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .build());

                        Glide.with(context.getApplicationContext())
                                .load(glideUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(fallbackRes)
                                .error(fallbackRes)
                                .fallback(fallbackRes)
                                .into(ivPhoto);
                    } catch (Exception e) {
                        ivPhoto.setImageResource(fallbackRes);
                    }
                } else if (d.getPhotoResId() != 0) {
                    ivPhoto.setImageResource(d.getPhotoResId());
                }
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDignitaryClick(d);
                }
            });
        }
    }
}