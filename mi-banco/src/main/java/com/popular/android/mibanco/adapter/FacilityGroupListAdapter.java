//package com.popular.android.mibanco.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.locator.BankOverlayItem;
//import com.popular.android.mibanco.locator.LocatorHelper;
//import com.popular.android.mibanco.object.BankLocation;
//import com.popular.android.mibanco.util.FontChanger;
//
//import java.util.ArrayList;
//import java.util.TreeSet;
//
///**
// * Adapter class to manage data for branches and atms list
// */
//public class FacilityGroupListAdapter extends BaseAdapter {
//
//    private static final int TYPE_HEADER = 1;
//
//    private static final int TYPE_ITEM = 0;
//
//    private static final int TYPE_MAX_COUNT = TYPE_HEADER + 1;
//
//    private final Context context;
//
//    private final TreeSet<Integer> headers = new TreeSet<Integer>();
//
//    private final LayoutInflater inflater;
//
//    private final ArrayList<Object> items = new ArrayList<Object>();
//
//    public FacilityGroupListAdapter(final Context context) {
//        this.context = context;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    public void addItem(final Object item) {
//        items.add(item);
//        if (item instanceof String) {
//            headers.add(items.size() - 1);
//        }
//        super.notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return items.size();
//    }
//
//    @Override
//    public Object getItem(final int position) {
//        return items.get(position);
//    }
//
//    @Override
//    public long getItemId(final int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemViewType(final int position) {
//        return headers.contains(position) ? TYPE_HEADER : TYPE_ITEM;
//    }
//
//    static class ViewHolder {
//
//        public TextView header;
//
//        public TextView title;
//
//        public TextView content;
//
//        public TextView distance;
//
//        public View linearLayout;
//    }
//
//    @Override
//    public View getView(final int position, final View convertView, final ViewGroup parent) {
//        final Object item = getItem(position);
//        View myConvertView = convertView;
//
//        if (myConvertView == null) {
//            ViewHolder viewHolder = new ViewHolder();
//            if (item instanceof BankOverlayItem) {
//                myConvertView = inflater.inflate(R.layout.list_item_locator_group, null);
//
//                final BankOverlayItem facilityItem = (BankOverlayItem) item;
//                if (facilityItem.getDistance() != null) {
//                    viewHolder.distance = (TextView) myConvertView.findViewById(R.id.distance_text);
//                } else {
//                    viewHolder.linearLayout = myConvertView.findViewById(R.id.linearLayoutDistance);
//                }
//
//                viewHolder.title = (TextView) myConvertView.findViewById(R.id.title_text);
//                viewHolder.content = (TextView) myConvertView.findViewById(R.id.content_text);
//            } else {
//                myConvertView = inflater.inflate(R.layout.list_item_locator_group_header, null);
//                viewHolder.header = (TextView) myConvertView.findViewById(R.id.header_text);
//            }
//            myConvertView.setTag(viewHolder);
//        }
//
//        ViewHolder holder = (ViewHolder) myConvertView.getTag();
//        if (item instanceof BankOverlayItem) {
//            final BankOverlayItem facilityItem = (BankOverlayItem) item;
//            if (facilityItem.getDistance() != null) {
//                holder.distance.setText(LocatorHelper.getStringDistance(facilityItem.getDistance(), facilityItem.getDirection(), false, context));
//            } else {
//                holder.linearLayout.setVisibility(View.GONE);
//            }
//
//            holder.title.setText(facilityItem.getTitle());
//
//            BankLocation location = new BankLocation();
//            location.setStreet1(facilityItem.getSnippet());
//            location.setName(facilityItem.getTitle());
//            holder.content.setText(LocatorHelper.getFacilityContent(location));
//        } else {
//            holder.header.setText((String) item);
//        }
//
//        FontChanger.changeFonts(myConvertView);
//
//        return myConvertView;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return TYPE_MAX_COUNT;
//    }
//
//    @Override
//    public boolean isEnabled(final int position) {
//        return !headers.contains(position);
//    }
//}
