package com.popular.android.mibanco.base;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.object.SidebarItem;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;


@RunWith(PowerMockRunner.class)
@PrepareForTest({R.class, App.class, Utils.class})
public class BaseSessionActivityUT {

    @Mock
    private App app;
    @Mock
    private Context context;
    @Mock
    private Resources resources;
    @Mock
    private List<SidebarItem> menuItems;

    @InjectMocks
    private Accounts baseSessionActivity;

    @Before
    public void setUp() throws IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        baseSessionActivity = Mockito.spy(baseSessionActivity);
    }


    @Test
    public void whenOpenRequestDocumentsWebView_thenReturnNotNull() {
        Intent intent = baseSessionActivity.openRequestDocumentsWebView();
        assertNotNull(intent);
    }

    @Test
    public void whenLaunchOption_givenOptionRequestDocuments_thenReturnShowRequestDocuments() throws Exception {

        Field showRequestDocuments = BaseSessionActivity.class.getDeclaredField("showRequestDocuments");
        showRequestDocuments.setAccessible(true);
        showRequestDocuments.set(baseSessionActivity, 0);

        List<SidebarItem> menuItems = new ArrayList<>();
        SidebarItem sidebarItem = new SidebarItem("", 0, null);
        menuItems.add(sidebarItem);
        Field menuItemsList = BaseSessionActivity.class.getDeclaredField("menuItems");
        menuItemsList.setAccessible(true);
        menuItemsList.set(baseSessionActivity, menuItems);

        boolean launchO = Whitebox.invokeMethod(baseSessionActivity, "launchOption", 0);
        assertTrue(launchO);
        Mockito.verify(baseSessionActivity, Mockito.times(1)).openRequestDocumentsWebView();
    }

    @Test
    public void whenLaunchOption_givenOtherOption_thenReturnNoShowRequestDocuments() throws Exception {
        List<SidebarItem> menuItems = new ArrayList<>();
        SidebarItem sidebarItem = new SidebarItem("", 0, null);
        menuItems.add(sidebarItem);
        Field menuItemsList = BaseSessionActivity.class.getDeclaredField("menuItems");
        menuItemsList.setAccessible(true);
        menuItemsList.set(baseSessionActivity, menuItems);

        boolean launchO = Whitebox.invokeMethod(baseSessionActivity, "launchOption", 0);
        assertTrue(launchO);
        Mockito.verify(baseSessionActivity, Mockito.times(0)).openRequestDocumentsWebView();
    }
    @Test
    public void whenCallAddDefaultMenu_GivenIntentClassIntAndStringNotNull_ThenReturnIntentNotNull() throws Exception
    {
        setInternalState(baseSessionActivity, "menuItems", menuItems);
        when(app.getLanguage()).thenReturn("es");
        when(baseSessionActivity.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(baseSessionActivity.getResources()).thenReturn(resources);
        when(baseSessionActivity.getPackageName()).thenReturn("name");
        Intent intent = Whitebox.invokeMethod(baseSessionActivity,"addDefaultMenu",Intent.class,0,"drawableName");
        assertNotNull(intent);
    }


}