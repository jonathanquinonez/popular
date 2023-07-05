package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.OpenAccountUrl;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

public class OpenAccount extends BaseActivity {
    private Context context;
    private static boolean  DpisrunOnce=false;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            try {
                postTokenOpac();
            } catch (Exception e) {
                Log.e("OpenAccount", e.toString());
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_account);

        if (application == null || application.getAsyncTasksManager() == null) {
            errorReload();
        } else {
            //  **** OJO SOLO PARA SAMSUNG PAY
            String walletRequest = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            //  **** FIN SOLO PARA SAMSUNG PAY


            context = this;
            setContentView(R.layout.activity_open_account);
        }
    }
    private void startViewOA(String opacUrl) throws Exception {

        final Intent openAccountIntent = new Intent(OpenAccount.this, WebViewActivity.class);
        if (MiBancoPreferences.getOpac().get(MiBancoConstants.IS_FOREING_CUSTOMER).equals("true")) {
            opacUrl += "&&foreignCustomer=true";
        }
        openAccountIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, opacUrl);

        String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
        for (int i = 0; i < urlBlacklist.length; ++i) {
            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
        }
        openAccountIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
        openAccountIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        openAccountIntent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        openAccountIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_MBDP, getResources().getStringArray(R.array.mbdp_external_pdf));
        startActivityForResult(openAccountIntent, MiBancoConstants.OPAC_REQUEST_CODE);
    }

    public String postTokenOpac() throws Exception {
        if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            App.getApplicationInstance().getAsyncTasksManager().getOpacUrl(OpenAccount.this, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(OpenAccount.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    if (data != null) {
                        OpenAccountUrl openAccount = (OpenAccountUrl) data;
                        if (openAccount.getError() != null || !openAccount.getError().equals("")) {
                            finish();
                        }
                        try {
                            startViewOA(openAccount.getContent().getUrl());
                        } catch (Exception e) {
                            Log.w("Open Account", e);
                        }
                    } else {
                        finish();
                    }
                }
            }, true);
        }else{
            Toast.makeText(getApplicationContext(),R.string.error_occurred,Toast.LENGTH_LONG).show();
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
             if (requestCode != MiBancoConstants.OPAC_REQUEST_CODE) {
                 try {
                     postTokenOpac();
                 } catch (Exception e) {
                     Log.w("Open Account", e);
                 }
             }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
        BPAnalytics.logEvent(BPAnalytics.EVENT_OPEN_ACCOUNT);
    }
}
