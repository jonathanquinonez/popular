package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.ws.response.AthmEnrollCard;

/**
 * Activity that manages the phone call in the ATHM enrollment process
 */
public class AthmPhoneCall extends AthmActivity implements View.OnClickListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_phone_call);

        ((TextView) findViewById(R.id.tvCode)).setText(getIntent().getStringExtra(MiBancoConstants.ATHM_PHONE_CODE_KEY));

        findViewById(R.id.btnContinue).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                AthmTasks.postAthmEnrollCard(this, new AthmTasks.AthmListener<AthmEnrollCard>() {
                    @Override
                    public void onAthmApiResponse(AthmEnrollCard result) {
                        if(result !=null && !result.isAlertError() && !result.isBlocked() && !result.isDowntime()) {
                            Gson gS = new Gson();
                            String stringResult = gS.toJson(result);
                            Intent accountInfoIntent = new Intent(AthmPhoneCall.this, AthmAccountInfo.class);
                            accountInfoIntent.putExtra(MiBancoConstants.ATHM_ENROLL_CARD_RESPONSE_KEY, stringResult);
                            startActivity(accountInfoIntent);
                            finish();
                        }
                    }
                });
                break;
            case R.id.btnCancel:
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
