package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.object.ListItemSelectable;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

/**
 * Adapter class to manage data in remembered amounts list
 */
public class MyAmountsAdapter extends BaseAdapter implements OnClickListener {

    private int checkedIndex = -1;

    private String checkedName;

    private final Context context;

    private final ArrayList<ListItemSelectable> items;

    private final SimpleListener onDeleteListener;

    private final LayoutInflater inflater;

    public MyAmountsAdapter(final Context context, final ArrayList<ListItemSelectable> items, final App app, final SimpleListener onDeleteListener) {
        this.items = items;
        this.onDeleteListener = onDeleteListener;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(final ArrayList<ListItemSelectable> items) {
        this.items.addAll(items);
    }

    public int getCheckedAmount() {
        if (checkedIndex != -1 && checkedIndex >= 0 && checkedIndex < items.size()) {
            return items.get(checkedIndex).getId();
        }

        return -1;
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

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(R.layout.list_item_amounts, null);
        }

        final ListItemSelectable item = items.get(position);

        final RadioButton radioAmountName = (RadioButton) myConvertView.findViewById(R.id.item_title);
        final ImageButton buttonDelete = (ImageButton) myConvertView.findViewById(R.id.button_delete);
        ((TextView) myConvertView.findViewById(R.id.item_amount)).setText(item.getContent());
        radioAmountName.setText(item.getTitle());
        radioAmountName.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonDelete.setTag(position);

        if (item.getTitle().equals(checkedName)) {
            radioAmountName.setChecked(true);
            checkedIndex = position;
        } else {
            radioAmountName.setChecked(false);
        }

        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }

    public void remove(final int position) {
        items.remove(position);
        checkedIndex = -1;
        notifyDataSetChanged();
    }

    public void remove(final ListItemSelectable item) {
        items.remove(item);
        checkedIndex = -1;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_title) {
            final RadioButton button = (RadioButton) v;
            if (button.isChecked()) {
                notifyDataSetChanged();
                checkedName = button.getText().toString();
            }
        } else if (v.getId() == R.id.button_delete) {
            int position = (Integer) v.getTag();
            if (Utils.removeFromMyAmounts(context.getApplicationContext(), ((ListItemSelectable) getItem(position)).getTitle())) {
                remove(position);
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.amount_removed), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.amount_cant_be_removed), Toast.LENGTH_LONG).show();
            }

            if (onDeleteListener != null) {
                if (items.size() <= 0) {
                    onDeleteListener.done();
                }
            }
        }
    }
}
