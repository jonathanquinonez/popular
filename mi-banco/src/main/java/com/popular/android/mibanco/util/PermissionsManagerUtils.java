package com.popular.android.mibanco.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.AthmContacts;
import com.popular.android.mibanco.activity.DepositCheck;
import com.popular.android.mibanco.activity.DepositCheckCamera;
import com.popular.android.mibanco.activity.EasyCashHistoryActivity;
import com.popular.android.mibanco.activity.EasyCashHistoryReceipt;
import com.popular.android.mibanco.activity.EasyCashNonCustHistoryActivity;
import com.popular.android.mibanco.activity.EasyCashRedeem;
import com.popular.android.mibanco.activity.EasyCashStaging;
import com.popular.android.mibanco.activity.EasyCashStagingReceipt;
import com.popular.android.mibanco.activity.EnterUsername;
import com.popular.android.mibanco.activity.ErrorView;
import com.popular.android.mibanco.activity.SettingsList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//import com.popular.android.mibanco.activity.LocatorFacilityDetails;
//import com.popular.android.mibanco.activity.LocatorFacilityList;
//import com.popular.android.mibanco.activity.LocatorMap;
//import com.popular.android.mibanco.activity.LocatorTabs;

/**
 * Utilities class to manage common permissions methods
 * Created by ET55498 on 9/8/16.
 */
public class PermissionsManagerUtils {


    public static final String CLASS_NAME = "PermissionsManagerUtils";


    /**
     * Method that verifies if the class needs a permission
     * @param context The Activity Context
     * @return A list with the missing permissions. Empty list if no permissions need to be asked for
     */
    public static List<String> missingPermissions(Context context)
    {
        if(Build.VERSION.SDK_INT < MiBancoConstants.MAX_NO_RUNTIME_PERMISSION_VERSION){
            Log.d(CLASS_NAME,"No need for asking for permission");
            return new LinkedList<>();
        }

        List<String> manifestPermissions = null;
        if(context.getClass() == IntroScreen.class){
            Log.d(CLASS_NAME,"Intro screen permission verification");
            manifestPermissions = Arrays.asList(MiBancoConstants.INTRO_PERMISSIONS);

//        }else if(context.getClass() == LocatorTabs.class
//                || context.getClass() == LocatorFacilityList.class
//                || context.getClass() == LocatorFacilityDetails.class
//                || context.getClass() == LocatorMap.class){
//            manifestPermissions = Arrays.asList(MiBancoConstants.LOCATOR_PERMISSIONS);

        }else if(context.getClass() == AthmContacts.class
                || context.getClass() == EasyCashHistoryReceipt.class
                || context.getClass() == EasyCashHistoryActivity.class
                || context.getClass() == EasyCashNonCustHistoryActivity.class
                || context.getClass() == EasyCashStagingReceipt.class
                || context.getClass() == EasyCashStaging.class) {
            manifestPermissions = Arrays.asList(MiBancoConstants.CONTACTS_PERMISSIONS);

        }else if(context.getClass() == DepositCheck.class
                || context.getClass() == DepositCheckCamera.class
                || context.getClass() == EasyCashRedeem.class
                || (context.getClass() == EnterUsername.class && FeatureFlags.CUSTOM_LOGIN_IMAGE())){
            manifestPermissions = Arrays.asList(MiBancoConstants.CAMERA_PERMISSIONS);
        }else if(context.getClass() == SettingsList.class){
            manifestPermissions = Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else if(context.getClass() == ErrorView.class){
            manifestPermissions = Arrays.asList(Manifest.permission.CALL_PHONE);
        }

        return missingPermissions(context,manifestPermissions);
    }


    public static List<String> missingPermissions(Context context, List<String> neededPermissions) {
        if (neededPermissions != null) {
            List<String> permissionsNotAssigned = new LinkedList<>();
            for (String permission : neededPermissions) {
                Log.d(CLASS_NAME, "Verify if user has permission " + permission);
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(CLASS_NAME, "User does not has permission");
                    permissionsNotAssigned.add(permission);
                }
            }
            return permissionsNotAssigned;
        }
        return new LinkedList<>();
    }

    /**
     * Method that asks for unasigned permissions
     * @param context The Activity Context
     * @param unassignedPermissions The permissions that needs to be asked for
     * @param requestCode The request code when the control returns to the activity
     * @return True if permissions need to be asked
     */
    public static boolean askForPermission(Context context, List<String> unassignedPermissions, int requestCode) {

        if (unassignedPermissions.size() != 0) {
            String[] permissionsArr = new String[unassignedPermissions.size()];
            permissionsArr = unassignedPermissions.toArray(permissionsArr);
            /**
             * TODO: LALR android.content.ActivityNotFoundException PermissionsManagerUtils.java line 118 in com.popular.android.mibanco.util.PermissionsManagerUtils.askForPermission()
             * 	No Activity found to handle Intent { act=android.content.pm.action.REQUEST_PERMISSIONS pkg=com.google.android.packageinstaller (has extras) }
             *
             * De donde?
             * IntroScreen.java line 71 in com.popular.android.mibanco.IntroScreen.onCreate()
             */
            ActivityCompat.requestPermissions(((Activity) context), permissionsArr, requestCode);
            return false;
        } else {
            Log.d(CLASS_NAME,"User has all permissions. Returns true");
            return true;
        }
    }

    public static void displayRequiredPermissionsDialog(Context context, int message, DialogInterface.OnClickListener permissionsOnClick) {
        AlertDialogParameters params = new AlertDialogParameters(context, message ,permissionsOnClick);
        params.setPositiveButtonText(context.getResources().getString(R.string.yes).toUpperCase());
        params.setNegativeButtonText(context.getResources().getString(R.string.no).toUpperCase());
        Utils.showAlertDialog(params);
    }

    public static boolean isFunctionalityAllowed(Context mContext, String[] permissions, int[] grantResults, int message, DialogInterface.OnClickListener permissionsOnClick) {
        boolean permissionsAccepted = true;
        int index = 0;
        if (grantResults.length > 0) {
            for (int res: grantResults) {
                if (!permissions[index].equals(Manifest.permission.ACCESS_FINE_LOCATION) && res == PackageManager.PERMISSION_DENIED) {
                    permissionsAccepted = false;
                    break;
                }
                index++;
            }
        }
        if (!permissionsAccepted && permissionsOnClick != null) {
            displayRequiredPermissionsDialog(mContext, message, permissionsOnClick);
            return false;
        }
        return permissionsAccepted;
    }
}
