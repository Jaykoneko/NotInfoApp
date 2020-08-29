package com.example.notinfoapp.p006ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* renamed from: com.example.notinfoapp.ui.gallery.GalleryViewModel */
public class GalleryViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("Galeria");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
