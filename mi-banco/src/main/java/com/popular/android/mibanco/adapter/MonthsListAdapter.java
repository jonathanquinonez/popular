package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.FontChanger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Adapter class to manage data in months list
 */
public class MonthsListAdapter extends BaseAdapter {

    private static final int MONTHS_TO_SHOW = 12;

    private final LayoutInflater inflater;

    private final ArrayList<String> items = new ArrayList<String>(13);

    public MonthsListAdapter(final Context context) {
        for (int i = 0; i <= MONTHS_TO_SHOW; ++i) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, i);
            final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.SPINNER_MONTH_YEAR_FORMAT);
            items.add(sdf.format(calendar.getTime()));
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        if (items.size() > 0 && items.size() - 1 >= position) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(R.layout.list_item_months, null);
        }

        final String item = items.get(position);
        ((TextView) myConvertView.findViewById(R.id.title_text)).setText(item);

        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }
}
