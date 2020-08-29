package com.example.notinfoapp.p006ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* renamed from: com.example.notinfoapp.ui.home.HomeViewModel */
public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public HomeViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("Fragment Home");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
