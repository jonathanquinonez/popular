package com.popular.android.mibanco.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.MarketplaceCard;

import java.util.List;


public class MarketplaceAdapter extends BaseAdapter {
    Context context;
    List<MarketplaceCard> cards;
    LayoutInflater inflater;
    MarketplaceCard rowItem;

    public MarketplaceAdapter(Context context, List<MarketplaceCard> listItem) {
        this.context = context;
        cards = listItem;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int arg0) {
        return cards.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return cards.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE) instanceof LayoutInflater && convertView == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.marketplace_card, null);
        }

        if (getItem(position) instanceof MarketplaceCard) {
            rowItem = (MarketplaceCard)getItem(position);
        }

        TextView textTitle = null;
        TextView textSubtitle = null;
        TextView textButton = null;
        ImageView imageView = null;
        textTitle = convertView.findViewById(R.id.marketplace_title);
        textSubtitle = convertView.findViewById(R.id.marketplace_subtitle);
        textButton = convertView.findViewById(R.id.marketplace_button);
        imageView = convertView.findViewById(R.id.marketplace_image);

        textTitle.setText(rowItem.getTitle());
        textSubtitle.setText(rowItem.getSubTitle());
        textButton.setText(rowItem.getButtonText());

        if (rowItem.getImageBitmap() != null) {
            imageView.setAlpha(0.f);
            imageView.setImageBitmap(rowItem.getImageBitmap());
            imageView.animate()
                    .alpha(1.f).setDuration(1000).start();
        }

        return convertView;
    }
}
