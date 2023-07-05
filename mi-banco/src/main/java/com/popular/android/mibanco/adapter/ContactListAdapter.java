package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.object.ContactItem;
import com.popular.android.mibanco.object.ContactItem.ContactType;
import com.popular.android.mibanco.util.FontChanger;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Adapter class to manage data in contact list
 */
public class ContactListAdapter extends BaseAdapter {

    /**
     * Static class that represents the data in a row
     */
    public static class ViewHolder {

        public TextView content;

        public TextView description;

        public TextView header;
    }

    private static final int TYPE_HEADER = 1;

    private static final int TYPE_ITEM = 0;

    private static final int TYPE_MAX_COUNT = TYPE_HEADER + 1;

    private final int headerLayoutResourceId;

    private final TreeSet<Integer> headers = new TreeSet<Integer>();

    private final LayoutInflater inflater;

    private final int itemLayoutResourceId;

    private final ArrayList<ContactItem> items = new ArrayList<ContactItem>();

    public ContactListAdapter(final Context context, final int itemLayoutResourceId, final int headerLayoutResourceId) {
        this.itemLayoutResourceId = itemLayoutResourceId;
        this.headerLayoutResourceId = headerLayoutResourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final ContactItem item) {
        items.add(item);
        if (item.getType() == ContactType.CONTACT_TYPE_HEADER) {
            headers.add(items.size() - 1);
        }
        super.notifyDataSetChanged();
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
    public int getItemViewType(final int position) {
        return headers.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        final ContactItem item = (ContactItem) getItem(position);
        View myConvertView = convertView;

        final int type = getItemViewType(position);
        if (myConvertView == null) {
            holder = new ViewHolder();
            if (item.getType() == ContactType.CONTACT_TYPE_HEADER) {
                myConvertView = inflater.inflate(headerLayoutResourceId, null);
                holder.header = (TextView) myConvertView.findViewById(R.id.title_text);
            } else {
                myConvertView = inflater.inflate(itemLayoutResourceId, null);
                holder.description = (TextView) myConvertView.findViewById(R.id.contactus_description_text);
                holder.content = (TextView) myConvertView.findViewById(R.id.contactus_content_text);
            }
            myConvertView.setTag(holder);
        } else {
            holder = (ViewHolder) myConvertView.getTag();
        }

        switch (type) {
        case TYPE_ITEM:
            holder.description.setText(item.getDescription());
            holder.content.setText(item.getContent());
            break;
        case TYPE_HEADER:
            holder.header.setText(item.getContent());
            break;
        default:
            break;
        }

        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public boolean isEnabled(final int position) {
        return !headers.contains(position);
    }
}
