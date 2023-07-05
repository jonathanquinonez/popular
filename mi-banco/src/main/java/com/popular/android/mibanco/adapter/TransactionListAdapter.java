package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.WebViewActivity;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter class to manage data in the transactions list
 */
public class TransactionListAdapter extends BaseAdapter {

    private static final int TYPE_HEADER = 1;

    private static final int TYPE_ITEM = 0;

    private static final int TYPE_MAX_COUNT = TYPE_HEADER + 1;

    private final static String DESCRIPTION_DATE_PATTERN_MATCHER_STRING = ".*ON (\\d{2}/\\d{2}/\\d{2})";

    private final static String DESCRIPTION_DATE_PATTERN_STRING = "ON \\d{2}/\\d{2}/\\d{2}";

    private final String accountSubtype;

    private final String accountNumber;

    private final String currentStatementRangeString;

    private final ArrayList<Object> filteredItems;

    private final Context context;

    private final LayoutInflater inflater;

    private final String IDA = "IDA"; //account type IDA

    public TransactionListAdapter(final Context context, final List<AccountTransaction> items, final String rangeString, final String acctSubtype, final String accountFrontedId) {
        this.context = context;

        filteredItems = new ArrayList<Object>(items);
        currentStatementRangeString = rangeString;
        accountSubtype = acctSubtype;
        accountNumber = accountFrontedId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addHeaders();
    }

    // calculate positions of headers and add them to the list
    private void addHeaders() {
        int inProcessTransactionsCount = 0;

        if (filteredItems != null) {
            for (final Object transaction : filteredItems) {
                if (transaction instanceof AccountTransaction) {
                    if (((AccountTransaction) transaction).getIsDebit()) {
                        ++inProcessTransactionsCount;
                    } else {
                        break;
                    }
                }
            }
            if (inProcessTransactionsCount > 0) {
                filteredItems.add(0, context.getString(R.string.statement_in_process).toUpperCase());
                if (filteredItems.size() - 1 - inProcessTransactionsCount > 0) {
                    filteredItems.add(inProcessTransactionsCount + 1, currentStatementRangeString);
                }
            } else {
                if (FeatureFlags.MBCA_104() && App.isSelectedNonTransactioanlAcct(accountSubtype)) {
                    if(filteredItems.size() <= 0) {
                        filteredItems.add(0, context.getString(R.string.nothing_to_display_non_trans));
                    }
                    else {
                        filteredItems.add(0, context.getString(R.string.transactions).toUpperCase());
                        filteredItems.add(1, context.getString(R.string.mortgage_disclaimer));
                    }
                }
                else {
                    filteredItems.add(0, currentStatementRangeString);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public Object getItem(final int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        final Object item = filteredItems.get(position);
        if (item instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    static class ViewHolder {

        public TextView header;

        public TextView description;

        public TextView date;

        public TextView amount;

        public TextView amountSign;

        public TextView mortgageDisclaimer;

        public ImageView accountDetailArrow;

    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        final Object item = filteredItems.get(position);

        if (myConvertView == null) {
            ViewHolder viewHolder = new ViewHolder();
            if (item instanceof String) {
                myConvertView = inflater.inflate(R.layout.list_item_transactions_header, parent, false);
                viewHolder.header = (TextView) myConvertView.findViewById(R.id.text_header);

                if(FeatureFlags.MBCA_104()  && App.isSelectedNonTransactioanlAcct(accountSubtype) && position == 1) {
                    myConvertView = inflater.inflate(R.layout.list_item_mortgage_disclaimer, parent, false);
                    viewHolder.mortgageDisclaimer = (TextView) myConvertView.findViewById(R.id.mortgage_disclaimer);
                }
            }
            else {
                myConvertView = inflater.inflate(R.layout.list_item_transaction, parent, false);
                viewHolder.description = (TextView) myConvertView.findViewById(R.id.description);
                viewHolder.date = (TextView) myConvertView.findViewById(R.id.date);
                viewHolder.amount = (TextView) myConvertView.findViewById(R.id.amount);
                viewHolder.amountSign = (TextView) myConvertView.findViewById(R.id.amount_sign);
                //add statement arrow ImageView
                addImageViewWithChevronToTransaction(myConvertView, viewHolder);
            }
            myConvertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) myConvertView.getTag();
        if (item instanceof String) {
            holder.header.setText((String) item);

            if(FeatureFlags.MBCA_104() && holder.mortgageDisclaimer != null && App.isSelectedNonTransactioanlAcct(accountSubtype) && position == 1) {
                holder.mortgageDisclaimer.setText((String) item);
            }
        }
        else {
            final AccountTransaction transation = (AccountTransaction) item;
            holder.description.setText(transation.getDescription().replaceFirst(DESCRIPTION_DATE_PATTERN_STRING, "").replaceFirst("TELEPAGO ", ""));

            final Calendar today = Calendar.getInstance();

            Date date = null;
            try {
                String dateString = transation.getPostedDate();

                if (TextUtils.isEmpty(dateString)) {
                    Pattern descriptionDatePattern = Pattern.compile(DESCRIPTION_DATE_PATTERN_MATCHER_STRING);
                    final Matcher dateMatcher = descriptionDatePattern.matcher(transation.getDescription());
                    if (dateMatcher.find()) {

                        SimpleDateFormat sdf2 = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_IN_DESCRIPTION_DATE_FORMAT);
                        dateString = dateMatcher.group(1);
                        date = sdf2.parse(dateString);
                        final Calendar transactionDate = Calendar.getInstance();
                        transactionDate.setTime(date);

                        if (today.get(Calendar.DAY_OF_YEAR) == transactionDate.get(Calendar.DAY_OF_YEAR) && today.get(Calendar.YEAR) == transactionDate.get(Calendar.YEAR)) {
                            holder.date.setText(R.string.today);
                        } else {
                            holder.date.setText(DateFormat.getLongDateFormat(context).format(date));
                        }
                    } else {
                        holder.date.setText("");
                    }
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);
                    date = sdf.parse(dateString);
                    holder.date.setText(DateFormat.getLongDateFormat(context).format(date));
                }
            } catch (final ParseException e) {

                holder.date.setText(transation.getPostedDate());
                e.printStackTrace();
            }

            holder.amount.setText(transation.getAmount());
            if ("-".equals(transation.getSign())) {
                holder.amountSign.setVisibility(View.VISIBLE);
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.account_details_debit));
            } else if ("+".equals(transation.getSign())) {
                holder.amountSign.setVisibility(View.GONE);
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.account_details_credit));
            }
            else {
                holder.amountSign.setVisibility(View.GONE);
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black));
            }


