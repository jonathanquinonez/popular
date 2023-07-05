package com.popular.android.mibanco.activity;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.Utils;

/**
 * Activity to be extended to other ATHM related activities and manage onPositiveClick common method
 */
public class AthmActivity extends BaseSessionActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupLanguage(this);
    }

    @Override
    public void onPositiveClick(DialogFragment dialog, int dialogId, Bundle data) {
        switch (dialogId) {
            case MiBancoConstants.MiBancoDialogId.ATHM_DOWNTIME:
            case MiBancoConstants.MiBancoDialogId.ATHM_BLOCKED:
                dialog.dismiss();
                onBackPressed();
                break;
            case MiBancoConstants.MiBancoDialogId.ATHM_ALERT_ERROR:
                dialog.dismiss();
                break;
            default:
                super.onPositiveClick(dialog, dialogId, data);
                break;
        }
    }
}
