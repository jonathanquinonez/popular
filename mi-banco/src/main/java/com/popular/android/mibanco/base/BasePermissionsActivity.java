package com.popular.android.mibanco.base;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.util.PermissionsManagerUtils;

import java.util.List;

/**
 * Base class for Activities that verifies permissions in user session.
 */
public abstract class BasePermissionsActivity extends BaseActivity {

    private boolean askedForPermission = false;


    @Override
    protected void onResume() {

        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(this);
        if (!askedForPermission && missingPermissions.size() > 0) {
            askedForPermission = true;
            PermissionsManagerUtils.askForPermission(this, missingPermissions, MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);

        } else {
            onPermissionResult(missingPermissions.size() == 0);
        }

        super.onResume();
    }

    public abstract void onPermissionResult(boolean permissionGranted);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS:
                boolean permissionsAccepted = true;
                if(grantResults.length >0){
                    for(int res: grantResults){
                        if(res != PackageManager.PERMISSION_GRANTED){
                            permissionsAccepted = false;
                            break;
                        }
                    }
                }
                this.onPermissionResult(permissionsAccepted);

                break;
            default:
                break;
        }
    }
}
