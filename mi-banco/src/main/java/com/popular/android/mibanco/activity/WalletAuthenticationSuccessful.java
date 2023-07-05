package com.popular.android.mibanco.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.BPAnalytics;

/**
 * Activity that manages the Wallet authentication success
 */
public class WalletAuthenticationSuccessful extends BaseSessionActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_authentication_successful);

        TextView detailMessageTextView = (TextView) findViewById(R.id.txtViewSuccessDetails);

        if (application.getCallingWallet().equals(MiBancoConstants.SAMSUNGPAY_WALLET)) {
            detailMessageTextView.setText(getResources().getString(R.string.wallet_authentication_details_samsung_pay));
        }else if (application.getCallingWallet().equals( MiBancoConstants.ANDROIDPAY_WALLET)) {
            detailMessageTextView.setText(getResources().getString(R.string.wallet_authentication_details_android_pay));
        }

        findViewById(R.id.btnLearnMore).setOnClickListener(this);
        findViewById(R.id.btnOk).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLearnMore:
                Intent webViewIntent = new Intent(this, WebViewActivity.class);
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.wallet_website_url));
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                startActivity(webViewIntent);
                break;
            case R.id.btnOk:
                setResult(Activity.RESULT_OK);
                BPAnalytics.logEvent(BPAnalytics.EVENT_WALLET_AUTHENTICATION_SUCCESS, "walletType", application.getCallingWallet());
                finishAfterTransition();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
