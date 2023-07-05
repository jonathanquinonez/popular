package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foound.widget.AmazingAdapter;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Receipts.ETransferReceipt;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.TransferHistoryEntry;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * Adapter class to manage data in the transfers history list
 */
public class TransferHistoryAdapter extends AmazingAdapter {

    private final static int FIRST_MONTH_INDEX = 0;

    private final static int LAST_MONTH_INDEX = 11;

    private List<Pair<String, List<TransferHistoryEntry>>> items;
    private int itemsSize = 0;

    private final LayoutInflater inflater;

    private final String[] months = new DateFormatSymbols().getMonths();

    private final Context context;

    private ETransferReceipt transferReceipt;

    public TransferHistoryAdapter(final Context context, final List<Pair<String, List<TransferHistoryEntry>>> data, ETransferReceipt transferReceipt) {
        this.items = data;
        this.itemsSize =items.size();
        this.context = context;
        this.transferReceipt = transferReceipt;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolderTransfer {

        public TextView from;
        public TextView fromNumber;
        public TextView to;
        public TextView toNumber;
        public TextView amount;
        public TextView date;
        public ImageView logo;
    }

    private static class ViewHolderTransferFiltered {

        public TextView to;
        public TextView toNumber;
        public TextView amount;
        public TextView date;
    }

    @Override
    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        View myConvertView = convertView;
        if (transferReceipt == ETransferReceipt.FULLLIST) {
            if (myConvertView == null) {
                myConvertView = inflater.inflate(R.layout.list_item_receipt, null);

                ViewHolderTransfer viewHolder = new ViewHolderTransfer();
                viewHolder.from = (TextView) myConvertView.findViewById(R.id.txt_from);
                viewHolder.fromNumber = (TextView) myConvertView.findViewById(R.id.txt_from_number);
                viewHolder.to = (TextView) myConvertView.findViewById(R.id.txt_payee_to);
                viewHolder.toNumber = (TextView) myConvertView.findViewById(R.id.txt_to_payee_number);
                viewHolder.amount = (TextView) myConvertView.findViewById(R.id.txt_amount);
                viewHolder.date = (TextView) myConvertView.findViewById(R.id.txt_date);
                viewHolder.logo = (ImageView) myConvertView.findViewById(R.id.img_receipt_card);
                myConvertView.setTag(viewHolder);
            }

            ViewHolderTransfer holder = (ViewHolderTransfer) myConvertView.getTag();
            final TransferHistoryEntry item = getItem(position);

            if (item != null) {
                holder.from.setText(item.getSourceNickname());
                holder.fromNumber.setText(item.getSourceAccountLast4Num());
                holder.to.setText(item.getTargetNickname());
                holder.toNumber.setText(item.getTargetAccountLast4Num());
                holder.amount.setText(item.getAmount());

                if (App.getApplicationInstance() != null && App.getApplicationInstance().getCustomerAccountsMap() != null) {
                    CustomerAccount account = App.getApplicationInstance().getCustomerAccountsMap().get(item.getTargetApiAccountKey() + item.getTargetAccountNumberSuffix());
                    if (account != null) {
                        Utils.displayAccountImage(holder.logo, account);
                    } else {
                        holder.logo.setImageResource(R.drawable.account_image_default);
                    }
                } else {
                    holder.logo.setImageResource(R.drawable.account_image_default);
                }

                String[] effectiveDateSplit = item.getEffectiveDate().split("/");
                String effectiveDate = formatDate(effectiveDateSplit);

                holder.date.setText(effectiveDate);
            }
        } else if (transferReceipt == ETransferReceipt.BYACCOUNT) {
            if (myConvertView == null) {
                myConvertView = inflater.inflate(R.layout.list_item_receipt_filtered, null);

                ViewHolderTransferFiltered viewHolder = new ViewHolderTransferFiltered();
                viewHolder.to = (TextView) myConvertView.findViewById(R.id.txt_payee_to);
                viewHolder.toNumber = (TextView) myConvertView.findViewById(R.id.txt_to_payee_number);
                viewHolder.amount = (TextView) myConvertView.findViewById(R.id.txt_amount);
                viewHolder.date = (TextView) myConvertView.findViewById(R.id.txt_date);
                myConvertView.setTag(viewHolder);
            }

            ViewHolderTransferFiltered holder = (ViewHolderTransferFiltered) myConvertView.getTag();
            final TransferHistoryEntry item = getItem(position);

            if (item != null) {
                holder.to.setText(item.getSourceNickname());
                holder.toNumber.setText(item.getSourceAccountLast4Num());
                holder.amount.setText(item.getAmount());

                String[] effectiveDateSplit = item.getEffectiveDate().split("/");
                String effectiveDate = formatDate(effectiveDateSplit);
                holder.date.setText(effectiveDate);
            }
        }

        return myConvertView;
    }

    public String formatDate(final String[] date) {
        return getMonthForInt(Integer.parseInt(date[0])) + " " + date[1] + ", " + date[2];
    }

    public String getMonthForInt(final int monthInt) {
        int tempMonth = monthInt;
        String month = "invalid";
        tempMonth = tempMonth - 1;
        if (tempMonth >= FIRST_MONTH_INDEX && tempMonth <= LAST_MONTH_INDEX) {
            month = months[tempMonth];
        }
        return month;
    }

    @Override
    public void configurePinnedHeader(View headerLayout, int position, int alpha) {
        int sectionIndex = getSectionForPosition(position);
        if (sectionIndex < 0 || sectionIndex >= getSections().length) {
            return;
        }
        TextView sectionHeader = (TextView) headerLayout.findViewById(R.id.header_text);
        sectionHeader.setText(getSections()[sectionIndex]);
        sectionHeader.setBackgroundColor(alpha << 24 | ContextCompat.getColor(context, R.color.white));
        sectionHeader.setTextColor(alpha << 24 | ContextCompat.getColor(context, R.color.account_details_header));
        FontChanger.changeFonts(sectionHeader);
    }

    @Override
    public int getPositionForSection(int section) {
        if (section < 0) {
            section = 0;
        }
        if (section >= itemsSize) {
            section = itemsSize - 1;
        }
        int c = 0;
        for (int i = 0; i < itemsSize; i++) {
            if (section == i) {
                return c;
            }
            c += items.get(i).second.size();
        }

        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        int c = 0;
        for (int i = 0; i < itemsSize; i++) {
            if (position >= c && position < c + items.get(i).second.size()) {
                return i;
            }
            c += items.get(i).second.size();
        }
        return -1;
    }

    @Override
    public String[] getSections() {
        String[] res = new String[itemsSize];
        for (int i = 0; i < itemsSize; i++) {
            res[i] = items.get(i).first;
        }
        return res;
    }

    @Override
    public int getCount() {
        int res = 0;
        for (int i = 0; i < itemsSize; i++) {
            res += items.get(i).second.size();
        }
        return res;
    }

    @Override
    public TransferHistoryEntry getItem(int position) {
        int c = 0;
        for (int i = 0; i < itemsSize; i++) {
            if (position >= c && position < c + items.get(i).second.size()) {
                return items.get(i).second.get(position - c);
            }
            c += items.get(i).second.size();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onNextPageRequested(int page) {
    }

    @Override
    protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        View headerBox = view.findViewById(R.id.header_box);
        TextView sectionTitle = (TextView) headerBox.findViewById(R.id.header_text);
        if (displaySectionHeader) {
            headerBox.setVisibility(View.VISIBLE);
            sectionTitle.setText(getSections()[getSectionForPosition(position)]);
        } else {
            headerBox.setVisibility(View.GONE);
        }
        FontChanger.changeFonts(view);
    }
}
