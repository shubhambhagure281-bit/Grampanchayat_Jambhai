package com.example.grampanchayatjambhai.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.adapters.GalleryAdapter;
import com.example.grampanchayatjambhai.databinding.ActivityAdminGalleryBinding;
import com.example.grampanchayatjambhai.models.GalleryItem;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;
import com.example.grampanchayatjambhai.utils.Constants;
import com.example.grampanchayatjambhai.utils.FirebaseUtil;
import com.example.grampanchayatjambhai.utils.ThemeManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminGalleryActivity extends AppCompatActivity implements GalleryAdapter.OnGalleryItemActionListener {

    private ActivityAdminGalleryBinding binding;
    private GalleryAdapter adapter;
    private FirebaseFirestore db;
    private FirestoreRepository repository;

    private Uri pendingImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private ImageView currentDialogPreview = null;
    private View currentDialogPreviewLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseUtil.getFirestore();
        repository = new FirestoreRepository();

        setupToolbar();
        setupImagePicker();
        setupRecyclerView();
        loadGalleryPhotos();

        binding.fabAddPhoto.setOnClickListener(v -> showAddPhotoDialog());
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        pendingImageUri = uri;
                        if (currentDialogPreview != null) {
                            currentDialogPreview.setImageURI(uri);
                        }
                        if (currentDialogPreviewLayout != null) {
                            currentDialogPreviewLayout.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(this, "फोटो निवडला गेला!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupRecyclerView() {
        adapter = new GalleryAdapter(this);
        binding.rvGallery.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvGallery.setAdapter(adapter);
    }

    private void loadGalleryPhotos() {
        db.collection(Constants.COLLECTION_GALLERY)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GalleryItem> list = new ArrayList<>();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            GalleryItem item = doc.toObject(GalleryItem.class);
                            if (item != null) {
                                if (TextUtils.isEmpty(item.getId())) {
                                    item.setId(doc.getId());
                                }
                                list.add(item);
                            }
                        }
                    }

                    if (list.isEmpty()) {
                        binding.tvEmptyGallery.setText("कोणतेही गॅलरी फोटो उपलब्ध नाहीत.\nखालील '+' बटणावर क्लिक करून नवीन फोटो जोडा.");
                        binding.tvEmptyGallery.setVisibility(View.VISIBLE);
                        binding.rvGallery.setVisibility(View.GONE);
                    } else {
                        binding.tvEmptyGallery.setVisibility(View.GONE);
                        binding.rvGallery.setVisibility(View.VISIBLE);
                        adapter.setGalleryList(list);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.tvEmptyGallery.setText("कोणतेही गॅलरी फोटो उपलब्ध नाहीत.\nखालील '+' बटणावर क्लिक करून नवीन फोटो जोडा.");
                    binding.tvEmptyGallery.setVisibility(View.VISIBLE);
                    binding.rvGallery.setVisibility(View.GONE);
                });
    }

    private String uriToBase64(Uri uri) {
        if (uri == null) return "";
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            if (bitmap == null) return "";

            int maxDim = 800;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxDim || height > maxDim) {
                float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                width = Math.round(ratio * width);
                height = Math.round(ratio * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showAddPhotoDialog() {
        pendingImageUri = null;

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_gallery_photo, null);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        View btnSelectPhoto = dialogView.findViewById(R.id.btnSelectPhoto);
        currentDialogPreview = dialogView.findViewById(R.id.ivSelectedPreview);
        currentDialogPreviewLayout = dialogView.findViewById(R.id.layoutPreview);

        btnSelectPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        new AlertDialog.Builder(this)
                .setTitle("गॅलरीमधून फोटो जोडा")
                .setView(dialogView)
                .setPositiveButton("अपलोड व सेव्ह करा", (dialog, which) -> {
                    String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
                    String category = etCategory.getText() != null ? etCategory.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(this, "कृपया फोटोचे नाव/शीर्षक प्रविष्ट करा.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pendingImageUri == null) {
                        Toast.makeText(this, "कृपया फोन गॅलरीमधून फोटो निवडा.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "फोटो अपलोड होत आहे...", Toast.LENGTH_SHORT).show();

                    String docId = db.collection(Constants.COLLECTION_GALLERY).document().getId();
                    String base64Image = uriToBase64(pendingImageUri);

                    repository.uploadGalleryPhoto(docId, pendingImageUri)
                            .addOnSuccessListener(downloadUri -> {
                                String photoUrl = (downloadUri != null && !TextUtils.isEmpty(downloadUri.toString())) ? downloadUri.toString() : base64Image;
                                GalleryItem item = new GalleryItem(docId, title, TextUtils.isEmpty(category) ? "विकास कामे" : category, photoUrl, System.currentTimeMillis());

                                db.collection(Constants.COLLECTION_GALLERY)
                                        .document(docId)
                                        .set(item)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "गॅलरी फोटो यशस्वीरीत्या जोडला!", Toast.LENGTH_SHORT).show();
                                            loadGalleryPhotos();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "सेव्ह अयशस्वी.", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> {
                                GalleryItem item = new GalleryItem(docId, title, TextUtils.isEmpty(category) ? "विकास कामे" : category, base64Image, System.currentTimeMillis());
                                db.collection(Constants.COLLECTION_GALLERY)
                                        .document(docId)
                                        .set(item)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "गॅलरी फोटो यशस्वीरीत्या जोडला!", Toast.LENGTH_SHORT).show();
                                            loadGalleryPhotos();
                                        });
                            });
                })
                .setNegativeButton("रद्द करा", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDeleteClick(GalleryItem item) {
        if (item == null || TextUtils.isEmpty(item.getId())) return;

        new AlertDialog.Builder(this)
                .setTitle("फोटो हटवा")
                .setMessage("तुम्हाला हा फोटो गॅलरीमधून हटवायचा आहे का?")
                .setPositiveButton("हटवा", (dialog, which) -> {
                    db.collection(Constants.COLLECTION_GALLERY)
                            .document(item.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "फोटो हटवला!", Toast.LENGTH_SHORT).show();
                                loadGalleryPhotos();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "हटवणे अयशस्वी.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("रद्द करा", (dialog, which) -> dialog.dismiss())
                .show();
    }
}