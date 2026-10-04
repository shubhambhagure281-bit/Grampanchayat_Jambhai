package com.example.grampanchayatjambhai.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.grampanchayatjambhai.models.Notice;
import com.example.grampanchayatjambhai.repositories.AuthRepository;
import com.example.grampanchayatjambhai.repositories.FirestoreRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final FirestoreRepository firestoreRepository;

    private final MutableLiveData<List<Notice>> noticesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    public MainViewModel() {
        this.authRepository = new AuthRepository();
        this.firestoreRepository = new FirestoreRepository();
    }

    public LiveData<List<Notice>> getNoticesLiveData() {
        return noticesLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }
}