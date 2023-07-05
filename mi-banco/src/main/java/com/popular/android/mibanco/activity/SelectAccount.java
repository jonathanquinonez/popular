package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.SelectAccountAdapter;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

/**
 * Activity that manages the selection of an account
 */
public class SelectAccount extends AthmActivity implements AdapterView.OnItemClickListener {

    private SelectAccountAdapter listAdapter;
    private AccountCard selectedAccount;
    private boolean isAccountOnly;
    private boolean validateAccoutValance;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_account);

        ListView listViewCards = (ListView) findViewById(R.id.listViewCards);
        Bundle acctBundle = getIntent().getBundleExtra("accounts");
        isAccountOnly = getIntent().getBooleanExtra("accountsOnly", false);
        validateAccoutValance = getIntent().getBooleanExtra("validateAccountValance", false);

        ArrayList<AccountCard> accounts = null;
        if(acctBundle != null){
            accounts = (ArrayList<AccountCard>)( acctBundle.getSerializable("accounts"));
        }

        listAdapter = new SelectAccountAdapter(this);
        listViewCards.setAdapter(listAdapter);
        listAdapter.updateDataset(accounts);
        listViewCards.setOnItemClickListener(this);
        listAdapter.updateDataset(accounts);

        String selectionTitle = getIntent().getStringExtra(MiBancoConstants.SELECT_CARD_TITLE);
        if(!Utils.isBlankOrNull(selectionTitle)) {
            TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
            txtTitle.setText(selectionTitle);
        }


        String warningMessage = getIntent().getStringExtra(MiBancoConstants.SELECT_CARD_WARNING);
        if(!Utils.isBlankOrNull(warningMessage)) {
            TextView txtWarning = (TextView) findViewById(R.id.txtSelectAcctDisclamer);
            txtWarning.setText(warningMessage);
        }else{
            LinearLayout msgLayout = (LinearLayout)findViewById(R.id.select_acct_msg_layout);
            msgLayout.setVisibility(View.GONE);
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedAccount = listAdapter.getItem(position);

        if(isAccountOnly){

            int balance = Utils.getAmountIntValue(selectedAccount.getBalance());
            if (validateAccoutValance && balance < MiBancoConstants.MC_MIN_BALANCE_AMOUNT) {
                MobileCashUtils.informativeMessageWithoutTitle(this, R.string.mc_error_minbalance);
                return;
            }else {
                Intent returnIntent = new Intent();

                //selectedAccount.setSelectedCardFromAccount(selectedAccount.getAtmCards().get(0));
                returnIntent.putExtra(MiBancoConstants.ATH_CARD_KEY, selectedAccount);
                setResult(RESULT_OK, returnIntent);
                finish();
            }

        }else {

            // Select Cards
            int balance = Utils.getAmountIntValue(selectedAccount.getBalance());
            if (validateAccoutValance && balance < MiBancoConstants.MC_MIN_BALANCE_AMOUNT) {
                MobileCashUtils.informativeMessageWithoutTitle(this, R.string.mc_error_minbalance);
                return;
            }

            if (selectedAccount.getAtmCards() != null && selectedAccount.getAtmCards().size() > 0) {
                if (selectedAccount.getAtmCards().size() == 1) {

                    Intent returnIntent = new Intent();
                    selectedAccount.setSelectedCardFromAccount(selectedAccount.getAtmCards().get(0));
                    returnIntent.putExtra(MiBancoConstants.ATH_CARD_KEY, selectedAccount);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Intent selectCardIntent = new Intent(this, SelectCard.class);
                    Bundle b = new Bundle();
                    ArrayList<AccountCard> cards = new ArrayList<>();
                    for (AccountCard card : selectedAccount.getAtmCards()) {
                        card.setNickname(card.getAtmType().equals("INT") ? getResources().getString(R.string.mc_ath_international) : getResources().getString(R.string.mc_ath_regular));
                        card.setAccountLast4Num(card.getAtmLast4Num());
                        cards.add(card);
                    }
                    b.putSerializable("cards", cards);
                    selectCardIntent.putExtra("cards", b);
                    selectCardIntent.putExtra(MiBancoConstants.SELECT_CARD_TITLE, getResources().getString(R.string.select_card_header));
                    startActivityForResult(selectCardIntent, MiBancoConstants.MC_SELECT_CARD_REQUEST_CODE);
                }
            } else {
                MobileCashUtils.informativeMessageWithoutTitle(this, R.string.mc_accounts_error_message);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Manage result from select card
        if (resultCode == RESULT_OK && requestCode == MiBancoConstants.MC_SELECT_CARD_REQUEST_CODE) {

            AccountCard card = (AccountCard) data.getSerializableExtra(MiBancoConstants.ATH_CARD_KEY);
            Intent returnIntent = new Intent();
            selectedAccount.setSelectedCardFromAccount(card);
            returnIntent.putExtra(MiBancoConstants.ATH_CARD_KEY, selectedAccount);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }


}
