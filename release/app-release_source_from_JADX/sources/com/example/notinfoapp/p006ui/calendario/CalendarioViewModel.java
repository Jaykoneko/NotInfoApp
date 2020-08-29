package com.example.notinfoapp.p006ui.calendario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* renamed from: com.example.notinfoapp.ui.calendario.CalendarioViewModel */
public class CalendarioViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public CalendarioViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("Calendario");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
