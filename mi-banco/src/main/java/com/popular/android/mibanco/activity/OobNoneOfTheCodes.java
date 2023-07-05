package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;


/**
 * Activity that manages the process when no oob access will be granted
 */
public class OobNoneOfTheCodes extends BaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oob_no_access);

        findViewById(R.id.btnSubmitNearestBranch).setOnClickListener(this);
        findViewById(R.id.btnSubmitCallNow).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmitNearestBranch:
                Utils.openExternalUrl(OobNoneOfTheCodes.this, getString(R.string.locator_url));
                break;
            case R.id.btnSubmitCallNow:
                final Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string.unblock_local_phone_number)));
                startActivity(callIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(true);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(true);
        menu.findItem(R.id.menu_contact).setVisible(true);

        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        application.reLogin(this);
    }
}
