package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.webkit.CookieSyncManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AccountDetails.class, ReportFragment.class, BPAnalytics.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, FeatureFlags.class, ContextCompat.class, AnimationUtils.class})
public class AccountDetailsUT {

    @Mock
    private Customer cust;

    @Mock
    private App app;

    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private AccountDetails accountDetails;

    @Mock
    private Accounts accounts;

    @Mock
    private Intent intent;

    @Mock
    private Window window;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        mockStatic(ContextCompat.class);
        mockStatic(BPAnalytics.class);

        accountDetails = spy(new AccountDetails());
        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);

        PowerMockito.doReturn(appDelegate).when(accountDetails).getDelegate();


    }

    @Ignore
    @Test
    public void whenOnCreate_SetFlagSecure() throws Exception {
        Mockito.doNothing().when(accountDetails).setContentView(R.layout.account_details);
        Mockito.when(accountDetails.getIntent()).thenReturn(intent);
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();

        when(accountDetails.getWindow()).thenReturn(window);
        accountDetails.onCreate(null);
    }
}
