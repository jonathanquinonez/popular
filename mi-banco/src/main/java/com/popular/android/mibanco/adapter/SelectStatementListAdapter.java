package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foound.widget.AmazingAdapter;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.TransactionsCycle;
import com.popular.android.mibanco.util.FontChanger;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * Adapter class to manage data in list to select statements
 */
public class SelectStatementListAdapter extends AmazingAdapter {

    private final static int FIRST_MONTH_INDEX = 0;

    private final static int LAST_MONTH_INDEX = 11;

    private List<Pair<String, List<TransactionsCycle>>> items;
    private int itemsSize = 0;

    private final LayoutInflater inflater;

    private final DateFormatSymbols dfs = new DateFormatSymbols();

    private final String[] months = dfs.getMonths();

    private final Context context;

    public SelectStatementListAdapter(final Context context, final List<Pair<String, List<TransactionsCycle>>> data) {
        this.items = data;
        itemsSize = items.size();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public TransactionsCycle getItem(int position) {
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

    public String formatDate(final String[] date) {
        return getMonthForInt(Integer.parseInt(date[0])) + " " + date[1];
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

    static class ViewHolder {

        public TextView from;

        public TextView to;

        public TextView amount;

        public TextView toLabel;

        public TextView currentLabel;

        public ImageView selected;
    }

    @Override
    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(R.layout.list_item_select_statement, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.from = (TextView) myConvertView.findViewById(R.id.txt_from);
            viewHolder.to = (TextView) myConvertView.findViewById(R.id.txt_payee_to);
            viewHolder.amount = (TextView) myConvertView.findViewById(R.id.txt_amount);
            viewHolder.toLabel = (TextView) myConvertView.findViewById(R.id.to);
            viewHolder.currentLabel = (TextView) myConvertView.findViewById(R.id.current);
            try {
                viewHolder.selected = (ImageView) myConvertView.findViewById(R.id.selected_img);
            } catch (final Exception ex) {

            }
            myConvertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) myConvertView.getTag();
        final TransactionsCycle item = getItem(position);

        String startDate = null;
        String endDate = null;

        if (TextUtils.isEmpty(item.getStartDate())) {
            // item contains only "Current" text
            holder.from.setVisibility(View.GONE);
            holder.to.setVisibility(View.GONE);
            holder.amount.setVisibility(View.GONE);
            holder.toLabel.setVisibility(View.GONE);
            holder.currentLabel.setVisibility(View.VISIBLE);
        } else {
            holder.from.setVisibility(View.VISIBLE);
            holder.to.setVisibility(View.VISIBLE);
            holder.amount.setVisibility(View.VISIBLE);
            holder.toLabel.setVisibility(View.VISIBLE);
            holder.currentLabel.setVisibility(View.GONE);

            String[] startDateSplit;
            String[] endDateSplit;
            startDateSplit = item.getStartDate().split("/");
            endDateSplit = item.getEndDate().split("/");
            startDate = formatDate(startDateSplit);
            endDate = formatDate(endDateSplit);
        }

        holder.from.setText(startDate);
        holder.to.setText(endDate);
        holder.amount.setText(item.getEndBalance());

        if (item.isSelected()) {
            if (holder.selected != null) {
                holder.selected.setVisibility(View.VISIBLE);
            }

            holder.currentLabel.setTextColor(ContextCompat.getColor(context, R.color.blue));
            holder.from.setTextColor(ContextCompat.getColor(context, R.color.blue));
            holder.to.setTextColor(ContextCompat.getColor(context, R.color.blue));
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.blue));
            holder.toLabel.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else {
            if (holder.selected != null) {
                holder.selected.setVisibility(View.INVISIBLE);
            }

            holder.currentLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.from.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.to.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.toLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        return myConvertView;
    }

    @Override
    public void configurePinnedHeader(View headerLayout, int position, int alpha) {
        TextView sectionHeader = (TextView) headerLayout.findViewById(R.id.header_text);
        sectionHeader.setText(getSections()[getSectionForPosition(position)]);
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

}
