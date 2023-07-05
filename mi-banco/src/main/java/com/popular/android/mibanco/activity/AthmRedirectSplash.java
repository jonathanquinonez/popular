package com.popular.android.mibanco.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.ATHMUtils;

public class AthmRedirectSplash extends BaseSessionActivity {

    private Button btnContinue;
    private Context mContext = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_redirect);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        btnContinue.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            ATHMUtils.redirectToStore(mContext);
            finish();
        } else {
            super.onClick(v);
        }
    }
}