            if (accountSubtype.equals(IDA) && !transation.getIsDebit()){
                myConvertView.setOnClickListener(accountDetailClickListener(transation));
                if(transation.getShowDetailEnabled() != null){
                    if(transation.getShowDetailEnabled().equalsIgnoreCase("true")){
                        holder.accountDetailArrow.setVisibility(View.VISIBLE);
                    }else{
                        holder.accountDetailArrow.setVisibility(View.INVISIBLE);
                        myConvertView.setOnClickListener(null);
                    }
                }else{
                    holder.accountDetailArrow.setVisibility(View.INVISIBLE);
                    myConvertView.setOnClickListener(null);
                }
            }else{
                holder.accountDetailArrow.setVisibility(View.GONE);
                myConvertView.setOnClickListener(null);
            }
        }

        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }

    private void addImageViewWithChevronToTransaction(View myConvertView, ViewHolder viewHolder) {
        if (myConvertView.findViewById(R.id.statement_arrow) instanceof ImageView) {
            viewHolder.accountDetailArrow = (ImageView) myConvertView.findViewById(R.id.statement_arrow);
        }
    }

    private void showTransactionDetailsWebView(AccountTransaction transaction){
        Intent webViewIntent = new Intent(context, WebViewActivity.class);
        webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(
                context.getString(R.string.transaction_detail) + transaction.getTraceId())
                .replace("%1", accountNumber));
        webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_RIGHT_MENU, true);
        webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        context.startActivity(webViewIntent);
    }

    private View.OnClickListener accountDetailClickListener(final AccountTransaction trans) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDetailsWebView(trans);
            }
        };
    }
}
