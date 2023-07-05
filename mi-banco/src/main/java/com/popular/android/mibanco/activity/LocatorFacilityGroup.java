//package com.popular.android.mibanco.activity;
//
//import android.content.Intent;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.adapter.FacilityGroupListAdapter;
//import com.popular.android.mibanco.base.BaseActivity;
//import com.popular.android.mibanco.locator.BankOverlayItem;
//import com.popular.android.mibanco.locator.LocatorHelper;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
///**
// * Shows detailed information about ATM or a branch. Gives user option to guide him to the facility using external Google Maps application.
// */
//public class LocatorFacilityGroup extends BaseActivity {
//
//    private ArrayList<BankOverlayItem> atms;
//    private ArrayList<BankOverlayItem> branches;
//    private ListView groupList;
//
//    private final Comparator<BankOverlayItem> itemsComparatorTitle = new Comparator<BankOverlayItem>() {
//        @Override
//        public int compare(final BankOverlayItem lhs, final BankOverlayItem rhs) {
//            return lhs.getTitle().compareTo(rhs.getTitle());
//        }
//    };
//
//    private final Comparator<BankOverlayItem> itemsDistanceComparator = new Comparator<BankOverlayItem>() {
//        @Override
//        public int compare(final BankOverlayItem lhs, final BankOverlayItem rhs) {
//            return lhs.getDistance().compareTo(rhs.getDistance());
//        }
//    };
//
//    private void loadCluster() {
//        final FacilityGroupListAdapter listAdapter = new FacilityGroupListAdapter(LocatorFacilityGroup.this);
//        if (application.getUserLocation() != null) {
//            final Location userLocation = application.getUserLocation();
//            for (final BankOverlayItem branch : branches) {
//                final Location targetLocation = new Location("A");
//                targetLocation.setLatitude(branch.getPoint().getLatitudeE6() / LocatorHelper.MILLION);
//                targetLocation.setLongitude(branch.getPoint().getLongitudeE6() / LocatorHelper.MILLION);
//                branch.setDistance(targetLocation.distanceTo(userLocation));
//                branch.setDirection(LocatorHelper.determineDirection(targetLocation, userLocation));
//            }
//            for (final BankOverlayItem atm : atms) {
//                final Location targetLocation = new Location("A");
//                targetLocation.setLatitude(atm.getPoint().getLatitudeE6() / LocatorHelper.MILLION);
//                targetLocation.setLongitude(atm.getPoint().getLongitudeE6() / LocatorHelper.MILLION);
//                atm.setDistance(targetLocation.distanceTo(userLocation));
//                atm.setDirection(LocatorHelper.determineDirection(targetLocation, userLocation));
//            }
//            Collections.sort(branches, itemsDistanceComparator);
//            Collections.sort(atms, itemsDistanceComparator);
//        } else {
//            Collections.sort(branches, itemsComparatorTitle);
//            Collections.sort(atms, itemsComparatorTitle);
//        }
//
//        if (branches.size() > 0) {
//            listAdapter.addItem(getString(R.string.branch));
//            for (final BankOverlayItem branch : branches) {
//                listAdapter.addItem(branch);
//            }
//        }
//
//        if (atms.size() > 0) {
//            listAdapter.addItem(getString(R.string.atm));
//            for (final BankOverlayItem atm : atms) {
//                listAdapter.addItem(atm);
//            }
//        }
//        groupList.setAdapter(listAdapter);
//    }
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.locator_group);
//
//        final BankOverlayItem facilityItem = application.getLastDisplayedGroup();
//        if (facilityItem == null) {
//            finish();
//            return;
//        }
//
//        branches = facilityItem.getChildrenBranches();
//        atms = facilityItem.getChildrenAtms();
//        groupList = (ListView) findViewById(R.id.group_list);
//        groupList.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
//                final FacilityGroupListAdapter adapter = (FacilityGroupListAdapter) ((ListView) parent).getAdapter();
//                final Object item = adapter.getItem(position);
//
//                if (item instanceof BankOverlayItem) {
//                    final BankOverlayItem facilityItem = (BankOverlayItem) item;
//
//                    final Intent intent = new Intent(LocatorFacilityGroup.this, LocatorFacilityDetails.class);
//                    intent.putExtra("title", facilityItem.getTitle());
//                    intent.putExtra("content", facilityItem.getSnippet());
//                    intent.putExtra("latitude", facilityItem.getPoint().getLatitudeE6() / LocatorHelper.MILLION);
//                    intent.putExtra("longitude", facilityItem.getPoint().getLongitudeE6() / LocatorHelper.MILLION);
//                    intent.putExtra("id", String.valueOf(facilityItem.getId()));
//                    intent.putExtra("type", facilityItem.getType());
//
//                    if (facilityItem.getDistance() != null) {
//                        intent.putExtra("distance", facilityItem.getDistance());
//                    }
//
//                    LocatorFacilityGroup.this.startActivity(intent);
//                }
//            }
//        });
//
//        loadCluster();
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(final Menu menu) {
//        menu.findItem(R.id.menu_settings).setVisible(false);
//        menu.findItem(R.id.menu_logout).setVisible(false);
//        menu.findItem(R.id.menu_locator).setVisible(false);
//        menu.findItem(R.id.menu_contact).setVisible(false);
//
//        return true;
//    }
//}
