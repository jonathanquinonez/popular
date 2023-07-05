package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kyleduo.switchbutton.SwitchButton;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.OnOffCard;
import com.popular.android.mibanco.model.OnOffPlastics;
import com.popular.android.mibanco.object.ViewHolder;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.EnumStatusResponses;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.CustomGsonParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class OnOffAccountPlastics extends BaseSessionActivity {

    private Context mContext = this;
    private static String plasticFont = "fonts/ocraextended.ttf";
    private List<OnOffCard> cards;
    DisplayMetrics displayMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_off_account_plastics);

        configureAccountInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayMetrics = new DisplayMetrics();

        Customer loggedInCustomer = application.getLoggedInUser();
        if (loggedInCustomer != null) {
            if( cards == null) {
                String json = getIntent().getStringExtra("plastics");
                CustomGsonParser gson = new CustomGsonParser();
                OnOffPlastics content = gson.fromJson(json, OnOffPlastics.class);
                cards = content.getPlastics();
            }
            loadPlastics(cards, R.id.onoff_cards_section, R.id.onoff_cards_list);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_onoff_faq).setVisible(true);
        menu.findItem(R.id.menu_onoff_logout).setVisible(true);
        menu.findItem(R.id.menu_logout).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_onoff_faq:
                Intent webViewIntent = new Intent(this, WebViewActivity.class);
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.on_off_faq));
                startActivity(webViewIntent);
                break;
            case R.id.menu_onoff_logout:
                showLogoutDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Fills out plastics with provided data.
     *
     * @param listItems accounts information list
     * @param sectionResId the resource id of a section
     * @param listResId the resource id of a list
     */
    private void loadPlastics(final List<OnOffCard> listItems, final int sectionResId, final int listResId) {
        final LinearLayout listLayout = findViewById(listResId);
        listLayout.removeAllViews();
        if (listItems.size() == 0) {
            findViewById(sectionResId).setVisibility(View.GONE);
        } else {
            for (final OnOffCard acc : listItems) {
                listLayout.addView(getView(acc));
            }
        }
    }

    /**
     * Gets the view for an account.
     *
     * @param item the data item
     * @return the view
     */

    @SuppressLint("ClickableViewAccessibility")
    private View getView(final OnOffCard item) {
        ViewHolder holder = null;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewItem = inflater.inflate(R.layout.on_off_plastics, null);
        viewItem.setTag(holder);

        adjustCardLayoutAspectRatio(viewItem, item);

        holder = new ViewHolder(viewItem);
        holder.getName().setText(item.getPlasticNumber());
        holder.getComment().setText(item.getPlasticEmbossedName());

        Typeface type = Typeface.createFromAsset(getAssets(), plasticFont);
        holder.getComment().setTypeface(type);
        holder.getName().setTypeface(type);

        final SwitchButton button = holder.getOnOffButton();
        button.setTextColor(ContextCompat.getColor(this, R.color.white));
        button.setClickable(false);

        TextView lastTransactionView = viewItem.findViewById(R.id.date_off);

        if (item.isPlasticIsOff()) {
            button.bringToFront();
            viewItem.findViewById(R.id.toggle_description).bringToFront();
            viewItem.findViewById(R.id.onoff_card_view).invalidate();
            viewItem.findViewById(R.id.onoff_overlay).setVisibility(View.VISIBLE);
            viewItem.findViewById(R.id.toggle_description).bringToFront();
            viewItem.findViewById(R.id.toggle_description).setVisibility(View.VISIBLE);

            lastTransactionView.setText(buildLastTransactionText(getString(R.string.on_off_last_date_off), convertOnOffDate(item.getPlasticLastTurnedOff())));

        } else {
            Drawable thumbOn = getResources().getDrawable(R.drawable.onoff_toggle_on);
            button.setChecked(true);
            button.setThumbDrawable(thumbOn);
            button.setBackColorRes(R.color.switch_green);

            if (item.getPlasticLastTransactionDate() == null || item.getPlasticLastTransactionDate().isEmpty()) {
                lastTransactionView.setVisibility(View.INVISIBLE);
            } else {
                lastTransactionView.setVisibility(View.VISIBLE);
            }

            lastTransactionView.setText(buildLastTransactionText(getString(R.string.on_off_last_transaction), convertOnOffDate(item.getPlasticLastTransactionDate())));
        }

        ImageView imageV = viewItem.findViewById(R.id.onoff_plastic_image);
        imageV.setImageResource(getCardImage(item.getPlasticType()));
        imageV.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        manageToggle(viewItem, button, item);
                }
                return true;
            }
        });

        return viewItem;
    }

    private int getCardImage(String type) {
        switch (type) {
            case "GOLD":
                return R.drawable.on_off_card_gold;
            case "SECURITIES":
                return R.drawable.on_off_card_securities;
            case "ONE":
                return R.drawable.on_off_card_one;
            case "CLASSIC":
                return R.drawable.on_off_card_classic;
            default:
                return R.drawable.on_off_card_classic;
        }
    }

    /**
     * Manages the plastic status
     * @param button the button that has been toggled
     * @param card the on/off card representing the plastic
     */
    private void manageToggle(final View view, final SwitchButton button, final OnOffCard card) {

        int statusDesc = R.string.on_off_change_onstatus_desc;
        int statusDescTitle = R.string.on_off_change_onstatus_title;

        if (button.isChecked()) {
            statusDesc = R.string.on_off_change_status_desc;
            statusDescTitle = R.string.on_off_change_status_title;
        }

        AlertDialogParameters params = new AlertDialogParameters(mContext, statusDesc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        String action = button.isChecked() ? "OFF": "ON";
                        updatePlastic(card, action, button, view);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });

        params.setTitle(getResources().getString(statusDescTitle));
        params.setPositiveButtonText(getResources().getString(R.string.on_off_accept));
        params.setNegativeButtonText(getResources().getString(R.string.ec_send_cash_alert_no));
        Utils.showAlertDialog(params);
    }

    private void updatePlastic(final OnOffCard onOffCard, final String action, final SwitchButton button, final View view) {
        App instance = App.getApplicationInstance();
        if (instance != null && instance.getAsyncTasksManager() != null) {
            instance.getAsyncTasksManager().postMobilePlasticStatusTask(OnOffAccountPlastics.this, onOffCard.getPlasticFrontEndId(), action, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(OnOffAccountPlastics.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    if (data != null) {
                        OnOffPlastics card = (OnOffPlastics) data;
                        if (card == null && card.getResponseStatus() == null) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ONOFF_PLASTICUPDATE_ERROR);
                            displayOnOffToggleError(action);
                            return;
                        }

                        int counter;
                        int responseStatus = Integer.parseInt(card.getResponseStatus());

                        if (responseStatus == EnumStatusResponses.NEW_STATUS_OFF_RESPONSE_SUCCESS.getCode()) {
                            counter = +1;
                            onOffCard.setPlasticLastTurnedOff(convertOnOffDate(card.getCardPlastic().getLastOffInfo().getDate()));
                            onOffCard.setPlasticIsOff(true);
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ONOFF_PLASTICUPDATE_OFF_SUCCESS);
                        } else if (responseStatus == EnumStatusResponses.NEW_STATUS_ON_RESPONSE_SUCCESS.getCode()) {
                            counter = -1;
                            onOffCard.setPlasticIsOff(false);
                            onOffCard.setPlasticLastTransactionDate(convertOnOffDate(onOffCard.getPlasticLastTransactionDate()));
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ONOFF_PLASTICUPDATE_ON_SUCCESS);
                        } else {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ONOFF_PLASTICUPDATE_ERROR);
                            displayOnOffToggleError(action);
                            return;
                        }

                        List<CustomerAccount> updateAccs = card.getTmpList();
                        for (final CustomerAccount acc : updateAccs) {
                            acc.setOnOffCount(acc.getOnOffCount()+counter);
                        }

                        //Update Session On Off indicator
                        Intent intent = new Intent(MiBancoConstants.ONOFF_RELOAD_INDICATOR);
                        intent.putExtra("newAccountCounter", counter);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                        validateStatus(view, button, onOffCard);

                    } else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_ONOFF_PLASTICUPDATE_ERROR);
                        displayOnOffToggleError(action);
                        return;
                    }
                }
            });
        }
    }

    private void configureAccountInfo() {
        CustomerAccount account = (CustomerAccount) getIntent().getSerializableExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY);
        if (account == null) {
            finish();
            return;
        }

        Utils.displayAccountImage((ImageView) findViewById(R.id.item_image), account);

        ((TextView) findViewById(R.id.item_name)).setText(account.getNickname());
        ((TextView) findViewById(R.id.item_value)).setText(account.getPortalBalance());
        if (account.isBalanceColorRed()) {
            ((TextView) findViewById(R.id.item_value)).setTextColor(ContextCompat.getColor(this, R.color.account_details_header_debit_balance));
            ((TextView) findViewById(R.id.item_value)).setText(((TextView) findViewById(R.id.item_value)).getText());
        }

        ((TextView) findViewById(R.id.item_comment)).setText(account.getAccountLast4Num());
    }

    private void validateStatus(View view, SwitchButton button, OnOffCard card) {

        Drawable thumbOn;

        TextView lastTransactionView = (TextView) view.findViewById(R.id.date_off);

        if (!button.isChecked()) {
            button.setBackColorRes(R.color.switch_green);
            thumbOn = getResources().getDrawable(R.drawable.onoff_toggle_on);

            view.findViewById(R.id.onoff_overlay).setVisibility(View.GONE);
            view.findViewById(R.id.toggle_description).setVisibility(View.GONE);

            lastTransactionView.setText(buildLastTransactionText(getString(R.string.on_off_last_transaction), card.getPlasticLastTransactionDate()));
        } else {
            button.setBackColorRes(R.color.switch_red);
            thumbOn = getResources().getDrawable(R.drawable.onoff_toggle_off);

            button.bringToFront();
            view.findViewById(R.id.toggle_description).bringToFront();
            view.findViewById(R.id.onoff_card_view).invalidate();
            view.findViewById(R.id.onoff_overlay).setVisibility(View.VISIBLE);
            view.findViewById(R.id.toggle_description).setVisibility(View.VISIBLE);

            lastTransactionView.setText(buildLastTransactionText(getString(R.string.on_off_last_date_off), card.getPlasticLastTurnedOff()));
        }

        button.toggle();
        button.setThumbDrawable(thumbOn);

    }

    private void displayOnOffToggleError(String action) {
        if (action.equalsIgnoreCase("ON")) {
            MobileCashUtils.informativeMessage(mContext, R.string.onoff_toggle_on_error_message);
        } else {
            MobileCashUtils.informativeMessage(mContext, R.string.onoff_toggle_off_error_message);
        }
    }

    private String convertOnOffDate(String dateString) {

        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
            Date date = dateFormat.parse(dateString);
            dateFormat.setTimeZone(TimeZone.getDefault());

            final String language = application.getLanguage();

            if (language != null)  {
                if(language.equalsIgnoreCase(MiBancoConstants.ENGLISH_LANGUAGE_CODE)){
                    dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                } else{
                    dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                }
            }

            return dateFormat.format(date);

        } catch (ParseException e){
            // Could not convert, display original date.
            return dateString;
        }
    }

    private SpannableStringBuilder buildLastTransactionText(String boldText, String regularText) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(boldText + "  " + regularText);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void adjustCardLayoutAspectRatio(View viewItem, OnOffCard card) {
        RelativeLayout cardLayout = viewItem.findViewById(R.id.card_layout);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Bitmap cardBitmap = BitmapFactory.decodeResource(getResources(), getCardImage(card.getPlasticType()));
        float cardImageRatio = (float) cardBitmap.getWidth() / (float) cardBitmap.getHeight();
        int newCardImageHeight = (int) (displayMetrics.widthPixels / cardImageRatio);
        cardLayout.getLayoutParams().height = newCardImageHeight;
    }
}
