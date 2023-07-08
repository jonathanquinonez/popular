package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.view.pickerview.ArrayBankWheelItem;

import java.util.ArrayList;
import java.util.List;

public class TransferAccountAdapter extends BaseAdapter {
    private Context context;
    private List<ArrayBankWheelItem> item;
    public TransferAccountAdapter(Context context, List<ArrayBankWheelItem> item){
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return this.item.size();
    }

    @Override
    public Object getItem(int position) {
        return this.item.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = convertView;
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        v = layoutInflater.inflate(R.layout.wheel_item_fragment, null);

        String accountName  = item.get(position).getName();
        String accountNumber  = item.get(position).getCode();
        String accountAmmount  = item.get(position).getAmount();
        int imagen  = item.get(position).getImgResource();

        TextView textViewName = (TextView) v.findViewById(R.id.item_name);
        TextView textViewNumber = (TextView) v.findViewById(R.id.item_last4num);
        TextView textViewAmmount = (TextView) v.findViewById(R.id.item_balance);
        ImageView imagenView = (ImageView) v.findViewById(R.id.account_image);

        textViewName.setText(accountName);
        textViewNumber.setText(accountNumber);
        textViewAmmount.setText(accountAmmount);
        imagenView.setImageResource(imagen);

        return v;
    }
}


