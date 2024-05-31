package com.linsheng.FATJS.ui.home;

import static com.linsheng.FATJS.config.GlobalVariableHolder.FATJS_INFO;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(FATJS_INFO);
    }

    public LiveData<String> getText() {
        return mText;
    }
}