//package com.popular.android.mibanco.activity;
//
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.MiBancoConstants;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.adapter.LocationsAdapter;
//import com.popular.android.mibanco.base.BaseActivity;
//import com.popular.android.mibanco.listener.AsyncTaskListener;
//import com.popular.android.mibanco.listener.LocationsListener;
//import com.popular.android.mibanco.locator.BankOverlayItem;
//import com.popular.android.mibanco.locator.LocatorHelper;
//import com.popular.android.mibanco.object.BankLocation;
//import com.popular.android.mibanco.object.BankLocationDetail;
//import com.popular.android.mibanco.util.PermissionsManagerUtils;
//import com.popular.android.mibanco.view.CustomFastScrollView;
//
//import java.util.List;
//
///**
// * Activity that manages the locator facility list (atm and branches)
// */
//public class LocatorFacilityList extends BaseActivity implements OnItemClickListener {
//
//    private boolean dataLoaded = false;
//    private boolean isBranchesList;
//    private boolean hasPermission = false;
//
//    private ProgressBar progressBar;
//    private LocationsAdapter adapter;
//    private CustomFastScrollView fastScrollView;
//
//    //region ACTIVITY LIFECYCLE METHODS ****
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.locator_facility_list);
//
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar_update);
//        fastScrollView = (CustomFastScrollView) findViewById(R.id.fast_scroll_view);
//        adapter = new LocationsAdapter(LocatorFacilityList.this, R.layout.list_item_locator);
//        isBranchesList = getIntent().getBooleanExtra(MiBancoConstants.LOCATOR_BRANCHES_KEY, false);
//
//        ListView facilitiesList = (ListView) findViewById(R.id.list_facilities);
//        if(facilitiesList != null) {
//            facilitiesList.setAdapter(adapter);
//            facilitiesList.setOnItemClickListener(this);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (App.getApplicationInstance().getLocationManager() != null) {
//            App.getApplicationInstance().getLocationManager().setContext(this);
//            App.getApplicationInstance().getLocationManager().connect(this, new AsyncTaskListener() {
//
//                @Override
//                public void onSuccess(Object data) {
//                    if (data != null && App.getApplicationInstance().getUserLocation() == null) {
//                        App.getApplicationInstance().setUserLocation((Location) data);
//                        updateLocations();
//                    }
//                }
//
//                @Override
//                public boolean onError(Throwable error) {
//                    return false;
//                }
//
//                @Override
//                public void onCancelled() {
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        hasPermission = PermissionsManagerUtils.missingPermissions(this).size() == 0;
////        if(hasPermission && App.getApplicationInstance().getLocationManager()== null){
////            App.getApplicationInstance().setLocationManager(new com.popular.android.mibanco.locator.LocationManager(this));
////        }
//        if (App.getApplicationInstance().getLocationManager() != null) {
//            App.getApplicationInstance().getLocationManager().setContext(this);
//        }
//
//        updateLocations();
//    }
//
//    @Override
//    protected void onStop() {
//        if (App.getApplicationInstance().getLocationManager() != null) {
//            App.getApplicationInstance().getLocationManager().disconnect();
//        }
//        super.onStop();
//    }
//
//
//    //endregion
//
//    //region MENU OPTIONS MANAGEMENT ***
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return getParent().onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && ViewConfiguration.get(this).hasPermanentMenuKey()) {
//            menu.findItem(R.id.menu_overflow).setVisible(false);
//        }
//
//        menu.findItem(R.id.menu_locator).setVisible(false);
//        menu.findItem(R.id.menu_logout).setVisible(false);
//
//        if(getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return getParent().onOptionsItemSelected(item);
//    }
//
//    //endregion
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        final BankLocation item = (BankLocation) adapterView.getAdapter().getItem(position);
//        final Intent intent = new Intent(LocatorFacilityList.this, LocatorFacilityDetails.class);
//
//        intent.putExtra("title", (item.getName()));
//        intent.putExtra("content", LocatorHelper.getFacilityContent(item));
//        intent.putExtra("latitude", item.getLatitude());
//        intent.putExtra("longitude", item.getLongitude());
//        intent.putExtra("id", String.valueOf(item.getId()));
//        intent.putExtra("branchServices", LocatorHelper.getFacilityServices(item, getResources()));
//
//        if (item.getDistance() != null) {
//            intent.putExtra("distance", item.getDistance());
//        }
//
//        if (isBranchesList) {
//            intent.putExtra("type", BankOverlayItem.BANK_ITEM_BRANCH);
//        } else {
//            intent.putExtra("type", BankOverlayItem.BANK_ITEM_ATM);
//        }
//        startActivity(intent);
//    }
//
//    // region LIST UPDATES ***
//
//    private void updateList(final List<BankLocation> facilities) {
//        if (!dataLoaded && adapter != null && fastScrollView != null) {
//            adapter.setData(facilities);
//            dataLoaded = true;
//        }
//    }
//
//    private void updateLocations() {
//        if(application != null && application.getAsyncTasksManager()!= null) {
//            application.getAsyncTasksManager().getLocations(LocatorFacilityList.this, new LocationsListener() {
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
//                    if (isBranchesList) {
//                        updateList(branches);
//                    } else {
//                        updateList(atms);
//                    }
//                    new UpdateUserLocation().execute();
//                }
//            });
//        }
//    }
//
//    private class UpdateUserLocation extends AsyncTask<Void, Void, Integer> {
//
//        private static final int RESULT_SUCCESS = 0;
//        private static final int RESULT_FAILURE_LOCATION_NOT_AVAILABLE = 1;
//        private static final int RESULT_FAILURE_LOCATION_SERVICES_DISABLED = 2;
//
//        @Override
//        protected Integer doInBackground(Void... params) {
//            LocationManager locationManager = null;
//            if(hasPermission) {
//                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            }
//
//            if (!hasPermission || !adapter.updateUserLocation()) {
//                if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    return RESULT_FAILURE_LOCATION_SERVICES_DISABLED;
//                }
//                return RESULT_FAILURE_LOCATION_NOT_AVAILABLE;
//            }
//            return RESULT_SUCCESS;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onCancelled(Integer result) {
//            progressBar.setVisibility(View.GONE);
//        }
//
//        @Override
//        protected void onPostExecute(Integer result) {
//            progressBar.setVisibility(View.GONE);
//            adapter.notifyDataSetChanged();
//            fastScrollView.listItemsChanged(adapter);
//
//            if(hasPermission) {
//                if (result == RESULT_FAILURE_LOCATION_NOT_AVAILABLE && App.getApplicationInstance().getUserLocation() == null) {
//                    Toast.makeText(LocatorFacilityList.this, R.string.location_unavailable, Toast.LENGTH_SHORT).show();
//                } else if (result == RESULT_FAILURE_LOCATION_SERVICES_DISABLED) {
//                    Toast.makeText(LocatorFacilityList.this, R.string.locator_services_unavailable, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//
//    //endregion
//}
