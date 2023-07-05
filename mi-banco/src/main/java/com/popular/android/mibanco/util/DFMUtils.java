package com.popular.android.mibanco.util;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.activity.AcceptTermsActivity;
import com.popular.android.mibanco.activity.DepositCheck;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.AcceptedTermsInRDC;

public class DFMUtils {

    public static void verifyAcceptedTermsAction(final AppCompatActivity appCompatActivity) {
        App app = App.getApplicationInstance();
        if (app != null) {
            if(!App.getApplicationInstance().getRdcClientAcceptedTerms()) {
                if (app.getAsyncTasksManager() != null) {
                    app.getAsyncTasksManager().acceptedTermsInRDC(appCompatActivity, new ResponderListener() {
                        @Override
                        public void sessionHasExpired() {
                            App.getApplicationInstance().reLogin(appCompatActivity);
                        }

                        @Override
                        public void responder(String responderName, Object data) {
                            AcceptedTermsInRDC acceptedTermsInRDC = (AcceptedTermsInRDC) data;
                            if (!acceptedTermsInRDC.getAcceptTerms().toLowerCase().equals("true")) {
                                AcceptTermsActivity.launch(appCompatActivity, acceptedTermsInRDC.getAmountChargeTerms());
                            } else {
                                App.getApplicationInstance().setRdcClientAcceptedTerms(true);
                                Intent intent = new Intent(appCompatActivity, DepositCheck.class);
                                appCompatActivity.startActivity(intent);
                            }
                        }
                    });
                }
            } else {
                Intent intent = new Intent(appCompatActivity, DepositCheck.class);
                appCompatActivity.startActivity(intent);
            }
        }
    }

    //Limits per segment (for commercial segments)
    public static boolean isLimitsPerSegmentEnabled() {
        return App.getApplicationInstance().getLoggedInUser().isLimitsPerSegmentEnabled();
    }
}
