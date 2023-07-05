package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.Utils;

/**
 * Terms and Conditions Activity.
 */
public class Terms extends BaseActivity {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);

        final TextView termsAndConditionsTextView = (TextView) findViewById(R.id.textViewTermsAndConditions);
        termsAndConditionsTextView.setText(Utils.loadRawTextResource(R.raw.terms_and_conditions));
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
}
