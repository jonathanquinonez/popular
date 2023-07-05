package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AthmPhoneProvider;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.ws.response.AthmEnrollPhone;
import com.popular.android.mibanco.ws.response.AthmEnrollPhoneCode;
import com.qburst.android.widget.spinnerextended.SpinnerExtended;

/**
 * Activity to manage the phone number entry in the ATHM enrollment process
 */
public class AthmPhoneNumber extends AthmActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {

    private EditText etPhoneNumber;
    private SpinnerExtended spinnerProvider;
    private Button btnContinue;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_phone_number);

        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etPhoneNumber.addTextChangedListener(this);

        spinnerProvider = (SpinnerExtended) findViewById(R.id.spinnerProvider);
        spinnerProvider.setOnItemSelectedListener(this);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        findViewById(R.id.btnCancel).setOnClickListener(this);

        AthmEnrollPhone enrollPhone = ((AthmEnrollPhone) getIntent().getSerializableExtra(MiBancoConstants.ATHM_PHONE_NUMBER_RESPONSE_KEY));
        if(enrollPhone != null) {
            ArrayAdapter<AthmPhoneProvider> adapter = new ArrayAdapter<>(
                    AthmPhoneNumber.this,
                    R.layout.spinner_extended_selected_item,
                    android.R.id.text1,
                    enrollPhone.getProviders());

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProvider.setAdapter(adapter);
        }
    }

    private void validate() {
        if (!TextUtils.isEmpty(etPhoneNumber.getText()) && etPhoneNumber.getText().length() == 10 && spinnerProvider.getSelectedItemPosition() != SpinnerExtended.NOTHING_SELECTED_POSITION) {
            btnContinue.setEnabled(true);
        } else {
            btnContinue.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                AthmTasks.postAthmEnrollPhone(this, etPhoneNumber.getText().toString(), ((AthmPhoneProvider) spinnerProvider.getSelectedItem()).getValue(), new AthmTasks.AthmListener<AthmEnrollPhoneCode>() {
                    @Override
                    public void onAthmApiResponse(AthmEnrollPhoneCode result) {
                        if(result != null && !result.isAlertError() && !result.isBlocked() && !result.isDowntime()) {
                            Intent phoneCallIntent = new Intent();
                            phoneCallIntent.setClass(AthmPhoneNumber.this, AthmPhoneCall.class);
                            phoneCallIntent.putExtra(MiBancoConstants.ATHM_PHONE_CODE_KEY, result.getConfirmationCode());
                            startActivity(phoneCallIntent);
                            finish();
                        }
                    }
                });
                break;
            case R.id.btnCancel:
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        validate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        validate();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        validate();
    }
}
