package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

/**
 * Activity that manages the process where an ATHM transfer is sent
 */
public class AthmTransferSent extends AthmActivity implements View.OnClickListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_transfer_sent);

        TextView tvTransferDetails = (TextView) findViewById(R.id.tvTransactionDetails);
        tvTransferDetails.setText(getString(R.string.athm_transfer_details, getIntent().getStringExtra(MiBancoConstants.ATHM_TRANSFER_AMOUNT_KEY), getIntent().getStringExtra(MiBancoConstants.ATHM_TRANSFER_PHONE_KEY)));

        findViewById(R.id.btnOk).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOk) {
            setResult(RESULT_OK);
            finish();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
