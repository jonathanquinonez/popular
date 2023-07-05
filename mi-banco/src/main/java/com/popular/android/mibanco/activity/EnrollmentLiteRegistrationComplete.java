package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ProductType;
import com.popular.android.mibanco.util.Utils;

public class EnrollmentLiteRegistrationComplete extends BaseActivity {

    private boolean isCustomer = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_lite_registration_complete);

        Button btnOk = (Button) findViewById(R.id.btnOk);

        isCustomer = getIntent().getBooleanExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, false);

        //App.getApplicationInstance().setCustomerPhone(null);
        if(isCustomer){
            if(AutoLoginUtils.osFingerprintRequirements(this,true)) {//Phone has fingerprint configured
                if (AutoLoginUtils.getFingerprintPreference(this) && App.getApplicationInstance().isAutoLogin()) {
                    AutoLoginUtils.registerDevice(this, ProductType.FINGERPRINT.toString(),true, isCustomer);
                }
            }
            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_CUSTOMER_SUCCESS);
        }else{
            Utils.saveStringContentToShared(this, "customerPhone", App.getApplicationInstance().getCustomerPhone(this));
            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_NON_CUSTOMER_SUCCESS);
        }


        //hide back button in toolbar
        View customView = LayoutInflater.from(this).inflate(R.layout.toolbar_no_back, null);
        getSupportActionBar().setCustomView(customView);

        if(btnOk != null) {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerEntitlements customerEntitlements = App.getApplicationInstance().getCustomerEntitlements();
                    if(!isCustomer){
                        final Intent cashDropIntent = new Intent(getApplicationContext(), EasyCashNonCustHistoryActivity.class);
                        cashDropIntent.putExtra("isCustomer",isCustomer);
                        startActivity(cashDropIntent);
                    }
                    else if(customerEntitlements != null && customerEntitlements.hasCashDrop() != null &&
                            customerEntitlements.hasCashDrop()){
                            final Intent cashDropIntent = new Intent(getApplicationContext(), EasyCashStaging.class);
                            startActivity(cashDropIntent);
                    }
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // overriden to do nothing
        // in case hardware back is pressed

    }
}
