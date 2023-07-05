package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.AccountDepositAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.model.CustomerAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Mi Banco  - Mi Banco Credit Acquisition-MBCA.
 *
 * @author Stephanie Diaz <Stephanie.Diaz@evertecinc.com>
 * @version 1.0
 */
public class CashRewardsAccountDepositConfiguration extends BaseSessionActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_deposit_selection);
        List<CustomerAccount> accountsList;
        AccountDepositAdapter adapter;
        ArrayList<CustomerAccount> accounts = new ArrayList<>();

        if (App.getApplicationInstance() != null && App.getApplicationInstance()
                .getAsyncTasksManager() != null) {
            accountsList = (List<CustomerAccount>) getIntent()
                    .getSerializableExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL);

            if (accountsList == null) {
                finish();
                return;
            }

            for (CustomerAccount account : accountsList) {
                accounts.add(account);
            }

            adapter = new AccountDepositAdapter(this, accounts);

            ListView listView = findViewById(R.id.listViewCards);
            listView.setAdapter(adapter);

        }

    }
}
