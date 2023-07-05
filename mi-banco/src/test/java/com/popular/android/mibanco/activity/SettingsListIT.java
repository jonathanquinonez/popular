package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.SettingsListAdapter;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.object.SettingsItem;
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
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SettingsList.class, MiBancoEnviromentConstants.class, MiBancoConstants.class, R.class,
        Utils.class, FeatureFlags.class, ContextCompat.class,
        App.class, BuildConfig.class, Utils.class})
public class SettingsListIT {

    @Mock
    private AppCompatDelegate appDelegate; //App interface to inject in activity

    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private Resources resources;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SettingsListAdapter adapter;

    @Mock
    private ListView settingsList;

    @Mock
    private Customer cust;

    @InjectMocks
    private SettingsList activity;//Activity

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ContextCompat.class);
        activity = PowerMockito.spy(new SettingsList());
        adapter = PowerMockito.mock(SettingsListAdapter.class);

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.doReturn(appDelegate).when(activity).getDelegate();
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(activity.getApplicationContext()).thenReturn(context);
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);
        when(Utils.isOnsenSupported()).thenReturn(true);
    }

    // @Test
    // public void whenCreateSettingsList_GivenIsSessionActiveTrueAndIsTransactionalTrue_ThenEditEmailInMenu() throws Exception {

    //     SettingsListAdapter listAdapter = new SettingsListAdapter(context, Boolean.TRUE);
    //     PowerMockito.whenNew(SettingsListAdapter.class).withAnyArguments().thenReturn(listAdapter);
    //     when(cust.getIsTransactional()).thenReturn(true);

    //     when(context.getString(R.string.email_change)).thenReturn("Edit e-mail");
    //     SettingsItem editEmail = new SettingsItem("Edit e-mail", WebViewActivity.class);
    //     PowerMockito.whenNew(SettingsItem.class).withAnyArguments().thenReturn(editEmail);
    //     PowerMockito.when(activity.findViewById(R.id.list_settings)).thenReturn(settingsList);
    //     PowerMockito.when(app.getCustomerEntitlements()).thenReturn(null);
    //     Whitebox.invokeMethod(activity, "createSettingsList", Boolean.TRUE, Boolean.FALSE);
    //     verifyPrivate(activity, times(1)).invoke("emailChangeClickListener");
    // }
    @Test
    public void whenCreateSettingsList_GivenIsSessionActiveFalseAndCustomerNull_ThenEditEmailNotInMenu() throws Exception {
        String textTitle = "Settings";

        PowerMockito.whenNew(SettingsListAdapter.class).withAnyArguments().thenReturn(adapter);
        PowerMockito.doReturn(null).when(app).getLoggedInUser();
        when(context.getString(R.string.email_change)).thenReturn("Edit e-mail");
        SettingsItem editEmail = new SettingsItem("Edit e-mail", WebViewActivity.class);
        PowerMockito.when(activity.findViewById(R.id.list_settings)).thenReturn(settingsList);
        PowerMockito.when(app.getCustomerEntitlements()).thenReturn(null);

        Whitebox.invokeMethod(activity, "createSettingsList", Boolean.FALSE, Boolean.FALSE);
        verify(adapter, never()).addItem(editEmail);
    }

}
