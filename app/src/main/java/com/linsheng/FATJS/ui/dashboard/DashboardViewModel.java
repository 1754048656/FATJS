package com.linsheng.FATJS.ui.dashboard;

import static com.linsheng.FATJS.config.GlobalVariableHolder.FATJS_INFO;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(FATJS_INFO);
    }

    public LiveData<String> getText() {
        return mText;
    }
}