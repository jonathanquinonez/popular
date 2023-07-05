package com.popular.android.mibanco.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AccountDepositAdapter extends ArrayAdapter<CustomerAccount>  {

    private Context listcontext;
    private List<CustomerAccount> accountList;
    private Activity activity;

    public AccountDepositAdapter(Activity activity,
                                 ArrayList<CustomerAccount> list){

        super(activity.getApplicationContext(), 0, list);
        listcontext = activity;
        accountList = list;
        this.activity = activity;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        final CustomerAccount currentAccount = accountList.get(position);

        if(listItem == null)
            listItem = LayoutInflater.from(listcontext)
                    .inflate(R.layout.list_item_athm_card,parent,false);

        ImageView accountImage = listItem.findViewById(R.id.imgCard);
        TextView tvName =listItem.findViewById(R.id.tvName);
        TextView tvBalance = listItem.findViewById(R.id.tvBalance);
        TextView tvLast4Digits = listItem.findViewById(R.id.tvLast4Digits);

        if(currentAccount != null) {
            accountImage.setImageResource(currentAccount.getImgResource());
            tvName.setText(currentAccount.getNickname());
            tvBalance.setText(currentAccount.getPortalBalance());
            tvLast4Digits.setText(currentAccount.getAccountLast4Num());
        }

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MiBancoConstants.ACCOUNT_DEPOSIT_INFO, currentAccount);
                activity.setResult(RESULT_OK, intent);
                activity.finish();
            }
        });
        return listItem;
    }
}
