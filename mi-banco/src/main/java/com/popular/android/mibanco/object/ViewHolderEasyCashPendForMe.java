package com.popular.android.mibanco.object;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.R;

public class ViewHolderEasyCashPendForMe {

    protected View mItem;
    protected long position;
    protected TextView txtAccountName;
    protected TextView txtTrxAmount;
    protected TextView txtTrxLastFourNum;
    protected TextView txtTrxExpirationDate;
    protected TextView txtAthType;
    protected ImageView img;


    public ViewHolderEasyCashPendForMe(final View item) {
        this(item, -1);
    }

    public ViewHolderEasyCashPendForMe(final View item, final long position) {
        mItem = item;
        this.position = position;
    }

    public View getmItem() {
        return mItem;
    }

    public TextView getTxtAccountName() {
        if(txtAccountName == null){
            txtAccountName = (TextView)mItem.findViewById(R.id.txtMcWithdrawalAccountName);
        }
        return txtAccountName;
    }

    public TextView getTxtTrxAmount() {
        if(txtTrxAmount == null){
            txtTrxAmount = (TextView)mItem.findViewById(R.id.txtTrxAmount);
        }
        return txtTrxAmount;
    }

    public TextView getTxtTrxLastFourNum() {
        if(txtTrxLastFourNum == null){
            txtTrxLastFourNum = (TextView)mItem.findViewById(R.id.txtLast4Num);
        }
        return txtTrxLastFourNum;
    }

    public TextView getTxtTrxExpirationDate() {
        if(txtTrxExpirationDate == null){
            txtTrxExpirationDate = (TextView)mItem.findViewById(R.id.txtTrxExpirationDate);
        }
        return txtTrxExpirationDate;
    }

    public TextView getTxtAthType() {
        if(txtAthType == null){
            txtAthType = (TextView)mItem.findViewById(R.id.txtAthType);
        }
        return txtAthType;
    }

    public ImageView getImg() {
        if(img == null){
            img = (ImageView)mItem.findViewById(R.id.accountImg);
        }
        return img;
    }

    public long getPosition() {
        return position;
    }
    public void setPosition(final long position) {
        this.position = position;
    }

}
