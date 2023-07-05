package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;

/**
 * Contact Us Activity class.
 */
public class Downtime extends BaseSessionActivity {


    /**
     * The message tag
     */
    public static final String DOWNTIME_MESSAGE = "downtimeMessage";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.downtime);

        TextView message = (TextView) findViewById(R.id.maintenance_message);
        final Intent intent = getIntent();
        if (message != null && intent.getStringExtra(DOWNTIME_MESSAGE) != null) {
            message.setText(intent.getStringExtra(DOWNTIME_MESSAGE));
        }
    }
}
