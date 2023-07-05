package com.popular.android.mibanco.viewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.popular.android.mibanco.App;

public class SharedViewModelFactory implements ViewModelProvider.Factory {
    private final App application;
    private final String requestedUrl;
    private final String accountHome;

    private Context mContext;

    public SharedViewModelFactory(Context mContext, App application, String requestedUrl, String accountHome) {
        this.application = application;
        this.requestedUrl = requestedUrl;
        this.accountHome = accountHome;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SharedViewModel.class)) {
            return (T) new SharedViewModel(mContext, application, requestedUrl, accountHome);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}