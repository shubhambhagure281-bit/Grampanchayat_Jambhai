package com.example.grampanchayatjambhai.repositories;

import android.net.Uri;

import com.example.grampanchayatjambhai.models.Complaint;
import com.example.grampanchayatjambhai.models.Member;
import com.example.grampanchayatjambhai.models.Notice;
import com.example.grampanchayatjambhai.models.NotificationModel;
import com.example.grampanchayatjambhai.models.PanchayatInfo;
import com.example.grampanchayatjambhai.models.Scheme;
import com.example.grampanchayatjambhai.models.User;
import com.example.grampanchayatjambhai.utils.Constants;
import com.example.grampanchayatjambhai.utils.FirebaseUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

public class FirestoreRepository {

    private final FirebaseFirestore db;

    public FirestoreRepository() {
        this.db = FirebaseUtil.getFirestore();
    }

    public Task<Void> saveUserProfile(User user) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user);
    }

    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get();
    }

    public Task<QuerySnapshot> getAllUsers() {
        return db.collection(Constants.COLLECTION_USERS)
                .get();
    }

    public Task<DocumentSnapshot> getPanchayatInfo() {
        return db.collection(Constants.COLLECTION_PANCHAYAT)
                .document("info")
                .get();
    }

    public Task<Void> savePanchayatInfo(PanchayatInfo info) {
        return db.collection(Constants.COLLECTION_PANCHAYAT)
                .document("info")
                .set(info);
    }

    public Task<QuerySnapshot> getPanchayatMembers() {
        return db.collection(Constants.COLLECTION_MEMBERS)
                .get();
    }

    public Task<Void> saveMember(Member member) {
        return db.collection(Constants.COLLECTION_MEMBERS)
                .document(member.getId())
                .set(member);
    }

    public Task<Void> deleteMember(String memberId) {
        return db.collection(Constants.COLLECTION_MEMBERS)
                .document(memberId)
                .delete();
    }

    public Task<QuerySnapshot> getNotices() {
        return db.collection(Constants.COLLECTION_NOTICES)
                .get();
    }

    public Task<Void> saveNotice(Notice notice) {
        return db.collection(Constants.COLLECTION_NOTICES)
                .document(notice.getId())
                .set(notice);
    }

    public Task<Void> deleteNotice(String noticeId) {
        return db.collection(Constants.COLLECTION_NOTICES)
                .document(noticeId)
                .delete();
    }

    public Task<QuerySnapshot> getSchemes() {
        return db.collection(Constants.COLLECTION_SCHEMES)
                .get();
    }

    public Task<Void> saveScheme(Scheme scheme) {
        return db.collection(Constants.COLLECTION_SCHEMES)
                .document(scheme.getId())
                .set(scheme);
    }

    public Task<Void> deleteScheme(String schemeId) {
        return db.collection(Constants.COLLECTION_SCHEMES)
                .document(schemeId)
                .delete();
    }

    public Task<Void> submitComplaintWithId(Complaint complaint) {
        return db.collection(Constants.COLLECTION_COMPLAINTS)
                .document(complaint.getComplaintId())
                .set(complaint);
    }

    public Task<DocumentReference> submitComplaint(Complaint complaint) {
        return db.collection(Constants.COLLECTION_COMPLAINTS)
                .add(complaint);
    }

    public Task<QuerySnapshot> getAllComplaints() {
        return db.collection(Constants.COLLECTION_COMPLAINTS)
                .get();
    }

    public Task<QuerySnapshot> getUserComplaints(String userId) {
        return db.collection(Constants.COLLECTION_COMPLAINTS)
                .whereEqualTo("userId", userId)
                .get();
    }

    public Task<DocumentSnapshot> getComplaintById(String complaintId) {
        return db.collection(Constants.COLLECTION_COMPLAINTS)
                .document(complaintId)
                .get();
    }

    public Task<Void> updateComplaintStatus(String complaintId, String newStatus) {
        return db.collection(Constants.COLLECTION_COMPLAINTS)
                .document(complaintId)
                .update(
                        "status", newStatus,
                        "updatedAt", System.currentTimeMillis()
                );
    }

    public Task<Void> logNotification(NotificationModel notification) {
        return db.collection("notifications")
                .document(notification.getNotificationId())
                .set(notification);
    }

    public Task<QuerySnapshot> getUserNotifications(String userId) {
        return db.collection("notifications")
                .whereEqualTo("userId", userId)
                .get();
    }

    public Task<Uri> uploadComplaintPhoto(String userId, String complaintId, Uri imageUri) {
        StorageReference storageRef = FirebaseUtil.getStorage()
                .getReference()
                .child("complaint_images/" + userId + "/" + complaintId + ".jpg");

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                });
    }

    public Task<Uri> uploadNoticeImage(String noticeId, Uri imageUri) {
        StorageReference storageRef = FirebaseUtil.getStorage()
                .getReference()
                .child("notice_images/" + noticeId + ".jpg");

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                });
    }

    public Task<Uri> uploadGalleryPhoto(String galleryId, Uri imageUri) {
        StorageReference storageRef = FirebaseUtil.getStorage()
                .getReference()
                .child("gallery_photos/" + galleryId + ".jpg");

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                });
    }

    public Task<Uri> uploadSchemeImage(String schemeId, Uri imageUri) {
        StorageReference storageRef = FirebaseUtil.getStorage()
                .getReference()
                .child("scheme_images/" + schemeId + ".jpg");

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                });
    }

    public Task<Uri> uploadMemberPhoto(String memberId, Uri imageUri) {
        StorageReference storageRef = FirebaseUtil.getStorage()
                .getReference()
                .child("member_images/" + memberId + ".jpg");

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                });
    }

    public Task<Uri> uploadProfilePhoto(String userId, Uri imageUri) {
        StorageReference storageRef = FirebaseUtil.getStorage()
                .getReference()
                .child("profile_images/" + userId + ".jpg");

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                });
    }
}