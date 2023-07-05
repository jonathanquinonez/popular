package com.popular.android.mibanco.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;

import java.util.concurrent.TimeUnit;

/**
 * Activity that manages the Wallet Authentication Error
 */
public class WalletAuthenticationError extends BaseSessionActivity {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_authentication_error);

        String errorMessage = getIntent().getStringExtra(MiBancoConstants.ERROR_MESSAGE_KEY).toUpperCase();

        TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);

        if (errorMessage != null) {
            switch (errorMessage) {
                case "ERROR":
                    errorMessageTextView.setText(getResources().getString(R.string.wallet_authentication_error_error));
                    break;
                case "NOT_FOUND":
                    errorMessageTextView.setText(getResources().getString(R.string.wallet_authentication_error_not_found));
                    break;
                case "ERROR_VTS":
                    errorMessageTextView.setText(getResources().getString(R.string.wallet_authentication_error_error_vts));
                    break;
                case "FINGERPRINT_DELAY_FAILED":
                    long minutesLeft = getIntent().getLongExtra(MiBancoConstants.ERROR_MESSAGE_VALUE_KEY, 0);

                    long hours = TimeUnit.MINUTES.toHours(minutesLeft);
                    long minutes = TimeUnit.MINUTES.toMinutes(minutesLeft) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(minutesLeft));

                    String responseString;
                    if (hours > 0) {
                        String time = String.format("%02d:%02d", hours, minutes);
                        responseString = String.format(getString(R.string.wallet_authentication_error_fingerprint_delay_hours), time);

                    } else {
                        String time = String.format("%d", minutes);
                        responseString = String.format(getString(R.string.wallet_authentication_error_fingerprint_delay_minutes), time);
                    }

                    errorMessageTextView.setText(responseString);
                    break;
                default:
                    errorMessageTextView.setText(getResources().getString(R.string.wallet_authentication_error));
                    break;
            }
        }

        findViewById(R.id.btnOk).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                setResult(Activity.RESULT_CANCELED);
                finishAfterTransition();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
