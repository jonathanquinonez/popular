//package com.popular.android.mibanco.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.location.Location;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.ViewConfiguration;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.listener.LocationsListener;
//import com.popular.android.mibanco.listener.ObjectListener;
//import com.popular.android.mibanco.listener.ResponderListener;
//import com.popular.android.mibanco.locator.BankLocationOverlay;
//import com.popular.android.mibanco.locator.BankOverlayItem;
//import com.popular.android.mibanco.locator.LocatorHelper;
//import com.popular.android.mibanco.locator.LocatorMapView;
//import com.popular.android.mibanco.object.BankLocation;
//import com.popular.android.mibanco.object.BankLocationDetail;
//import com.popular.android.mibanco.task.BaseAsyncTask;
//import com.popular.android.mibanco.util.PermissionsManagerUtils;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * The Map view visible on Locator's "Map" tab.
// */
//public class LocatorMap extends MapActivity {
//
//    /**
//     * Task for loading overlay points for ATMs and branches.
//     */
//    public class LoadPointsTask extends BaseAsyncTask {
//
//        /**
//         * The ATMs.
//         */
//        private final List<BankLocation> atms;
//
//        /**
//         * The branches.
//         */
//        private final List<BankLocation> branches;
//
//        /**
//         * The ATMs overlays.
//         */
//        private final List<BankOverlayItem> overlayItemsAtms = new ArrayList<>();
//
//        /**
//         * The branches overlays.
//         */
//        private final List<BankOverlayItem> overlayItemsBranches = new ArrayList<>();
//
//        /**
//         * The type.
//         */
//        private final int type;
//
//        /**
//         * Instantiates a new load points task.
//         *
//         * @param context      the context
//         * @param listener     the listener called on task finish
//         * @param showProgress should we show progress?
//         * @param aAtms        the ATMs list of BankLocation objects
//         * @param aBranches    the branches list of BankLocation objects
//         * @param aType        the type of points
//         */
//        public LoadPointsTask(final Context context, final ResponderListener listener, final boolean showProgress, final List<BankLocation> aAtms, final List<BankLocation> aBranches,
//                              final int aType) {
//            super(context, listener, showProgress);
//            atms = aAtms;
//            branches = aBranches;
//            type = aType;
//        }
//
//
//        @Override
//        protected Integer doInBackground(final Object... params) {
//            Thread.currentThread().setName("LoadPointsTask");
//
//            int atmsBegin = 0;
//            int branchesBegin = 0;
//
//            if (type == 1) {
//                atmsBegin++;
//            } else if (type == 2) {
//                branchesBegin++;
//            }
//
//            for (int i = atmsBegin; i < atms.size(); i++) {
//                final BankLocation facility = atms.get(i);
//                final BankOverlayItem item = new BankOverlayItem(LocatorHelper.getPoint(facility.getLatitude(), facility.getLongitude()), facility.getName(),
//                        LocatorHelper.getFacilityContent(facility), BankOverlayItem.BANK_ITEM_ATM);
//
//                item.setId(facility.getId());
//                overlayItemsAtms.add(item);
//            }
//            for (int i = branchesBegin; i < branches.size(); i++) {
//                final BankLocation facility = branches.get(i);
//                final BankOverlayItem item = new BankOverlayItem(LocatorHelper.getPoint(facility.getLatitude(), facility.getLongitude()), facility.getName(),
//                        LocatorHelper.getFacilityContent(facility), BankOverlayItem.BANK_ITEM_BRANCH);
//
//                item.setId(facility.getId());
//                overlayItemsBranches.add(item);
//            }
//
//            isGettingLocations = false;
//
//            return RESULT_SUCCESS;
//        }
//
//        @Override
//        protected void onPostExecute(final Integer result) {
//            super.onPostExecute(result);
//            overlayItems.clear();
//            overlayItems.addAll(overlayItemsAtms);
//            overlayItems.addAll(overlayItemsBranches);
//            mapView.setStartOverlays(overlayItems);
//        }
//    }
//
//    /**
//     * The first run?
//     */
//    private boolean firstRun = true;
//
//    /**
//     * Are locations being retrieved?
//     */
//    private boolean isGettingLocations;
//
//    /**
//     * The application instance.
//     */
//    private App application;
//
//    /**
//     * The items comparator.
//     */
//    private final Comparator<BankLocation> itemsComparator = new Comparator<BankLocation>() {
//        @Override
//        public int compare(final BankLocation lhs, final BankLocation rhs) {
//            return lhs.getDistance().compareTo(rhs.getDistance());
//        }
//    };
//
//    /**
//     * The MapView control.
//     */
//    private LocatorMapView mapView;
//
//    /**
//     * All overlay items.
//     */
//    private final List<BankOverlayItem> overlayItems = new ArrayList<>();
//
//    /**
//     * The user location.
//     */
//    private Location userLocation;
//
//    /**
//     * Gets the points.
//     */
//    private void getPoints() {
//        isGettingLocations = true;
//        if(application != null && application.getAsyncTasksManager() != null) {
//            application.getAsyncTasksManager().getLocations(LocatorMap.this, new LocationsListener() {
//
//                @Override
//                public void updateATMs(final List<BankLocation> atms) {
//                }
//
//                @Override
//                public void updateBranches(final List<BankLocation> branches) {
//                }
//
//                @Override
//                public void updateBranchDetail(BankLocationDetail branchDetail) {
//                }
//
//                @Override
//                public void updateLocations(final List<BankLocation> atms, final List<BankLocation> branches) {
//
//                    Location myLocation = mapView.getLocationsOverlay().getLastFix();
//                    if (myLocation == null && userLocation != null) {
//                        myLocation = userLocation;
//                    }
//                    if (myLocation != null) {
//                        final Location locA = new Location("A");
//                        for (final BankLocation facility : atms) {
//                            locA.setLatitude(facility.getLatitude());
//                            locA.setLongitude(facility.getLongitude());
//                            facility.setDistance(locA.distanceTo(myLocation));
//                        }
//                        final Location locB = new Location("B");
//                        for (final BankLocation facility : branches) {
//                            locB.setLatitude(facility.getLatitude());
//                            locB.setLongitude(facility.getLongitude());
//                            facility.setDistance(locB.distanceTo(myLocation));
//                        }
//
//                        Collections.sort(atms, itemsComparator);
//                        Collections.sort(branches, itemsComparator);
//                    }
//
//                    final int type = 0;
//                    new LoadPointsTask(LocatorMap.this, new ResponderListener() {
//
//                        @Override
//                        public void responder(final String responderName, final Object data) {
//                        }
//
//                        @Override
//                        public void sessionHasExpired() {
//                            application.reLogin(LocatorMap.this);
//                        }
//
//                    }, false, atms, branches, type).execute();
//
//                }
//            });
//        }
//    }
//
//    /**
//     * Initializes the map.
//     */
//    private void initMap() {
//        final boolean hasPermission = (PermissionsManagerUtils.missingPermissions(this).size() == 0);
//        mapView.setLocationsOverlay(new BankLocationOverlay(LocatorMap.this, mapView, new ObjectListener() {
//
//            @Override
//            public void done(final Object data) {
//                if(hasPermission) {
//                    userLocation = (Location) data;
//                    ((App) getApplication()).setUserLocation(userLocation);
//                }
//            }
//        }));
//
//        getPoints();
//
//        mapView.getOverlays().add(mapView.getLocationsOverlay());
//        mapView.getLocationsOverlay().enableCompass();
//        mapView.getLocationsOverlay().enableMyLocation();
//
//        final double popularCenterBranchLatitude = Double.parseDouble(getString(R.string.popular_center_latitude));
//        final double popularCenterBranchLongitude = Double.parseDouble(getString(R.string.popular_center_longitude));
//        mapView.getController().setCenter(new GeoPoint((int) (popularCenterBranchLatitude * LocatorHelper.MILLION), (int) (popularCenterBranchLongitude * LocatorHelper.MILLION)));
//
//        if(hasPermission) {
//            Toast.makeText(this, getResources().getString(R.string.try_locate), Toast.LENGTH_LONG).show();
//        }
//        mapView.getLocationsOverlay().runOnFirstFix(new Runnable() {
//            @Override
//            public void run() {
//                mapView.getController().setZoom(LocatorMapView.DEFAULT_MAP_ZOOM);
//                mapView.getController().setCenter(mapView.getLocationsOverlay().getMyLocation());
//            }
//        });
//    }
//
//    @Override
//    protected boolean isRouteDisplayed() {
//        return false;
//    }
//
//    /**
//     * Called when the activity is first created.
//     *
//     * @param savedInstanceState the saved instance state
//     */
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        application = (App) getApplication();
//
//        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        RelativeLayout mapLayout = (RelativeLayout) inflater.inflate(R.layout.locator_map, null);
//        mapView = (LocatorMapView) mapLayout.findViewById(R.id.locatorMapView);
//        mapView.setProgressBar((ProgressBar) mapLayout.findViewById(R.id.progressBar));
//        setContentView(mapLayout);
//
//        initMap();
//        firstRun = false;
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return getParent().onCreateOptionsMenu(menu);
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && ViewConfiguration.get(this).hasPermanentMenuKey()) {
//            menu.findItem(R.id.menu_overflow).setVisible(false);
//        }
//
//        menu.findItem(R.id.menu_locator).setVisible(false);
//        menu.findItem(R.id.menu_logout).setVisible(false);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return getParent().onOptionsItemSelected(item);
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.setContext(null);
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.getLocationsOverlay().disableCompass();
//        mapView.getLocationsOverlay().disableMyLocation();
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mapView.getLocationsOverlay() == null) {
//            return;
//        }
//        mapView.getLocationsOverlay().enableCompass();
//        mapView.getLocationsOverlay().enableMyLocation();
//
//        if (mapView.getLocationsOverlay().getMyLocation() != null && !firstRun && !isGettingLocations) {
//            getPoints();
//        }
//    }
//}
