//package com.popular.android.mibanco.adapter;
//
//import android.content.Context;
//import android.location.Location;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.LinearLayout;
//import android.widget.SectionIndexer;
//import android.widget.TextView;
//
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.locator.LocatorHelper;
//import com.popular.android.mibanco.object.BankLocation;
//import com.popular.android.mibanco.util.FontChanger;
//import com.popular.android.mibanco.util.Function;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * Adapter class to manage data Locations list
// */
//public class LocationsAdapter extends BaseAdapter implements SectionIndexer {
//
//    private final Context context;
//    private LayoutInflater inflater;
//    private List<BankLocation> items = new ArrayList<>();
//    private final Location tempLocation = new Location("A");
//    private final int mLayoutResourceId;
//    private SectionIndexer sectionIndexer;
//    private String[] sectionsArray;
//    private Location popularCenterLocation;
//
//    private final Comparator<BankLocation> itemsDistanceComparator = new Comparator<BankLocation>() {
//        @Override
//        public int compare(final BankLocation lhs, final BankLocation rhs) {
//            if (lhs.getDistance() < rhs.getDistance()) {
//                return -1;
//            } else if (lhs.getDistance() == rhs.getDistance()) {
//                return 0;
//            } else {
//                return 1;
//            }
//        }
//    };
//
//    public LocationsAdapter(final Context ctx, final int layoutResourceId) {
//        context = ctx;
//        mLayoutResourceId = layoutResourceId;
//
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        popularCenterLocation = new Location("A");
//        popularCenterLocation.setLatitude(Double.parseDouble(context.getString(R.string.popular_center_latitude)));
//        popularCenterLocation.setLongitude(Double.parseDouble(context.getString(R.string.popular_center_longitude)));
//
//        if (App.getApplicationInstance().getUserLocation() != null) {
//            getSectionIndexer();
//        }
//    }
//
//    public void setData(final List<BankLocation> items) {
//        this.items = items;
//    }
//
//    public boolean updateUserLocation() {
//        if (App.getApplicationInstance().getUserLocation() != null) {
//            setDistances(App.getApplicationInstance().getUserLocation());
//            try {
//                Collections.sort(items, itemsDistanceComparator);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            refreshSections();
//            return true;
//        } else {
//            setDistances(popularCenterLocation);
//            try {
//                Collections.sort(items, itemsDistanceComparator);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//    }
//
//    private SectionIndexer createSectionIndexer(final List<BankLocation> locations) {
//        return createSectionIndexer(locations, new Function<BankLocation, String>() {
//
//            @Override
//            public String apply(final BankLocation input) {
//                final Location loc = new Location("A");
//                loc.setLatitude(input.getLatitude());
//                loc.setLongitude(input.getLongitude());
//                return LocatorHelper.getStringDistance(input.getDistance(), LocatorHelper.determineDirection(loc, App.getApplicationInstance().getUserLocation()), false, context);
//            }
//        });
//    }
//
//    private SectionIndexer createSectionIndexer(final List<BankLocation> locations, final Function<BankLocation, String> sectionFunction) {
//        final List<String> sections = new ArrayList<String>();
//        final List<Integer> sectionsToPositions = new ArrayList<Integer>();
//        final List<Integer> positionsToSections = new ArrayList<Integer>();
//        int sectionsSize = sections.size();
//        if (App.getApplicationInstance().getUserLocation() != null) {
//            int locationsSize = locations.size();
//            for (int i = 0; i < locationsSize; i++) {
//                final BankLocation location = locations.get(i);
//                final String section = sectionFunction.apply(location);
//
//                if (sectionsSize == 0 || !sections.get(sectionsSize - 1).equals(section)) {
//                    sections.add(section);
//                    sectionsToPositions.add(i);
//                }
//                positionsToSections.add(sectionsSize - 1);
//            }
//
//            sectionsArray = new String[sectionsSize];
//            for (int i = 0; i < sectionsSize; i++) {
//                sectionsArray[i] = sections.get(i);
//            }
//        }
//
//        return new SectionIndexer() {
//
//            @Override
//            public int getPositionForSection(final int section) {
//                return sectionsToPositions.get(section);
//            }
//
//            @Override
//            public int getSectionForPosition(final int position) {
//                return positionsToSections.get(position);
//            }
//
//            @Override
//            public Object[] getSections() {
//                return sectionsArray;
//            }
//        };
//    }
//
//    public Context getContext() {
//        return context;
//    }
//
//    @Override
//    public int getCount() {
//        return items.size();
//    }
//
//    @Override
//    public BankLocation getItem(final int position) {
//        return items.get(position);
//    }
//
//    @Override
//    public long getItemId(final int position) {
//        return position;
//    }
//
//    @Override
//    public int getPositionForSection(final int section) {
//        return getSectionIndexer().getPositionForSection(section);
//    }
//
//    @Override
//    public int getSectionForPosition(final int position) {
//        return getSectionIndexer().getSectionForPosition(position);
//    }
//
//    private SectionIndexer getSectionIndexer() {
//        if (sectionIndexer == null) {
//            sectionIndexer = createSectionIndexer(items);
//        }
//        return sectionIndexer;
//    }
//
//    @Override
//    public Object[] getSections() {
//        return getSectionIndexer().getSections();
//    }
//
//    public String[] getSectionsArray() {
//        return sectionsArray;
//    }
//
//    static class ViewHolder {
//
//        public TextView title;
//        public TextView content;
//        public TextView distance;
//        public LinearLayout linearLayout;
//    }
//
//    @Override
//    public View getView(final int position, final View convertView, final ViewGroup parent) {
//        View myConvertView = convertView;
//        if (myConvertView == null) {
//            myConvertView = inflater.inflate(mLayoutResourceId, null);
//
//            ViewHolder viewHolder = new ViewHolder();
//            viewHolder.title = (TextView) myConvertView.findViewById(R.id.title_text);
//            viewHolder.content = (TextView) myConvertView.findViewById(R.id.content_text);
//            viewHolder.distance = (TextView) myConvertView.findViewById(R.id.distance_text);
//            viewHolder.linearLayout = (LinearLayout) myConvertView.findViewById(R.id.linearLayoutDistance);
//            myConvertView.setTag(viewHolder);
//        }
//
//        BankLocation item = getItem(position);
//        ViewHolder holder = (ViewHolder) myConvertView.getTag();
//        holder.title.setText(item.getName());
//        holder.content.setText(LocatorHelper.getFacilityContent(item));
//        if (App.getApplicationInstance().getUserLocation() != null) {
//            tempLocation.setLatitude(items.get(position).getLatitude());
//            tempLocation.setLongitude(items.get(position).getLongitude());
//            if (item.getDistance() != null && item.getDirection() != null) {
//                holder.linearLayout.setVisibility(View.VISIBLE);
//                holder.distance.setText(LocatorHelper.getStringDistance(item.getDistance(), item.getDirection(), false, context));
//            }
//        } else {
//            holder.linearLayout.setVisibility(View.INVISIBLE);
//        }
//
//        FontChanger.changeFonts(myConvertView);
//
//        return myConvertView;
//    }
//
//    public void refreshSections() {
//        sectionIndexer = null;
//        getSectionIndexer();
//    }
//
//    public void setDistances(Location userLocation) {
//        int itemsSize = items.size();
//        for (int i = 0; i < itemsSize; ++i) {
//            final BankLocation locationTo = items.get(i);
//            if (tempLocation != null && locationTo != null) {
//                tempLocation.setLatitude(locationTo.getLatitude());
//                tempLocation.setLongitude(locationTo.getLongitude());
//                locationTo.setDistance(tempLocation.distanceTo(userLocation));
//                locationTo.setDirection(LocatorHelper.determineDirection(tempLocation, userLocation));
//            }
//        }
//    }
//}
