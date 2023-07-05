package com.popular.android.mibanco;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class Quality extends BaseActivity implements OnClickListener {

    private static final String MAIN_URL_PREFS_KEY = "qa_main_url";
    private static final String PORT_PREFS_KEY = "qa_port";
    private static final String URL_PATH_PREFS_KEY = "qa_url_path";

    private EditText mainUrlEditText;
    private EditText portEditText;
    private EditText urlPathEditText;
    private EditText currentBaseUrlEditText;

    private String defaultApiUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quality);

        mainUrlEditText = (EditText) findViewById(R.id.quality_main_url_edit_text);
        portEditText = (EditText) findViewById(R.id.quality_port_edit_text);
        urlPathEditText = (EditText) findViewById(R.id.quality_url_path_edit_text);
        currentBaseUrlEditText = (EditText) findViewById(R.id.quality_current_base_url);

        OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                refreshBaseUrl();
            }
        };

        currentBaseUrlEditText.setKeyListener(null);
        ((Button) findViewById(R.id.restore_defaults_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.save_button)).setOnClickListener(this);

        setActionBarLogoBackButton();

        defaultApiUrl = getString(getResources().getIdentifier(getString(R.string.api_url), "string", getPackageName()));
        String mainUrl = Utils.getPrefsString(MAIN_URL_PREFS_KEY, this);
        String port = Utils.getPrefsString(PORT_PREFS_KEY, this);
        String urlPath = Utils.getPrefsString(URL_PATH_PREFS_KEY, this);
        if (TextUtils.isEmpty(mainUrl) || TextUtils.isEmpty(port) || TextUtils.isEmpty(urlPath)) {
            displayBaseUrl(defaultApiUrl);
        } else {
            mainUrlEditText.setText(mainUrl);
            portEditText.setText(port);
            urlPathEditText.setText(urlPath);
            refreshBaseUrl();
        }

        mainUrlEditText.setOnFocusChangeListener(focusChangeListener);
        portEditText.setOnFocusChangeListener(focusChangeListener);
        urlPathEditText.setOnFocusChangeListener(focusChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.restore_defaults_button:
            Utils.setPrefsString(MAIN_URL_PREFS_KEY, "", this);
            Utils.setPrefsString(PORT_PREFS_KEY, "", this);
            Utils.setPrefsString(URL_PATH_PREFS_KEY, "", this);
            displayBaseUrl(defaultApiUrl);
            App.getApplicationInstance().setApiUrl(defaultApiUrl);
            break;
        case R.id.save_button:
            saveBaseUrl();
            break;
        }
    }

    private void displayBaseUrl(String baseUrl) {
        try {
            URL url = new URL(baseUrl);
            mainUrlEditText.setText(url.getProtocol() + "://" + url.getHost());
            portEditText.setText(Integer.toString(url.getPort() == -1 ? url.getDefaultPort() : url.getPort()));
            urlPathEditText.setText(url.getPath());
            currentBaseUrlEditText.setText(url.toString());
        } catch (MalformedURLException e) {
            Toast.makeText(this, R.string.quality_invalid_url, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshBaseUrl() {
        try {
            URL url = new URL(mainUrlEditText.getText().toString() + ":" + portEditText.getText().toString() + urlPathEditText.getText().toString());
            displayBaseUrl(url.toString());
        } catch (MalformedURLException e) {
            Log.e(App.getApplicationInstance().getAttributionTag(), e.getMessage());
        }
    }

    private void saveBaseUrl() {
        if (TextUtils.isEmpty(mainUrlEditText.getText().toString())) {
            mainUrlEditText.setError(getString(R.string.quality_field_required));
            return;
        } else if (TextUtils.isEmpty(portEditText.getText().toString())) {
            portEditText.setError(getString(R.string.quality_field_required));
            return;
        } else if (TextUtils.isEmpty(urlPathEditText.getText().toString())) {
            urlPathEditText.setError(getString(R.string.quality_field_required));
            return;
        }

        mainUrlEditText.setError(null);
        portEditText.setError(null);
        urlPathEditText.setError(null);

        try {
            URL url = new URL(mainUrlEditText.getText().toString() + ":" + portEditText.getText().toString() + urlPathEditText.getText().toString());
            refreshBaseUrl();
            Utils.setPrefsString(MAIN_URL_PREFS_KEY, mainUrlEditText.getText().toString(), this);
            Utils.setPrefsString(PORT_PREFS_KEY, portEditText.getText().toString(), this);
            Utils.setPrefsString(URL_PATH_PREFS_KEY, urlPathEditText.getText().toString(), this);
            App.getApplicationInstance().setApiUrl(url.toString());
            Toast.makeText(this, R.string.quality_base_url_saved, Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            Toast.makeText(this, R.string.quality_invalid_url, Toast.LENGTH_SHORT).show();
        }
    }
}
