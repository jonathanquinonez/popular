package com.popular.android.mibanco.object;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.popular.android.mibanco.R;

/**
 * Provides and abstraction layer for Account ViewItem
 * @author ismael ahumada <ismael.ahumada@evertecinc.com>
 * @see com.popular.android.mibanco.activity.Accounts
 * @since Java 1.8
 * @version 1.0
 */
public class ViewHolder {
    protected TextView comment;
    protected ImageView image;
    protected View mItem;
    protected TextView name;
    protected TextView footer;//Represents the text shown in the footer of the account item view
    protected LinearLayout footerLayout;//It is the layout of the footer
    protected LinearLayout mainLayout;//It is the layout of the account information
    protected long position;
    protected TextView value;
    protected TextView onoffCounter;
    protected LinearLayout onoffView;
    protected RelativeLayout plasticView;
    protected SwitchButton onOffButton;

    /** Linear Layout containing cash rewards loading image, balance and description */
    protected LinearLayout tsysLoyaltyRewardsView;
    /** Text View containing cash rewards balance */
    protected TextView tsysLoyaltyRewardsBalanceTextView;
    /** Image View containing cash rewards loading image */
    protected ImageView tsysLoyaltyRewardsLoading;

    /** Linear Layout containing cash rewards loading image, balance*/
    protected RelativeLayout premiaBalanceView;
    /** Image View containing cash rewards loading image */
    protected ImageView premiaBalanceLoading;


    public ViewHolder(final View item) {
        this(item, -1);
    }

    public ViewHolder(final View item, final long position) {
        mItem = item;
        this.position = position;
    }

    public TextView getComment() {
        if (null == comment) {
            comment = mItem.findViewById(R.id.item_comment);
        }
        return comment;
    }

    public ImageView getImage() {
        if (null == image) {
            image = mItem.findViewById(R.id.item_image);
        }
        return image;
    }

    public TextView getName() {
        if (null == name) {
            name = mItem.findViewById(R.id.item_name);
        }
        return name;
    }

    /**
     * @return the TextView that contains the footer text
     */
    public TextView getFooter() {
        if (null == footer) {
            footer = mItem.findViewById(R.id.item_footer);
        }
        return footer;
    }

    /**
     * @return the footerLayout of account item
     */
    public LinearLayout getFooterLayout() {
        if (null == footerLayout) {
            footerLayout = mItem.findViewById(R.id.footer_layout);
        }
        return footerLayout;
    }

    /**
     * @return the mainLayout of account item
     */
    public LinearLayout getMainLayout() {
        if (null == mainLayout) {
            mainLayout = mItem.findViewById(R.id.main_layout);
        }
        return mainLayout;
    }


    public LinearLayout getOnOffView() {
        if (null == onoffView) {
            onoffView = mItem.findViewById(R.id.onoff_plastic_layout);
        }
        return onoffView;
    }

    public TextView getOnOffCounter() {
        if (null == onoffCounter) {
            onoffCounter = mItem.findViewById(R.id.onoff_plastic_count);
        }
        return onoffCounter;
    }

    public SwitchButton getOnOffButton() {
        if (null == onOffButton) {
            onOffButton = mItem.findViewById(R.id.switchOnOff);
        }
        return onOffButton;
    }


    public long getPosition() {
        return position;
    }

    public TextView getValue() {
        if (null == value) {
            value = mItem.findViewById(R.id.item_value);
        }
        return value;
    }

    public RelativeLayout getPlasticView() {
        if (null == plasticView) {
            plasticView = mItem.findViewById(R.id.onoff_card_view);
        }
        return plasticView;
    }

    public void setPosition(final long position) {
        this.position = position;
    }

    public LinearLayout getTsysLoyaltyRewardsView() {
        if(tsysLoyaltyRewardsView == null) {
            tsysLoyaltyRewardsView = mItem.findViewById(R.id.tsys_loyalty_rewards_view);
        }
        return tsysLoyaltyRewardsView;
    }

    public TextView getTsysLoyaltyRewardsBalanceTextView() {
        if(tsysLoyaltyRewardsBalanceTextView == null) {
            tsysLoyaltyRewardsBalanceTextView = mItem.findViewById(R.id.tsys_loyalty_rewards_balance);
        }
        return tsysLoyaltyRewardsBalanceTextView;
    }

    public ImageView getTsysLoyaltyRewardsLoading() {
        if(tsysLoyaltyRewardsLoading == null) {
            tsysLoyaltyRewardsLoading = mItem.findViewById(R.id.tsys_loyalty_rewards_loading);
        }
        return tsysLoyaltyRewardsLoading;
    }


    public RelativeLayout getPremiaBalanceView() {
        if(premiaBalanceView == null) {
            premiaBalanceView = mItem.findViewById(R.id.premia_loading_balance_view);
        }
        return premiaBalanceView;
    }

    public ImageView getPremiaBalanceLoading() {
        if(premiaBalanceLoading == null) {
            premiaBalanceLoading = mItem.findViewById(R.id.premia_balance_loading_item);
        }
        return premiaBalanceLoading;
    }
}
