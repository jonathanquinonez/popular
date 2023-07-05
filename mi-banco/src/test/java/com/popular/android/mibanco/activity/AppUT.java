package com.popular.android.mibanco.activity;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

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
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Test;
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
public class AppUT {

    private App app;

    @Mock
    private App mockApp = mock(App.class);

    @Mock
    private Context mockContext = mock(Context.class);

    @Mock
    private Intent androidIntent;


    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        app = spy(new App());
    }

    @Test
    public void whenGetStartOverThenReturnActivityIntentSuccess() throws Exception {

        androidIntent = new Intent();
        androidIntent.setClass(mockContext, App.class);

        try {
            PowerMockito.whenNew(Intent.class)
                    .withArguments(String.class).thenReturn(androidIntent);
           
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
