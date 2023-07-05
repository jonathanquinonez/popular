package com.popular.android.mibanco.viewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.NotificationCenter;
import com.popular.android.mibanco.task.CarouselTasks;
import com.popular.android.mibanco.ws.response.BannerResponse;
import com.popular.android.mibanco.task.PremiaTasks;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<BannerResponse> bannerResponse = new MutableLiveData<>();
    private final MutableLiveData<NotificationCenter> notificationCenterData = new MutableLiveData<>();
    private final MutableLiveData<String> premiaCatalogRedirectURL = new MutableLiveData<>();
    private final Executor executor = Executors.newSingleThreadExecutor();


    private final App application;
    private final String requestedUrl;
    private String accountHome;

    private Context mContext;

    public SharedViewModel(Context mContext, App application, String requestedUrl, String accountHome) {
        this.application = application;
        this.requestedUrl = requestedUrl;
        this.accountHome = accountHome;
        this.mContext = mContext;
        loadDataImageBanner();
    }

    public void setAccountHome(String accountHome) {
        this.accountHome = accountHome;
    }

    public MutableLiveData<BannerResponse> getBannerResponse() {
        return bannerResponse;
    }
    public MutableLiveData<NotificationCenter> getNotificationCenterData() { return notificationCenterData;}
    public MutableLiveData<String> getPremiaCatalogRedirectURL() { return premiaCatalogRedirectURL;}
    public void loadDataImageBanner() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CarouselTasks.CarouselInfo(mContext, new ResponderListener() {
                    @Override
                    public void responder (String responderName,final Object data){
                        BannerResponse response = new BannerResponse();
                        if (data instanceof BannerResponse) {
                            response = (BannerResponse) data;
                            bannerResponse.postValue(response);
                        }
                    }
                    @Override
                    public void sessionHasExpired () {
                        application.reLogin(application.getActivityContext());
                    }
                },requestedUrl,accountHome,application.getLanguage());
            }
        });
    }
    public void redirectToPremiaCatalog(CustomerAccount account) {
        PremiaTasks.premiaCatalogRedirect(mContext, account.getFrontEndId(), new ResponderListener() {
            @Override
            public void responder(String responderName, final Object data) {
                if (data == null) {
                    return;
                } else if (data instanceof String) {
                    premiaCatalogRedirectURL.postValue((String) data);
                }
            }
            @Override
            public void sessionHasExpired() {
                application.reLogin(application.getActivityContext());
            }
        });
    }
}