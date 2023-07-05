package com.popular.android.mibanco.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.util.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProgressDialog.class, Utils.class,  R.class, BuildConfig.class, App.class
        , MiBancoConstants.class,MiBancoEnviromentConstants.class, FeatureFlags.class, DeveloperActivity.class})
public class DeveloperActivityUT {
    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private SharedPreferences sharedPreferences;//Mi Banco settings

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Mock
    private DeveloperActivity developerActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);

        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);

    }

    @Test
    public void whenOnurlBase_SDG_Is_True() throws Exception {
        when(developerActivity.getResources()).thenReturn(mock(Resources.class));

        when(FeatureFlags.SDG_WIFI()).thenReturn(Boolean.TRUE);
        when(developerActivity, "urlBase").thenReturn(anyString());
    }

    @Test
    public void whenOnurlBase_SDG_Is_False() throws Exception {
        when(developerActivity.getResources()).thenReturn(mock(Resources.class));

        when(FeatureFlags.SDG_WIFI()).thenReturn(Boolean.FALSE);
        when(developerActivity, "urlBase").thenReturn(anyString());
    }

    @Test
    public void whenOnurlBase_Build_QA() throws Exception {
        when(developerActivity.getResources()).thenReturn(mock(Resources.class));
        when(developerActivity, "urlBase").thenReturn("https://cert.bancopopular.com:%s/cibp-web");
    }
}
