package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.SendAcceptTermsInRDC;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;

public class AcceptTermsActivity extends BaseSessionActivity {

    private static final String EXTRA_AMOUNT_VALUE = "amountValue";

    public static void launch(AppCompatActivity appCompatActivity, String amountValue){
        Intent intent = new Intent(appCompatActivity, AcceptTermsActivity.class);
        intent.putExtra(EXTRA_AMOUNT_VALUE, amountValue);
        appCompatActivity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        adjustFontScale(getResources().getConfiguration());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_terms);

        String amountValue = getIntent().getStringExtra(EXTRA_AMOUNT_VALUE);
        //Terms view
        ((TextView)findViewById(R.id.accept_terms_terms_description)).setText(getResources().getString(R.string.accept_terms_description, amountValue));
        findViewById(R.id.btnAcceptTerms).setOnClickListener(acceptButtonClick);
        findViewById(R.id.btnCancelTerms).setOnClickListener(cancelButtonClick);
        findViewById(R.id.accept_terms_terms).setOnClickListener(openTermsButtonClick);
        //findViewById(R.id.accept_terms_nocharge_description4).setOnClickListener(openTermsAndConditionsButtonClick);

        TextView mterm = findViewById(R.id.accept_terms_nocharge_description4);
        mterm.setClickable(true);
        mterm.setMovementMethod(LinkMovementMethod.getInstance());
        mterm.setText(Html.fromHtml(getResources().getString(R.string.accept_terms_nocharge_trerms)));

        //Turn of visibility to both views
        findViewById(R.id.termsView).setVisibility(View.GONE);
        findViewById(R.id.noChargeView).setVisibility(View.GONE);

        //No Charge view
        findViewById(R.id.btnAcceptTermsNoCharge).setOnClickListener(acceptButtonClick);

        if (amountValue.equals("") || amountValue.equals("0")){
            findViewById(R.id.noChargeView).setVisibility (View.VISIBLE);
        } else {
            findViewById(R.id.termsView).setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener openTermsButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.openExternalUrl(AcceptTermsActivity.this, getString(R.string.accept_terms_cancel_url));
        }
    };

    private View.OnClickListener cancelButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    private View.OnClickListener acceptButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendAcceptTermsRDC();
        }
    };

    private void sendAcceptTermsRDC() {
        if (application != null && application.getAsyncTasksManager() != null) {
            App.getApplicationInstance().getAsyncTasksManager().sendAcceptTermsInRDC(this, new ResponderListener() {
                @Override
                public void sessionHasExpired() {
                    application.reLogin(AcceptTermsActivity.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    SendAcceptTermsInRDC sendAcceptTermsInRDC = (SendAcceptTermsInRDC) data;
                    if (sendAcceptTermsInRDC.getStatus().equals("SUCCESS")) {
                        application.setRdcClientAcceptedTerms(true);
                        Intent intent = new Intent(AcceptTermsActivity.this, DepositCheck.class);
                        startActivity(intent);
                    } else {
                        MobileCashUtils.informativeMessage(AcceptTermsActivity.this, getString(R.string.error_occurred));
                    }
                }
            });
        }
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 1.10) {
            configuration.fontScale = (float) 1.10;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

}

