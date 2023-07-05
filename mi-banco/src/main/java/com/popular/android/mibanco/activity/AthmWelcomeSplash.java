package com.popular.android.mibanco.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.ATHMUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

/**
 * Activity to start the ATHM enrollment process
 */
public class AthmWelcomeSplash extends BaseSessionActivity {

    private Button btnContinue;
    private Context mContext = this;
    private String athmSsoToken;
    private boolean athmSsoBounded;
    private boolean btnWasClicked;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_welcome);

        BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SSO_WELCOME_SPLASH);

        athmSsoToken = getIntent().getStringExtra(MiBancoConstants.ATHM_SSO_TOKEN_KEY);
        athmSsoBounded = getIntent().getBooleanExtra(MiBancoConstants.ATHM_SSO_BOUND_KEY, false);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        btnContinue.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            btnWasClicked = true;
            Utils.saveAthmWelcomeSplash(mContext, true);
            ATHMUtils.verifyAthmAppVersion(mContext, athmSsoToken, athmSsoBounded, btnWasClicked);
            //finish();
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(btnWasClicked)
            finish();
    }
}
