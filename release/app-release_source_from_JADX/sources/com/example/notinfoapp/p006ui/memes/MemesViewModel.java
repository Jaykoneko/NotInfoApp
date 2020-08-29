package com.example.notinfoapp.p006ui.memes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* renamed from: com.example.notinfoapp.ui.memes.MemesViewModel */
public class MemesViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MemesViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("Momazos");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
