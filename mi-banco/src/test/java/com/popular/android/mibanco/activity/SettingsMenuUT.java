package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import android.webkit.CookieSyncManager;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.SettingsListAdapter;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.object.SettingsItem;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Accounts.class, ReportFragment.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, FeatureFlags.class, ContextCompat.class, User.class})
public class SettingsMenuUT {

    public static final String DUMMY_MANAGE_USERS = "Dummy Manage Users";
    public static final String DUMMY_LANGUAGE_TEXT = "Dummy Language Text";
    public static final String DUMMY_EMAIL_STRING = "Dummy Email Change";
    public static final String DUMMY_TITLE_STRING = "Dummy Title";

    @Mock
    private Customer cust;

    @Mock
    private User user;

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
    private ListView listView;

    @InjectMocks
    private SettingsList settingsListActivity;

    @Mock
    CustomerEntitlements customerEntitlements;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(CookieSyncManager.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ContextCompat.class);
        //PowerMockito.mockStatic(ATHMUtils.class);
        user = mock(User.class);

        customerEntitlements = mock(CustomerEntitlements.class);
        settingsListActivity = PowerMockito.spy(new SettingsList());

        //Need this to mock static MiBancoEnvironmentConstants
        //when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);

        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);

        PowerMockito.doReturn(appDelegate).when(settingsListActivity).getDelegate();

        cust = Mockito.mock(Customer.class);
        listView = Mockito.mock(ListView.class);

        //Resources
        when(settingsListActivity.getString(R.string.email_change)).thenReturn(DUMMY_EMAIL_STRING);
        when(settingsListActivity.getString(R.string.title_settings)).thenReturn(DUMMY_TITLE_STRING);
        when(settingsListActivity.getString((R.string.manage_users))).thenReturn(DUMMY_MANAGE_USERS);
        when(settingsListActivity.getString((R.string.language))).thenReturn(DUMMY_LANGUAGE_TEXT);
    }

    @Ignore
    @Test
    public void whenOnCreate_ok() {
        when(app.getCurrentUser()).thenReturn(user);
        when(app.getLoggedInUser()).thenReturn(cust);
        when(app.getCustomerEntitlements()).thenReturn(customerEntitlements);
        //when(customerEntitlements.hasAthm()).thenReturn(true);
        when(settingsListActivity.getApplicationContext()).thenReturn(context);

        //when(cust.getAthmSso()).thenReturn(true);
        doNothing().when(settingsListActivity).setContentView(R.layout.settings_list);
        PowerMockito.when(settingsListActivity.findViewById(R.id.list_settings)).thenReturn(listView);

        //Para capturar la creacion del adapter que quedó del lado de la implementación
        ArgumentCaptor<SettingsListAdapter> argumentCaptor = ArgumentCaptor.forClass(SettingsListAdapter.class);
        settingsListActivity.setContentView(listView);
        settingsListActivity.onCreate(mock(Bundle.class));
        Mockito.verify(listView).setAdapter(argumentCaptor.capture());
        SettingsListAdapter captured = argumentCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getItem(0));
        assertEquals(((SettingsItem) captured.getItem(0)).getTitle(), DUMMY_TITLE_STRING);
        assertEquals(((SettingsItem) captured.getItem(1)).getTitle(), DUMMY_MANAGE_USERS);
        assertEquals(((SettingsItem) captured.getItem(2)).getTitle(), DUMMY_LANGUAGE_TEXT);
    }

    @Ignore
    @Test
    public void whenOnCreate_SettingsMenu_Displayed() {
        when(app.getLoggedInUser()).thenReturn(cust);
        when(app.getCustomerEntitlements()).thenReturn(customerEntitlements);
        when(settingsListActivity.getApplicationContext()).thenReturn(context);

        doNothing().when(settingsListActivity).setContentView(R.layout.settings_list);
        PowerMockito.when(settingsListActivity.findViewById(R.id.list_settings)).thenReturn(listView);

        //Para capturar la creacion del adapter que quedó del lado de la implementación
        ArgumentCaptor<SettingsListAdapter> argumentCaptor = ArgumentCaptor.forClass(SettingsListAdapter.class);
        settingsListActivity.setContentView(listView);
        settingsListActivity.onCreate(mock(Bundle.class));
        Mockito.verify(listView).setAdapter(argumentCaptor.capture());
        SettingsListAdapter captured = argumentCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getItem(0));
        assertEquals(((SettingsItem) captured.getItem(0)).getTitle(), DUMMY_TITLE_STRING);
        assertEquals(((SettingsItem) captured.getItem(1)).getTitle(), DUMMY_MANAGE_USERS);
        assertEquals(((SettingsItem) captured.getItem(2)).getTitle(), DUMMY_LANGUAGE_TEXT);
    }

    @Ignore
    @Test
    public void whenOnCreate_SettingsEmailMenu_Displayed() {

        when(app.getCurrentUser()).thenReturn(user);
        when(app.getLoggedInUser()).thenReturn(cust);
        when(App.getApplicationInstance().getLoggedInUser().getIsTransactional()).thenReturn(Boolean.TRUE);
        when(app.getCustomerEntitlements()).thenReturn(customerEntitlements);
        //when(customerEntitlements.hasAthm()).thenReturn(true);
        when(settingsListActivity.getApplicationContext()).thenReturn(context);
        when(Utils.isOnsenSupported()).thenReturn(true);

        //when(cust.getAthmSso()).thenReturn(true);
        doNothing().when(settingsListActivity).setContentView(R.layout.settings_list);
        PowerMockito.when(settingsListActivity.findViewById(R.id.list_settings)).thenReturn(listView);

        //Para capturar la creacion del adapter que quedó del lado de la implementación
        ArgumentCaptor<SettingsListAdapter> argumentCaptor = ArgumentCaptor.forClass(SettingsListAdapter.class);
        settingsListActivity.setContentView(listView);
        settingsListActivity.onCreate(mock(Bundle.class));
        Mockito.verify(listView).setAdapter(argumentCaptor.capture());
        SettingsListAdapter captured = argumentCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getItem(0));
        assertEquals(((SettingsItem) captured.getItem(0)).getTitle(), DUMMY_TITLE_STRING);
        assertEquals(((SettingsItem) captured.getItem(1)).getTitle(), DUMMY_EMAIL_STRING);
        assertEquals(((SettingsItem) captured.getItem(2)).getTitle(), DUMMY_MANAGE_USERS);
        assertEquals(((SettingsItem) captured.getItem(3)).getTitle(), DUMMY_LANGUAGE_TEXT);
    }

    @Ignore
    @Test
    public void whenOnCreate_SettingsEmailMenu_NotDisplayed() {

        when(app.getCurrentUser()).thenReturn(user);
        when(app.getLoggedInUser()).thenReturn(cust);
        when(app.getCustomerEntitlements()).thenReturn(customerEntitlements);
        when(settingsListActivity.getApplicationContext()).thenReturn(context);
        when(Utils.isOnsenSupported()).thenReturn(false);

        doNothing().when(settingsListActivity).setContentView(R.layout.settings_list);
        PowerMockito.when(settingsListActivity.findViewById(R.id.list_settings)).thenReturn(listView);

        //Para capturar la creacion del adapter que quedó del lado de la implementación
        ArgumentCaptor<SettingsListAdapter> argumentCaptor = ArgumentCaptor.forClass(SettingsListAdapter.class);
        settingsListActivity.setContentView(listView);
        settingsListActivity.onCreate(mock(Bundle.class));
        Mockito.verify(listView).setAdapter(argumentCaptor.capture());
        SettingsListAdapter captured = argumentCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getItem(0));
        assertEquals(((SettingsItem) captured.getItem(0)).getTitle(), DUMMY_TITLE_STRING);
        assertEquals(((SettingsItem) captured.getItem(1)).getTitle(), DUMMY_MANAGE_USERS);
        assertEquals(((SettingsItem) captured.getItem(2)).getTitle(), DUMMY_LANGUAGE_TEXT);
    }
}