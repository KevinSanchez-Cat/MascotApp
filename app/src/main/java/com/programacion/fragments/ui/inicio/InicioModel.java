package com.programacion.fragments.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InicioModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InicioModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is inicio fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}