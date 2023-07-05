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
import com.popular.android.mibanco.adapter.SelectCardAdapter;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

/**
 * Activity that manages the selection of a card
 */
public class SelectCard extends AthmActivity implements AdapterView.OnItemClickListener {

    private SelectCardAdapter listAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_account);

        ListView listViewCards = (ListView) findViewById(R.id.listViewCards);
        Bundle acctBundle = getIntent().getBundleExtra("cards");
        ArrayList<AccountCard> accounts = null;
        if(acctBundle != null){
            accounts = (ArrayList<AccountCard>)( acctBundle.getSerializable("cards"));
        }

        listAdapter = new SelectCardAdapter(this);
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
        AccountCard selectedCard = listAdapter.getItem(position);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(MiBancoConstants.ATH_CARD_KEY, selectedCard);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
