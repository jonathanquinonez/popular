package com.popular.android.mibanco.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.object.ListItemEBills;
import com.popular.android.mibanco.object.ListItemSelectable;
import com.popular.android.mibanco.util.FontChanger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Adapter class to manage data in ebills list
 */
public class ListEBillsAdapter extends BaseAdapter {

    private final Context context;

    private LayoutInflater inflater;

    private final ArrayList<ListItemEBills> items;

    private final Comparator<ListItemEBills> itemsComparator = new Comparator<ListItemEBills>() {
        @Override
        public int compare(final ListItemEBills lhs, final ListItemEBills rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    };

    private final int mLayoutResourceId;

    private final SimpleDateFormat sdfInvoice;

    private final SimpleDateFormat sdfOutput;

    private final SimpleDateFormat sdfDue;

    public ListEBillsAdapter(final Context ctx, final Activity a, final int layoutResourceId, final ArrayList<ListItemEBills> aItems) {
        items = aItems;
        context = ctx;
        mLayoutResourceId = layoutResourceId;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Collections.sort(items, itemsComparator);
        sdfInvoice = new SimpleDateFormat("MMM dd, yyyy");
        sdfOutput = new SimpleDateFormat(((App) ((Activity) context).getApplication()).getDateFormat());
        sdfDue = new SimpleDateFormat();
    }

    public void addAll(final ArrayList<ListItemEBills> items) {
        this.items.addAll(items);
        Collections.sort(items, itemsComparator);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    static class ViewHolder {

        public TextView title;

        public TextView date;

        public TextView code;

        public ImageView card;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(mLayoutResourceId, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) myConvertView.findViewById(R.id.ebill_title);
            viewHolder.date = (TextView) myConvertView.findViewById(R.id.ebill_date);
            viewHolder.code = (TextView) myConvertView.findViewById(R.id.ebill_code);
            viewHolder.card = (ImageView) myConvertView.findViewById(R.id.ebill_card);
            myConvertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) myConvertView.getTag();
        final ListItemEBills item = items.get(position);
        sdfDue.applyPattern(item.getEbillDateFormat());
        String invoiceDate = null;
        synchronized (sdfInvoice){
            try {
                invoiceDate = sdfInvoice.format(sdfOutput.parse(item.getInvoiceDate()));
            } catch (final ParseException e) {

                invoiceDate = item.getInvoiceDate();
                Log.w("ListEBillsAdapter", "Error parsing Ebill's invoice date.",e);

            }

            String dueDate = null;
            try {
                dueDate = sdfInvoice.format(sdfDue.parse(item.getDueDate()));
            } catch (final ParseException e) {

                dueDate = item.getDueDate();
                Log.w("ListEBillsAdapter", "Error parsing Ebill's due date.",e);
            }

            final String date = String.format("%s %s, %s %s", context.getString(R.string.ebill_date), invoiceDate, context.getString(R.string.ebill_due_date), dueDate);

            holder.title.setText(item.getTitle());
            holder.date.setText(date);
            holder.code.setText(item.getPayeeLast4Num());
            holder.card.setImageResource(item.getImgResource());
            FontChanger.changeFonts(myConvertView);
            return myConvertView;
        }
        
    }

    public void remove(final ListItemSelectable item) {
        items.remove(item);
        notifyDataSetChanged();
    }

}
