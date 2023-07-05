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
 * Adapter class to manage data in card select list
 */
public class SelectCardAdapter extends BaseListAdapter<AccountCard> {

    public SelectCardAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_select_card, parent, false);
        }

        TextView tvName = ViewHolder.get(convertView, R.id.tvName);
        TextView tvLast4Digits = ViewHolder.get(convertView, R.id.tvLast4Digits);
        ImageView imgCard = ViewHolder.get(convertView, R.id.imgCard);

        AccountCard cardItem = getItem(position);
        tvName.setText(cardItem.getNickname());
        tvLast4Digits.setText(cardItem.getAccountLast4Num());
        ImageLoader.getInstance().displayImage(cardItem.getCardImageUri(), imgCard);

        return convertView;
    }
}
