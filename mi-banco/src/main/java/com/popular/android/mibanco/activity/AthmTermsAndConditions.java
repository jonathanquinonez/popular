package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.Utils;

/**
 * Activity that manages the terms and conditions acceptance in the ATHM enrollment process
 */
public class AthmTermsAndConditions extends AthmActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_terms_and_conditions);

        findViewById(R.id.btnAgree).setOnClickListener(this);
        findViewById(R.id.btnDisagree).setOnClickListener(this);

        WebView webViewTerms = (WebView) findViewById(R.id.webViewTerms);
        String requestedUrl = getString(R.string.athm_terms_and_conditions_url);
        if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
            webViewTerms.loadUrl(requestedUrl);
        }else{
            webViewTerms.goBack();
        }
        //webViewTerms.loadUrl(getString(R.string.athm_terms_and_conditions_url));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAgree:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btnDisagree:
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
