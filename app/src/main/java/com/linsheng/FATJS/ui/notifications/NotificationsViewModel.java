package com.linsheng.FATJS.ui.notifications;

import static com.linsheng.FATJS.config.GlobalVariableHolder.FATJS_INFO;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(FATJS_INFO);
    }

    public LiveData<String> getText() {
        return mText;
    }
}