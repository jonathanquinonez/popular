package com.popular.android.mibanco.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Base abstract class to be implemented by list adapters
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    private final Context context;
    private List<T> data = Collections.emptyList();

    public BaseListAdapter(final Context context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public void updateDataset(final List<T> data) {
        if (data == null) {
            setData((List<T>) Collections.emptyList());
        } else {
            setData(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public T getItem(final int position) {
        return getData().get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    protected List<T> getData() {
        return data;
    }

    protected void setData(final List<T> data) {
        this.data = data;
    }

    protected Context getContext() {
        return context;
    }

    /**
     * General class that represents a row in a list
     */
    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(final View view, final int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
