package com.programacion.fragments.ui.registrar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RegistrarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is registrar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}