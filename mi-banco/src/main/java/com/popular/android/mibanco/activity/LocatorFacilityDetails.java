//package com.popular.android.mibanco.activity;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.base.BaseActivity;
//import com.popular.android.mibanco.listener.LocationsListener;
//import com.popular.android.mibanco.locator.BankOverlayItem;
//import com.popular.android.mibanco.locator.LocationManager;
//import com.popular.android.mibanco.locator.LocatorHelper;
//import com.popular.android.mibanco.object.BankLocation;
//import com.popular.android.mibanco.object.BankLocationDetail;
//import com.popular.android.mibanco.util.BPAnalytics;
//import com.popular.android.mibanco.util.PermissionsManagerUtils;
//import com.popular.android.mibanco.util.Utils;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.List;
//
///**
// * Shows detailed information about ATM or a branch. Gives user option to guide him to the facility using external Google Maps application.
// */
//public class LocatorFacilityDetails extends BaseActivity {
//
//    private static final int ZOOM_LEVEL = 16;
//    private static final int layoutWidth = 1000;
//    private static final int layoutHeight = 200;
//
//    private ImageView mapImageView;
//    private Location facilityLocation;
//    private RelativeLayout imageLayout;
//    private ProgressBar progressBarMap;
//
//    private int placeType;
//    private String branchServices;
//    private boolean hasPermission = false;
//
//    //region ACTIVITY LIFECYCLE METHODS ****
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.locator_facility_details);
//
//        mapImageView = (ImageView) findViewById(R.id.mapImage);
//        mapImageView.setImageResource(R.drawable.welcome_default);
//        progressBarMap = (ProgressBar) findViewById(R.id.progressBarMap);
//        imageLayout = (RelativeLayout) findViewById(R.id.relativeImageLayout);
//        hasPermission = PermissionsManagerUtils.missingPermissions(this).size() == 0;
//
//        final TextView distanceTV = (TextView) findViewById(R.id.distance_text);
//        final TextView tvType = (TextView) findViewById(R.id.header_location_type);
//        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutDistance);
//
//        final Location userLocation = App.getApplicationInstance().getUserLocation();
//
//
//        String title = getIntent().getStringExtra("title");
//        String locationId = getIntent().getStringExtra("id");
//        int locationType = getIntent().getIntExtra("type", 0);
//        String content = getIntent().getStringExtra("content");
//        double latitude = getIntent().getDoubleExtra("latitude", 0);
//        double longitude = getIntent().getDoubleExtra("longitude", 0);
//        branchServices = getIntent().getStringExtra("branchServices");
//        placeType = getIntent().getIntExtra("type", BankOverlayItem.BANK_ITEM_BRANCH);
//
//        if(branchServices == null){
//            branchServices = "";
//        }
//
//        float distance = 0;
//        if (App.getApplicationInstance().getUserLocation() != null) {
//            distance = getIntent().getFloatExtra("distance", 0);
//        }
//
//        if(locationType == BankOverlayItem.BANK_ITEM_BRANCH && locationId != null && !locationId.equals("") && !locationId.equals("-1"))
//            updateLocationDetails(String.valueOf(locationId));
//
//
//        ((TextView) findViewById(R.id.title_text)).setText(title);
//        ((TextView) findViewById(R.id.content_text)).setText(content);
//        tvType.setText(placeType == BankOverlayItem.BANK_ITEM_ATM ? getResources().getString(R.string.atm) : getResources().getString(R.string.branch));
//
//
//        facilityLocation = new Location("A");
//        facilityLocation.setLongitude(longitude);
//        facilityLocation.setLatitude(latitude);
//
//        if (userLocation != null) {
//            linearLayout.setVisibility(View.VISIBLE);
//            distanceTV.setText(LocatorHelper.getStringDistance(distance, LocatorHelper.determineDirection(facilityLocation, userLocation), false, this));
//        } else {
//            linearLayout.setVisibility(View.GONE);
//        }
//        setListeners();
//
//        progressBarMap.setVisibility(View.GONE);
//        mapImageView.setVisibility(View.GONE);
//        if(hasPermission) {
//            LocationManager locationManager = new LocationManager(LocatorFacilityDetails.this);
//            App.getApplicationInstance().setLocationManager(locationManager);
//            if(mapImageView.getVisibility() == View.GONE) {
//                initializeMapImage();
//            }
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        hasPermission = PermissionsManagerUtils.missingPermissions(this).size() == 0;
//        if(hasPermission) {
//            if(App.getApplicationInstance().getLocationManager() == null) {
//                LocationManager locationManager = new LocationManager(LocatorFacilityDetails.this);
//                App.getApplicationInstance().setLocationManager(locationManager);
//            }
//
//            App.getApplicationInstance().getLocationManager().connect(this, null);
//        }
//        BPAnalytics.onStartSession(this);
//    }
//
//    @Override
//    protected void onStop() {
//        if(hasPermission) {
//            App.getApplicationInstance().getLocationManager().disconnect();
//        }
//        BPAnalytics.onEndSession(this);
//        super.onStop();
//    }
//
//    //endregion
//
//    private void updateLocationDetails(String locationId) {
//        if(application != null && application.getAsyncTasksManager() != null) {
//            application.getAsyncTasksManager().getLocationDetails(LocatorFacilityDetails.this, locationId, new LocationsListener() {
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
//
//                    if (branchDetail != null) {
//                        TextView hoursText = (TextView) findViewById(R.id.hours_text);
//
//                        String telephone = "";
//                        if (branchDetail.getPhone() != null && !branchDetail.getPhone().equals(""))
//                            telephone = "\n" + getResources().getString(R.string.phone_number) + ": " + branchDetail.getPhone() + "\n";
//
//                        String hours = LocatorHelper.getFacilityHours(branchDetail, getResources());
//
//                        hoursText.setText(Utils.concatenateStrings(new String[]{telephone, hours, branchServices}));
//                    }
//
//                }
//
//                @Override
//                public void updateLocations(final List<BankLocation> atms, final List<BankLocation> branches) {
//                }
//            });
//        }
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
//
//    //region LOCATION HELPER METHODS ****
//
//    private void setListeners() {
//        final Context mContext = this;
//        findViewById(R.id.btnGuideMe).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                String stringUri;
//
//                final Location myLocation = App.getApplicationInstance().getUserLocation();
//                if (myLocation != null) {
//                    BPAnalytics.logEvent(BPAnalytics.EVENT_LOCATOR_ASKED_FOR_DIRECTIONS);
//                    stringUri = "http://maps.google.com/maps?saddr=" + String.valueOf(myLocation.getLatitude()) + "+" + String.valueOf(myLocation.getLongitude()) + "&daddr="
//                            + String.valueOf(facilityLocation.getLatitude()) + "+" + String.valueOf(facilityLocation.getLongitude());
//                    Utils.openExternalUrl(LocatorFacilityDetails.this, stringUri);
//                } else {
//                    if(PermissionsManagerUtils.missingPermissions(mContext).size() == 0){
//                        Toast.makeText(LocatorFacilityDetails.this, R.string.my_location_permission, Toast.LENGTH_LONG).show();
//                    }else {
//                        Toast.makeText(LocatorFacilityDetails.this, R.string.my_location_error, Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });
//    }
//
//    private void initializeMapImage()
//    {
//        imageLayout.post(new Runnable() {
//
//            @Override
//            public void run() {
//                fetchMapImage();
//            }
//        });
//    }
//
//    private void fetchMapImage() {
//        if(mapImageView.getVisibility() == View.GONE){
//            progressBarMap.setVisibility(View.VISIBLE);
//            mapImageView.setVisibility(View.VISIBLE);
//        }
//        String urlString = "https://maps.googleapis.com/maps/api/staticmap?center=" + String.valueOf(facilityLocation.getLatitude()) + "," + String.valueOf(facilityLocation.getLongitude()) + "&zoom="
//                + ZOOM_LEVEL + "&size=" + layoutWidth + "x" + layoutHeight+  "&markers=color:" + (placeType == BankOverlayItem.BANK_ITEM_ATM ? "red" : "blue") + "|"
//                + String.valueOf(facilityLocation.getLatitude()) + "," + String.valueOf(facilityLocation.getLongitude()) + "&sensor=false&scale=2";
//
//        final Location myLocation = ((App) getApplication()).getUserLocation();
//        if (myLocation != null) {
//            try {
//                urlString += "&markers=size:small|color:black|label:" + URLEncoder.encode(getString(R.string.current_location), "UTF-8") + "|" + String.valueOf(myLocation.getLatitude()) + ","
//                        + String.valueOf(myLocation.getLongitude());
//            } catch (final UnsupportedEncodingException e) {
//                Log.w("LocatorFacility", e);
//            }
//        }
//
//        progressBarMap.setVisibility(View.VISIBLE);
//        mapImageView.setVisibility(View.INVISIBLE);
//        ImageLoader.getInstance().displayImage(urlString, mapImageView, new ImageLoadingListener() {
//
//            @Override
//            public void onLoadingStarted(String arg0, View arg1) {
//                progressBarMap.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//                progressBarMap.setVisibility(View.GONE);
//                mapImageView.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//                progressBarMap.setVisibility(View.GONE);
//                mapImageView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingCancelled(String arg0, View arg1) {
//                mapImageView.setVisibility(View.INVISIBLE);
//                progressBarMap.setVisibility(View.GONE);
//            }
//        });
//    }
//
//    //endregion
//}
