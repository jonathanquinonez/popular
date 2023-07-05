package com.popular.android.mibanco.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;

public class InformativeActivity extends BaseActivity {

    private String activity;
    private Context mContext = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informative);

        int title = getIntent().getIntExtra(MiBancoConstants.INFORMATIVE_TITLE_ID, 0);
        int message = getIntent().getIntExtra(MiBancoConstants.INFORMATIVE_TEXT_ID, 0);
        int buttonText = getIntent().getIntExtra(MiBancoConstants.INFORMATIVE_BUTTON_ID, 0);
        int image = getIntent().getIntExtra(MiBancoConstants.INFORMATIVE_IMAGE_ID, 0);
        activity = getIntent().getStringExtra("Activity");

        if(title != 0){
            ((TextView)findViewById(R.id.title)).setText(getResources().getString(title));
        }

        if(message != 0){
            ((TextView)findViewById(R.id.message)).setText(getResources().getString(message));
        }

        Button btnOk = (Button)findViewById(R.id.btnOk);
        if(buttonText != 0){

            btnOk.setText(getResources().getString(buttonText));
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if(image != 0){
            ImageView imageV = (ImageView)findViewById(R.id.image);
            imageV.setImageResource(image);
        }


        if(activity.contains("passcode")) {

            final String token = Utils.getStringContentFromShared(getApplicationContext(), MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN);
            if (!Utils.isBlankOrNull(token)) {
                //hide back button in toolbar
                View customView = LayoutInflater.from(this).inflate(R.layout.toolbar_no_back, null);
                getSupportActionBar().setCustomView(customView);
                btnOk.setVisibility(View.INVISIBLE);
            }


        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(activity != null && activity.contains("passcode")){
            if(AutoLoginUtils.isDeviceSecured(mContext)) {
                final String token = Utils.getStringContentFromShared(mContext, MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN);

                if (Utils.isBlankOrNull(token)) {
                    MobileCashUtils.enrollmentLiteWelcomeScreen(mContext, false);
                } else {
                    MobileCashUtils.cashDropTokenVerification(mContext, token);
                }
                finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // Do nothing
        if(activity.equals("passcode-enrollment")){
            finish();
        }

    }
}
