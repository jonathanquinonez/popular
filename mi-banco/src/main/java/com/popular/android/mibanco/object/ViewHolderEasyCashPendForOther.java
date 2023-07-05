package com.popular.android.mibanco.object;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.R;

public class ViewHolderEasyCashPendForOther {

    protected View mItem;
    protected long position;
    protected TextView txtToOrFrom;
    protected TextView txtExpirationDate;
    protected TextView txtTrxAmount;
    protected ImageView imageQr;


    public ViewHolderEasyCashPendForOther(final View item) {
        this(item, -1);
    }

    public ViewHolderEasyCashPendForOther(final View item, final long position) {
        mItem = item;
        this.position = position;
    }

    public View getmItem() {
        return mItem;
    }

    public TextView getTxtToOrFrom() {
        if(txtToOrFrom == null){
            txtToOrFrom = (TextView)mItem.findViewById(R.id.txtToOrFrom);
        }
        return txtToOrFrom;
    }

    public TextView getTxtExpirationDate() {
        if(txtExpirationDate == null){
            txtExpirationDate = (TextView)mItem.findViewById(R.id.txtExpirationDate);
        }
        return txtExpirationDate;
    }

    public TextView getTxtTrxAmount() {
        if(txtTrxAmount == null){
            txtTrxAmount = (TextView)mItem.findViewById(R.id.txtTrxAmount);
        }
        return txtTrxAmount;
    }

    public ImageView getImageQr() {
        if(imageQr == null){
            imageQr = (ImageView)mItem.findViewById(R.id.imageQr);
        }
        return imageQr;
    }

    public long getPosition() {
        return position;
    }
    public void setPosition(final long position) {
        this.position = position;
    }

}
