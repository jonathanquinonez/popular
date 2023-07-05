package com.popular.android.mibanco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PermissionInfoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.permission_info);

        TextView txtDetails = findViewById(R.id.textView_permission_privacy_text);
        String description = getResources().getString(R.string.permission_info_policy);

        Linkify.addLinks(txtDetails, Linkify.ALL);
        txtDetails.setMovementMethod(LinkMovementMethod.getInstance());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            txtDetails.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtDetails.setText(Html.fromHtml(description));
        }

        findViewById(R.id.button_permission_info_accept).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        findViewById(R.id.button_permission_info_cancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
