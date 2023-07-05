package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.Utils;

public class EasyCashLocator extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easycash_wv);

        WebView webViewTerms = (WebView) findViewById(R.id.webViewTerms);
        String requestedUrl = getString(R.string.easycash_locator_url);
        if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
            webViewTerms.loadUrl(requestedUrl);
        }else{
            webViewTerms.goBack();
        }
        //webViewTerms.loadUrl(getString(R.string.easycash_locator_url));
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        menu.findItem(R.id.menu_easycash_faq).setVisible(true);
        menu.findItem(R.id.menu_easycash_locator).setVisible(true);
        menu.findItem(R.id.menu_easycash_unbound).setVisible(true);
        return true;
    }
}
