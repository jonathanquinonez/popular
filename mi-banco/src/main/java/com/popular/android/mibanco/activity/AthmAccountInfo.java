package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.view.ObjectPickerFragment;
import com.popular.android.mibanco.ws.response.AthmEnrollAccount;
import com.popular.android.mibanco.ws.response.AthmEnrollCard;
import com.qburst.android.widget.spinnerextended.SpinnerExtended;

/**
 * Activity that manages the account information in the ATHM enrollment process
 */
public class AthmAccountInfo extends AthmActivity implements View.OnClickListener, ObjectPickerFragment.OnObjectsSetListener, TextWatcher {

    private TextView tvAccountHint;
    private LinearLayout viewAthCard;
    private SpinnerExtended spinnerExpirationDate;
    private EditText etCardNumber;
    private Button btnContinue;

    private AccountCard selectedCard;
    private String cardExpirationDateMonth;
    private String cardExpirationDateYear;
    private AthmEnrollCard cardResponse;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_account_info);

        LinearLayout viewSelectAccount = (LinearLayout) findViewById(R.id.viewSelectAccount);
        tvAccountHint = (TextView) findViewById(R.id.tvAccountHint);
        viewAthCard = (LinearLayout) findViewById(R.id.viewAthCard);
        spinnerExpirationDate = (SpinnerExtended) findViewById(R.id.spinnerExpirationDate);

        etCardNumber = (EditText) findViewById(R.id.etCardNumber);
        etCardNumber.addTextChangedListener(this);

        viewSelectAccount.setOnClickListener(this);
        spinnerExpirationDate.setOnClickListener(this);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        if (savedInstanceState != null) {
            selectedCard = (AccountCard) savedInstanceState.getSerializable(MiBancoConstants.ATH_CARD_KEY);
            cardExpirationDateMonth = savedInstanceState.getString(MiBancoConstants.ATHM_CARD_EXPIRATION_DATE_MONTH_KEY);
            cardExpirationDateYear = savedInstanceState.getString(MiBancoConstants.ATHM_CARD_EXPIRATION_DATE_YEAR_KEY);

            setSelectedCard();
            setExpirationDate();
        }

        String result = getIntent().getStringExtra(MiBancoConstants.ATHM_ENROLL_CARD_RESPONSE_KEY);
        if(result != null && !result.equals("")){
            cardResponse = new Gson().fromJson(result,AthmEnrollCard.class);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedCard != null) {
            outState.putSerializable(MiBancoConstants.ATH_CARD_KEY, selectedCard);
        }
        if (cardExpirationDateMonth != null) {
            outState.putSerializable(MiBancoConstants.ATHM_CARD_EXPIRATION_DATE_MONTH_KEY, cardExpirationDateMonth);
        }
        if (cardExpirationDateYear != null) {
            outState.putSerializable(MiBancoConstants.ATHM_CARD_EXPIRATION_DATE_YEAR_KEY, cardExpirationDateYear);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MiBancoConstants.ATHM_SELECT_CARD_REQUEST_CODE) {
            selectedCard = (AccountCard) data.getSerializableExtra(MiBancoConstants.ATH_CARD_KEY);
            setSelectedCard();
        }
    }

    private void validate() {
        if (TextUtils.isEmpty(etCardNumber.getText())|| etCardNumber.getText().length() !=16
                || selectedCard == null
                || cardExpirationDateMonth == null
                || cardExpirationDateYear == null
                || cardResponse == null
                || cardExpirationDateMonth.equals(getString(R.string.athm_expiration_month))
                || cardExpirationDateYear.equals(getString(R.string.athm_expiration_year))) {
            btnContinue.setEnabled(false);
        } else {
            btnContinue.setEnabled(true);
        }
    }

    private void setSelectedCard() {
        if (selectedCard != null) {
            tvAccountHint.setVisibility(View.GONE);
            viewAthCard.removeAllViews();

            View cardLayout = LayoutInflater.from(this).inflate(R.layout.athm_card_layout, viewAthCard, false);

            TextView tvName = (TextView) cardLayout.findViewById(R.id.tvName);
            TextView tvBalance = (TextView) cardLayout.findViewById(R.id.tvBalance);
            TextView tvLast4Digits = (TextView) cardLayout.findViewById(R.id.tvLast4Digits);
            ImageView imgCard = (ImageView) cardLayout.findViewById(R.id.imgCard);

            tvName.setText(selectedCard.getNickname());
            tvBalance.setText(selectedCard.getBalance());
            tvLast4Digits.setText(selectedCard.getAccountLast4Num());
            ImageLoader.getInstance().displayImage(selectedCard.getCardImageUri(), imgCard);

            viewAthCard.addView(cardLayout);
            viewAthCard.setVisibility(View.VISIBLE);
        }
        validate();
    }

    private void setExpirationDate() {
        if (cardExpirationDateMonth != null && cardExpirationDateYear != null) {
            spinnerExpirationDate.setAdapter(new ArrayAdapter<>(
                    this,
                    R.layout.spinner_extended_selected_item,
                    android.R.id.text1,
                    new String[]{cardExpirationDateMonth + " / " + cardExpirationDateYear}));
            spinnerExpirationDate.setSelection(0);
        }
        validate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewSelectAccount:
                if (cardResponse != null) {
                    App.getApplicationInstance().setListAccountsSelect(cardResponse.getAvailableAccounts());
                    Intent selectAccountIntent = new Intent(this, SelectAccount.class);
                    Bundle b = new Bundle();
                    b.putSerializable("accounts", cardResponse.getAvailableAccounts());
                    selectAccountIntent.putExtra("accounts", b);
                    selectAccountIntent.putExtra("accountsOnly",true);
                    startActivityForResult(selectAccountIntent, MiBancoConstants.ATHM_SELECT_CARD_REQUEST_CODE);
                }
                break;
            case R.id.spinnerExpirationDate:
                if (cardResponse != null) {
                    ObjectPickerFragment.showObjectPicker(this, cardResponse.getExpirationMonths(), cardResponse.getExpirationYears(), getString(R.string.set), getString(R.string.cancel), null);
                }
                break;
            case R.id.btnContinue:
                AthmTasks.postAthmEnrollAccount(this, selectedCard.getFrontEndId(), cardExpirationDateMonth, cardExpirationDateYear, etCardNumber.getText().toString(), new AthmTasks.AthmListener<AthmEnrollAccount>() {
                    @Override
                    public void onAthmApiResponse(AthmEnrollAccount result) {
                        if(result!= null && !result.isAlertError() && !result.isBlocked()&& !result.isDowntime()) {
                            if (result.getResponderMessage().equalsIgnoreCase("enroll_auth")) {
                                startActivity(new Intent(AthmAccountInfo.this, AthmLoginInfo.class));
                            } else {
                                startActivity(new Intent(AthmAccountInfo.this, AthmRegistrationInfo.class));
                            }
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
    public void onObjectsSet(Object object1, Object object2, Bundle data) {
        cardExpirationDateMonth = object1.toString();
        cardExpirationDateYear = object2.toString();
        setExpirationDate();
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
