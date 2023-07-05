package com.popular.android.mibanco.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.animation.AnimationUtils;
import android.webkit.CookieSyncManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AccountDetails.class, ReportFragment.class, BPAnalytics.class,
        Utils.class, App.class, ContextCompat.class, R.class, EnterPassword.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, FeatureFlags.class, ContextCompat.class, AnimationUtils.class})

public class EnterPasswordUT {

    @Mock
    private App application;

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private Resources resources;

    @Mock
    private Intent data;

    @Mock
    private ResponderListener responderListener;

    @Captor
    private ArgumentCaptor<Intent> intentCapture;
    @Captor
    private ArgumentCaptor<Integer> miBancoConstantsCapture;

    @InjectMocks
    private EnterPassword activity;


    @Before
    public void setup() throws Exception {

        activity = PowerMockito.spy(new EnterPassword());

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(ContextCompat.class);

        PowerMockito.when(App.getApplicationInstance()).thenReturn(application);
        PowerMockito.when(application.getBaseContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(application.getResources()).thenReturn(resources);
        when(activity.getResources()).thenReturn(resources);
    }

    @Test
    public void whenGetSendPassword() throws Exception {

        data = new Intent();
        data.setClass(context, EnterPassword.class);
        try {
            PowerMockito.whenNew(Intent.class)
                    .withArguments(String.class).thenReturn(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void whenOpenInterruptionPage_givenDefaultParams_thenReturnIntentActivity() {
        Mockito.when(Utils.getAbsoluteUrl(any(String.class))).thenReturn("");
        Mockito.when(application.getLanguage()).thenReturn("");
        Mockito.when(resources.getStringArray(any(Integer.class))).thenReturn(new String[0]);
        doNothing().when(activity).startActivityForResult(intentCapture.capture(), miBancoConstantsCapture.capture());

        activity.openInterruptionPage();
        assertNotNull(intentCapture.getValue());
        assertEquals(MiBancoConstants.INTERRUPTION_REQUEST_CODE, (int) miBancoConstantsCapture.getValue());
    }

    @Test
    public void whenOnActivityResult_givenINTERRUPTION_REQUEST_CODE_thenReturnCallnextActivityOnSuccessMethod () throws Exception {
        PowerMockito.doNothing().when(activity, "nextActivityOnSuccess");

        activity.onActivityResult(MiBancoConstants.INTERRUPTION_REQUEST_CODE, AppCompatActivity.RESULT_OK, data);

        verifyPrivate(activity).invoke("nextActivityOnSuccess");
        verifyPrivate(activity).invoke("finish");
    }
}
