package com.example.notinfoapp.p006ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* renamed from: com.example.notinfoapp.ui.slideshow.SlideshowViewModel */
public class SlideshowViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public SlideshowViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("Slideshow");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
