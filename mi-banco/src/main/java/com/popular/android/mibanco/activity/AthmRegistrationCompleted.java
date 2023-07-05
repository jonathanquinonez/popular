package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

/**
 * Activity that manages the completion of the ATHM registration process
 */
public class AthmRegistrationCompleted extends AthmActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_registration_completed);

        findViewById(R.id.btnLearnMore).setOnClickListener(this);
        findViewById(R.id.btnOk).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLearnMore:
                Intent webViewIntent = new Intent(this, WebViewActivity.class);
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.athm_website_url));
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                startActivity(webViewIntent);
                break;
            case R.id.btnOk:
                startActivity(new Intent(this, AthmTransfer.class));
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
