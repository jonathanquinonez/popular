package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseListAdapter;
import com.popular.android.mibanco.model.AccountCard;

/**
 * Adapter class to manage data in select account list
 */
public class SelectAccountAdapter extends BaseListAdapter<AccountCard> {

    public SelectAccountAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_athm_card, parent, false);
        }

        TextView tvName = ViewHolder.get(convertView, R.id.tvName);
        TextView tvBalance = ViewHolder.get(convertView, R.id.tvBalance);
        TextView tvLast4Digits = ViewHolder.get(convertView, R.id.tvLast4Digits);
        ImageView imgCard = ViewHolder.get(convertView, R.id.imgCard);

        AccountCard cardItem = getItem(position);
        if(cardItem != null) {
            tvName.setText(cardItem.getNickname());
            tvBalance.setText(cardItem.getBalance());
            tvLast4Digits.setText(cardItem.getAccountLast4Num());
            if (cardItem.getCardImageUri() != null) {
                ImageLoader.getInstance().displayImage(cardItem.getCardImageUri(), imgCard);
            }
        }

        return convertView;
    }
}
