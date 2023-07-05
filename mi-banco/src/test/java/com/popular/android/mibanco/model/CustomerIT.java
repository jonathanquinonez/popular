package com.popular.android.mibanco.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
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

import java.util.Arrays;
import java.util.LinkedList;

import static com.popular.android.mibanco.util.Utils.getSecuredSharedPreferences;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FeatureFlags.class, BuildConfig.class, MiBancoEnviromentConstants.class, App.class,
        MiBancoConstants.class, Utils.class})
public class CustomerIT {

    @Mock
    private App app;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private SharedPreferences sharedPreferences;

    @InjectMocks
    private Customer cust;

    private CustomerContent content;
    private CustomerAccount account;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(Utils.class);
        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);

        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);

        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);
        content = new CustomerContent();
        cust = spy(new Customer());
        cust.content =  content;
        account = new CustomerAccount();
    }

    @Test
    public void whenSortAcc_GivenHideMLATransactionsTrue() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("MLA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        cust.getMortgage();
        PowerMockito.verifyPrivate(cust).invoke("sortAcc");
    }
}