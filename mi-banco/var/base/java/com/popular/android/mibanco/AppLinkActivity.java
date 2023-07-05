package com.popular.android.mibanco;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.popular.android.mibanco.activity.WebViewActivity;
import com.popular.android.mibanco.util.Utils;

public class AppLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null){
            openAppLink(appLinkData.toString());
        }
    }

    private void openAppLink(String url){
        final Intent intent = new Intent(AppLinkActivity.this, WebViewActivity.class);
        if(!url.contains("javascript") && Utils.isValidUrl(url, getApplicationContext())){
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, url);
        }
        String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
        for (int i = 0; i < urlBlacklist.length; ++i) {
            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
        }
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
        intent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);

        final Intent iIntro = new Intent(AppLinkActivity.this, IntroScreen.class);
        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AppLinkActivity.this.startActivity(iIntro);
        startActivity(intent);

        finish();
    }
}
