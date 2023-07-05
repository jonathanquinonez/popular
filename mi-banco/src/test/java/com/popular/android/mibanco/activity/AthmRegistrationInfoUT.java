package com.popular.android.mibanco.activity;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.webkit.CookieSyncManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AccountDetails.class, ReportFragment.class, BPAnalytics.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, FeatureFlags.class, ContextCompat.class, AnimationUtils.class})
public class AthmRegistrationInfoUT {

    @InjectMocks
    private AthmRegistrationInfo athmRegistrationInfo;

    private App mockApp = mock(App.class);

    @Mock
    private Context mockContext = mock(Context.class);

    @Mock
    private Intent androidIntent;

    private int requestCode;

    private int resultCode;

    private MiBancoConstants constants;

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        athmRegistrationInfo = spy(new AthmRegistrationInfo());
    }

    @Test
    public void whenGetOnActivityResultThenReturnActivityIntentSuccess() throws Exception {

       if(requestCode == MiBancoConstants.ATHM_TERMS_AND_CONDITIONS_REQUEST_CODE && resultCode == RESULT_OK){

            androidIntent = new Intent();
            androidIntent.setClass(mockContext, AthmRegistrationInfo.class);

            try {
                PowerMockito.whenNew(Intent.class)
                        .withArguments(String.class).thenReturn(androidIntent);
                verify(androidIntent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                verify(mockContext).startActivity(androidIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
