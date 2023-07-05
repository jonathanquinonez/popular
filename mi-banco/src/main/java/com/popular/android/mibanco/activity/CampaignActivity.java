package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.Utils;

/**
 * Activity that manages the fingerprint authentication splash screen
 * In the future it could be modified to be a campaign controller
 */
public class CampaignActivity extends AppCompatActivity {

    private Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        final boolean isFromSession = getIntent().getBooleanExtra(MiBancoConstants.KEY_ENROLL_LITE_FINGERPRINT, false);

        if(!isFromSession && AutoLoginUtils.getFingerprintPreference(mContext)) {
            LinearLayout linearLayoutNext = (LinearLayout)findViewById(R.id.ll_next_option);
            Button buttonNext = (Button) findViewById(R.id.button_campaign_next);

            if(linearLayoutNext != null && buttonNext != null) {
                linearLayoutNext.setVisibility(View.VISIBLE);
                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterUsernameActivity(true);
                    }
                });
            }
        }else {
            RelativeLayout linearLayoutActivate = (RelativeLayout) findViewById(R.id.ll_activate_option);
            Button buttonYes = (Button) findViewById(R.id.button_campaign_yes);
            Button buttonNo = (Button) findViewById(R.id.button_campaign_no);

            if(linearLayoutActivate != null && buttonYes != null && buttonNo != null) {
                linearLayoutActivate.setVisibility(View.VISIBLE);

                if(!isFromSession) {
                    buttonYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterUsernameActivity(true);
                        }
                    });


                    buttonNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterUsernameActivity(false);
                        }
                    });
                }else{
                    buttonYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AutoLoginUtils.saveFingerprintPreference(mContext, true);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(EasyCashStaging.ACTIVATE_FINGERPRINT_RESULT, EasyCashStaging.ACTIVATE_FINGERPRINT_RESULT_YES);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    });

                    buttonNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(EasyCashStaging.ACTIVATE_FINGERPRINT_RESULT, EasyCashStaging.ACTIVATE_FINGERPRINT_RESULT_NO);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    });
                }
            }
        }
    }

    private void enterUsernameActivity(boolean rememberSwitchActive)
    {
        AutoLoginUtils.saveFingerprintPreference(mContext, rememberSwitchActive);
        final Intent loginIntent = new Intent(mContext, EnterUsername.class);
        if(rememberSwitchActive) {
            loginIntent.putExtra(MiBancoConstants.REMEMBER_USERNAME_DEFAULT, true);
        }
        Utils.saveFingerprintCampaign(mContext, true);
        startActivity(loginIntent);
        finish();
    }
}
