package com.example.grampanchayatjambhai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.MemberAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityGramPanchayatInfoBinding;
import com.example.grampanchayatjambhai.models.Member;
import com.example.grampanchayatjambhai.models.PanchayatInfo;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GramPanchayatInfoActivity extends AppCompatActivity {

    private ActivityGramPanchayatInfoBinding binding;
    private FirestoreRepository firestoreRepository;
    private MemberAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityGramPanchayatInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRepository = new FirestoreRepository();

        setupToolbar();
        setupRecyclerView();
        setupGalleryButton();
        loadPanchayatData();
    }

    private void setupGalleryButton() {
        binding.cardDignitariesGallery.setOnClickListener(v -> {
            Intent intent = new Intent(GramPanchayatInfoActivity.this, DignitariesGalleryActivity.class);
            intent.putExtra("district", "छत्रपती संभाजीनगर");
            intent.putExtra("taluka", "सिल्लोड");
            intent.putExtra("gram_panchayat", "जांभई");
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        memberAdapter = new MemberAdapter();
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMembers.setAdapter(memberAdapter);
    }

    private void loadPanchayatData() {
        showLoading();

        firestoreRepository.getPanchayatInfo()
                .addOnSuccessListener(this::handleInfoSuccess)
                .addOnFailureListener(e -> {
                    // Load fallback default info if document not created in Firestore yet
                    displayDefaultPanchayatInfo();
                });

        loadMembers();
    }

    private void handleInfoSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot != null && documentSnapshot.exists()) {
            PanchayatInfo info = documentSnapshot.toObject(PanchayatInfo.class);
            if (info != null) {
                bindPanchayatInfo(info);
            } else {
                displayDefaultPanchayatInfo();
            }
        } else {
            displayDefaultPanchayatInfo();
        }
    }

    private void bindPanchayatInfo(PanchayatInfo info) {
        binding.tvPanchayatName.setText(TextUtils.isEmpty(info.getPanchayatName()) ? getString(R.string.app_name) : info.getPanchayatName());
        binding.tvVillage.setText("गाव: " + (TextUtils.isEmpty(info.getVillage()) ? "जांभई" : info.getVillage()));
        binding.tvTalukaDistrict.setText("तालुका: " + (TextUtils.isEmpty(info.getTaluka()) ? "जांभई परिसर" : info.getTaluka()) + " | जिल्हा: " + (TextUtils.isEmpty(info.getDistrict()) ? "महाराष्ट्र" : info.getDistrict()));
        binding.tvEstablishedYear.setText("स्थापना वर्ष: " + (TextUtils.isEmpty(info.getEstablishedYear()) ? "१९६५" : info.getEstablishedYear()));

        binding.tvOfficeHours.setText("कार्यालयीन वेळ: " + (TextUtils.isEmpty(info.getOfficeHours()) ? "सकाळी १०:०० ते सायंकाळी ५:००" : info.getOfficeHours()));
        binding.tvOfficeAddress.setText("पत्ता: " + (TextUtils.isEmpty(info.getOfficeAddress()) ? "ग्रामपंचायत कार्यालय, मु. पो. जांभई" : info.getOfficeAddress()));
        binding.tvContactPhone.setText(getString(R.string.phone_label) + (TextUtils.isEmpty(info.getContactPhone()) ? "" : " / " + info.getContactPhone()));
        binding.tvContactEmail.setText(getString(R.string.email_label) + (TextUtils.isEmpty(info.getContactEmail()) ? "" : " / " + info.getContactEmail()));

        showContent();
    }

    private void displayDefaultPanchayatInfo() {
        PanchayatInfo defaultInfo = new PanchayatInfo(
                getString(R.string.app_name),
                "जांभई",
                "जांभई परिसर",
                "मुख्य जिल्हा",
                "१९६५",
                "सकाळी १०:०० ते सायंकाळी ५:००",
                "ग्रामपंचायत कार्यालय, मु. पो. जांभई",
                "+९१ ९८७६५४३२१०",
                "grampanchayat.jambhai@gov.in"
        );
        bindPanchayatInfo(defaultInfo);
    }

    private void loadMembers() {
        firestoreRepository.getPanchayatMembers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Member> members = new ArrayList<>();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Member member = doc.toObject(Member.class);
                            if (member != null) {
                                member.setId(doc.getId());
                                members.add(member);
                            }
                        }
                    }

                    if (members.isEmpty()) {
                        members = getDefaultMembers();
                    }
                    memberAdapter.setMemberList(members);
                })
                .addOnFailureListener(e -> {
                    // Fallback to default structural members if collection empty or offline
                    memberAdapter.setMemberList(getDefaultMembers());
                });
    }

    private List<Member> getDefaultMembers() {
        List<Member> defaultMembers = new ArrayList<>();
        defaultMembers.add(new Member("1", "मा. श्री. सरपंचसाहेब", "सरपंच", "", "+९१ ९८७६५४३२१०"));
        defaultMembers.add(new Member("2", "मा. सौ. उपसरपंचताई", "उपसरपंच", "", "+९१ ९८७६५४३२११"));
        defaultMembers.add(new Member("3", "मा. श्री. ग्रामसेवकसाहेब", "ग्रामसेवक", "", "+९१ ९८७६५४३२१२"));
        return defaultMembers;
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.svContent.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.GONE);
    }

    private void showContent() {
        binding.progressBar.setVisibility(View.GONE);
        binding.svContent.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setVisibility(View.GONE);
    }
}