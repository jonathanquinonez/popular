package com.popular.android.mibanco.activity;

import static com.popular.android.mibanco.MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.Utils;

public class EasyCashFaqs extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easycash_wv);
        boolean isCustomer = getIntent().getBooleanExtra(KEY_ENROLL_LITE_IS_CUSTOMER, false);

        WebView webViewTerms = (WebView) findViewById(R.id.webViewTerms);
        String requestedUrl = "";
        if (isCustomer) {
            requestedUrl = getString(R.string.easycash_customer_faq_url);
            if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                webViewTerms.loadUrl(requestedUrl);
            }else{
                webViewTerms.goBack();
            }
           // webViewTerms.loadUrl(getString(R.string.easycash_customer_faq_url));
        } else {
            requestedUrl = getString(R.string.easycash_noncustomer_faq_url);
            if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                webViewTerms.loadUrl(requestedUrl);
            }else{
                webViewTerms.goBack();
            }
            //webViewTerms.loadUrl(getString(R.string.easycash_noncustomer_faq_url));
        }
        finish();
    }



    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        menu.findItem(R.id.menu_easycash_faq).setVisible(false);
        menu.findItem(R.id.menu_easycash_locator).setVisible(false);
        menu.findItem(R.id.menu_easycash_unbound).setVisible(false);
        return true;
    }


}
