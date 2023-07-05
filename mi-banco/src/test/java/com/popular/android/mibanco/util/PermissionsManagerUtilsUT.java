package com.popular.android.mibanco.util;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import androidx.appcompat.app.AlertDialog;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.GlobalStatus;
import com.rsa.mobilesdk.sdk.MobileAPI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.powermock.api.mockito.PowerMockito.mock;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProgressDialog.class, RSACollectUtils.class,  R.class, BuildConfig.class, App.class
        , MiBancoConstants.class, MobileAPI.class, PermissionsManagerUtils.class, AlertDialog.Builder.class, Utils.class})
public class PermissionsManagerUtilsUT {


    @Mock
    final AlertDialog.Builder alertDialogBuilder = mock(AlertDialog.Builder.class);

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private SharedPreferences sharedPreferences;//Mi Banco settings

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(Utils.class);

        when(context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(context.getResources()).thenReturn(resources);

    }

    @Test
    public void whenCallingIsFunctionalityAllowed_And_LocationPermissions_Is_Enabled_Then_Return_True(){

        final int[] results = {PackageManager.PERMISSION_GRANTED,PackageManager.PERMISSION_GRANTED,PackageManager.PERMISSION_GRANTED};
        final int message = 14;
        final String[] permisions = {"canhack","canstolemyinfo", Manifest.permission.ACCESS_FINE_LOCATION};
        DialogInterface.OnClickListener permissionsOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        };

        boolean accessLocationPermissions = PermissionsManagerUtils.isFunctionalityAllowed(context, permisions, results, message, permissionsOnClick);
        assertTrue(accessLocationPermissions);

    }

    @Test
    public void whenCallingIsFunctionalityAllowed_And_LocationPermissions_Is_Disabled_Then_Return_False() throws  Exception{

        final int[] results = {PackageManager.PERMISSION_DENIED,PackageManager.PERMISSION_DENIED,PackageManager.PERMISSION_DENIED};
        final int message = 14;
        final String[] permisions = {"canhack","canstolemyinfo", Manifest.permission.ACCESS_FINE_LOCATION};
        DialogInterface.OnClickListener permissionsOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        };

        when(resources.getString(Mockito.anyInt())).thenReturn("Yes");

        whenNew(AlertDialog.Builder.class).withAnyArguments().thenReturn(alertDialogBuilder);

        boolean accessLocationPermissions = PermissionsManagerUtils.isFunctionalityAllowed(context, permisions, results, message, permissionsOnClick);
        assertFalse(accessLocationPermissions);

    }


}
