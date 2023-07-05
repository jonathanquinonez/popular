package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.ws.response.AthmEnrollInfo;
import com.popular.android.mibanco.ws.response.AthmEnrollPhone;

/**
 * Activity to start the ATHM enrollment process
 */
public class AthmEnroll extends AthmActivity implements View.OnClickListener {

    private Button btnEnroll;
    private Context mContext = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_enroll);

        btnEnroll = (Button) findViewById(R.id.btnEnroll);

        AthmTasks.getAthmEnrollInfo(this, new AthmTasks.AthmListener<AthmEnrollInfo>() {
            @Override
            public void onAthmApiResponse(AthmEnrollInfo result) {

                if(result != null) {
                    if (result.isQualified()) {
                        btnEnroll.setOnClickListener(AthmEnroll.this);
                        btnEnroll.setVisibility(View.VISIBLE);
                    } else {
                        btnEnroll.setVisibility(View.INVISIBLE);
                    }
                }else{
                    Toast.makeText(mContext,R.string.error_occurred,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEnroll) {
            AthmTasks.postAthmEnrollPhone(this, new AthmTasks.AthmListener<AthmEnrollPhone>() {
                @Override
                public void onAthmApiResponse(AthmEnrollPhone result) {
                    Intent phoneNumberIntent = new Intent(AthmEnroll.this, AthmPhoneNumber.class);
                    phoneNumberIntent.putExtra(MiBancoConstants.ATHM_PHONE_NUMBER_RESPONSE_KEY, result);
                    startActivity(phoneNumberIntent);
                    finish();
                }
            });
        } else {
            super.onClick(v);
        }
    }
}
