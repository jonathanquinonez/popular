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
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Receipts.EPaymentReceipt;
import com.popular.android.mibanco.model.PaymentHistoryEntry;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * Adapter class to manage data in payee history list
 */
public class PaymentHistoryAdapter extends AmazingAdapter {

    private final static int FIRST_MONTH_INDEX = 0;

    private final static int LAST_MONTH_INDEX = 11;

    private List<Pair<String, List<PaymentHistoryEntry>>> items;
    private int itemsSize = 0;

    private final LayoutInflater inflater;

    private final String[] months = new DateFormatSymbols().getMonths();

    private final Context context;

    private EPaymentReceipt paymentReceipt;

    public PaymentHistoryAdapter(final Context context, final List<Pair<String, List<PaymentHistoryEntry>>> data, EPaymentReceipt paymentReceipt) {
        this.items = data;
        this.itemsSize = items.size();
        this.context = context;
        this.paymentReceipt = paymentReceipt;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolderPayment {

        public TextView from;
        public TextView fromNumber;
        public TextView to;
        public TextView toNumber;
        public TextView amount;
        public TextView date;
        public ImageView logo;
    }

    private static class ViewHolderPaymentFiltered {

        public TextView to;
        public TextView toNumber;
        public TextView amount;
        public TextView date;
    }

    @Override
    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        View myConvertView = convertView;
        if (paymentReceipt == EPaymentReceipt.FULLLIST) {
            if (myConvertView == null) {
                myConvertView = inflater.inflate(R.layout.list_item_receipt, null);

                ViewHolderPayment viewHolder = new ViewHolderPayment();
                viewHolder.from = (TextView) myConvertView.findViewById(R.id.txt_from);
                viewHolder.fromNumber = (TextView) myConvertView.findViewById(R.id.txt_from_number);
                viewHolder.to = (TextView) myConvertView.findViewById(R.id.txt_payee_to);
                viewHolder.toNumber = (TextView) myConvertView.findViewById(R.id.txt_to_payee_number);
                viewHolder.amount = (TextView) myConvertView.findViewById(R.id.txt_amount);
                viewHolder.date = (TextView) myConvertView.findViewById(R.id.txt_date);
                viewHolder.logo = (ImageView) myConvertView.findViewById(R.id.img_receipt_card);
                myConvertView.setTag(viewHolder);
            }

            ViewHolderPayment holder = (ViewHolderPayment) myConvertView.getTag();
            final PaymentHistoryEntry item = getItem(position);

            if (item != null) {
                holder.from.setText(item.getSourceNickname());
                holder.fromNumber.setText(item.getSourceAccountLast4Num());
                holder.to.setText(item.getPayeeNickname());
                holder.toNumber.setText(item.getPayeeAccountLast4Num());
                holder.amount.setText(item.getAmount());

                String[] effectiveDateSplit = item.getEffectiveDate().split("/");
                String effectiveDate = formatDate(effectiveDateSplit);

                holder.date.setText(effectiveDate);

                final int drawableId = Utils.getPayeeDrawableResource(Utils.getValidGlobalPayeeId(item.getGlobalPayeeId()));
                holder.logo.setImageResource(drawableId);
            }
        } else if (paymentReceipt == EPaymentReceipt.BYPAYEE) {
            if (myConvertView == null) {
                myConvertView = inflater.inflate(R.layout.list_item_receipt_filtered, null);

                ViewHolderPaymentFiltered viewHolder = new ViewHolderPaymentFiltered();
                viewHolder.to = (TextView) myConvertView.findViewById(R.id.txt_payee_to);
                viewHolder.toNumber = (TextView) myConvertView.findViewById(R.id.txt_to_payee_number);
                viewHolder.amount = (TextView) myConvertView.findViewById(R.id.txt_amount);
                viewHolder.date = (TextView) myConvertView.findViewById(R.id.txt_date);
                myConvertView.setTag(viewHolder);
            }

            ViewHolderPaymentFiltered holder = (ViewHolderPaymentFiltered) myConvertView.getTag();
            final PaymentHistoryEntry item = getItem(position);

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
    public PaymentHistoryEntry getItem(int position) {
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
