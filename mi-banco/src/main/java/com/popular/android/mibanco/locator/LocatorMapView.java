//package com.popular.android.mibanco.locator;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.support.v4.content.ContextCompat;
//import android.util.AttributeSet;
//import android.util.SparseArray;
//import android.view.View;
//import android.widget.ProgressBar;
//
//import com.google.android.maps.MapView;
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.listener.ObjectListener;
//import com.popular.android.mibanco.task.BaseAsyncTask;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Class to manage the locator map view
// */
//public class LocatorMapView extends MapView {
//
//    class PlaceOverlaysTask extends BaseAsyncTask {
//
//        private List<BankOverlayItem> clusters;
//
//        private final SparseArray<List<BankOverlayItem>> locatorClustersCache;
//
//        private final int zoomLevel;
//
//        private long taskSerial;
//
//        public PlaceOverlaysTask(final Context context, final ObjectListener listener, final boolean showProgress, final int zoomLevel) {
//            super(context, listener, showProgress);
//            taskSerial = ++placeOverlaysTaskSerial;
//            this.zoomLevel = zoomLevel;
//            locatorClustersCache = ((App) ((Activity) context).getApplication()).getLocatorClustersCache();
//        }
//
//        @Override
//        protected Integer doInBackground(final Object... params) {
//            Thread.currentThread().setName("PlaceOverlaysTask");
//            if (zoomLevel < MIN_ZOOM_LEVEL || zoomLevel > MAX_ZOOM_LEVEL || getZoomLevel() != zoomLevel) {
//                return RESULT_FAILURE;
//            }
//
//            setProgressBarVisibility(View.VISIBLE);
//            if (locatorClustersCache.get(zoomLevel) == null) {
//                final List<BankOverlayItem> overlayItems = copyOverlays(sourceOverlayItems);
//
//                boolean markerFound = false;
//                BankOverlayItem startItem = null;
//                final List<BankOverlayItem> groups = new ArrayList<>();
//
//                do {
//                    markerFound = false;
//                    startItem = null;
//
//                    if (isCancelled()) {
//                        return RESULT_FAILURE;
//                    }
//
//                    int overlayItemsSize = overlayItems.size();
//                    for (int i = 0; i < overlayItemsSize; ++i) {
//                        if (isCancelled()) {
//                            return RESULT_FAILURE;
//                        }
//
//                        BankOverlayItem item = overlayItems.get(i);
//                        if (!overlayItems.get(i).isClustered()) {
//                            if (!markerFound) {
//                                markerFound = true;
//                                startItem = item;
//                            } else {
//                                item = overlayItems.get(i);
//                            }
//
//                            if (LocatorHelper.getOverLayItemDistance(startItem, item, LocatorMapView.this) <= MIN_MARKERS_DISTANCE_PIXELS) {
//                                item.setClustered(true);
//                                if (item.getType() == BankOverlayItem.BANK_ITEM_ATM) {
//                                    startItem.getChildrenAtms().add(item);
//                                } else {
//                                    startItem.getChildrenBranches().add(item);
//                                }
//                            }
//                        }
//                    }
//
//                    if (startItem != null) {
//                        final int atmCount = startItem.getChildrenAtms().size();
//                        final int branchCount = startItem.getChildrenBranches().size();
//
//                        if (branchCount + atmCount > 1) {
//                            startItem = createGroupItem(startItem);
//                        }
//                        groups.add(startItem);
//                    }
//                } while (markerFound);
//
//                clusters = groups;
//            } else {
//                clusters = locatorClustersCache.get(zoomLevel);
//            }
//
//            return RESULT_SUCCESS;
//        }
//
//        @Override
//        protected void onCancelled() {
//            if (taskSerial == placeOverlaysTaskSerial) {
//                setProgressBarVisibility(View.INVISIBLE);
//            }
//            super.onCancelled();
//        }
//
//        @Override
//        protected void onPostExecute(final Integer result) {
//            setProgressBarVisibility(View.INVISIBLE);
//            super.onPostExecute(result);
//            if (result == RESULT_SUCCESS && taskListener != null) {
//                if (clusters.size() > 0) {
//                    locatorClustersCache.append(zoomLevel, clusters);
//                }
//                ((ObjectListener) taskListener).done(clusters);
//            }
//        }
//
//        private void setProgressBarVisibility(final int visibility) {
//            if (progressBar != null) {
//                handler.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        progressBar.setVisibility(visibility);
//                    }
//                });
//            }
//        }
//    }
//
//    /**
//     * Start zoom level for the map view.
//     */
//    public static final int DEFAULT_MAP_ZOOM = 16;
//
//    private static final int MAP_LOAD_DELAY_MILIS = 250;
//
//    private static final int MAX_SUMMARY_TITLES = 5;
//
//    private static final int MAX_ZOOM_LEVEL = 19;
//
//    private static final int MIN_MARKERS_DISTANCE_PIXELS = 45;
//
//    private static final int MIN_ZOOM_LEVEL = 3;
//
//    private static long placeOverlaysTaskSerial = 0;
//
//    /** The marker's Drawable for ATMs. */
//    private final Drawable atmMarkerDrawable;
//
//    /** Branches. */
//    private BankFacilityBaloonOverlay baloonOverlayAtms;
//
//    /** Branches. */
//    private BankFacilityBaloonOverlay baloonOverlayBranches;
//
//    /** The marker's Drawable for branches. */
//    private final Drawable branchMarkerDrawable;
//
//    private Context context;
//
//    /** The map overlay. */
//    private BankLocationOverlay locationsOverlay;
//
//    private int oldZoomLevel = -1;
//
//    private List<BankOverlayItem> sourceOverlayItems = new ArrayList<>();
//
//    private PlaceOverlaysTask placeOverlaysTask;
//
//    private Handler handler;
//
//    private ProgressBar progressBar;
//
//    public LocatorMapView(final Context context, final AttributeSet attrs) {
//        super(context, attrs);
//
//        this.context = context;
//        handler = new Handler();
//
//        setClickable(true);
//
//        setBuiltInZoomControls(true);
//        getController().setZoom(DEFAULT_MAP_ZOOM);
//
//        atmMarkerDrawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_pin_red);
//        atmMarkerDrawable.setBounds(0, 0, atmMarkerDrawable.getIntrinsicWidth(), atmMarkerDrawable.getIntrinsicHeight());
//
//        branchMarkerDrawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_pin_blue);
//        branchMarkerDrawable.setBounds(0, 0, branchMarkerDrawable.getIntrinsicWidth(), branchMarkerDrawable.getIntrinsicHeight());
//    }
//
//    public void setProgressBar(ProgressBar progressBar) {
//        this.progressBar = progressBar;
//    }
//
//    private synchronized List<BankOverlayItem> copyOverlays(final List<BankOverlayItem> source) {
//        final List<BankOverlayItem> copy = new ArrayList<>(source.size());
//        for (final BankOverlayItem item : source) {
//            copy.add(new BankOverlayItem(item));
//        }
//
//        return copy;
//    }
//
//    private BankOverlayItem createGroupItem(final BankOverlayItem startItem) {
//        if (context == null) {
//            return null;
//        }
//
//        final int atmCount = startItem.getChildrenAtms().size();
//        final int branchCount = startItem.getChildrenBranches().size();
//
//        String branches = "";
//        if (branchCount > 0) {
//            if (branchCount == 1) {
//                branches = "1 " + context.getString(R.string.branch_singular);
//            } else {
//                branches = branchCount + " " + context.getString(R.string.branch_plural);
//            }
//        }
//
//        String atms = "";
//        if (atmCount > 0) {
//            if (atmCount == 1) {
//                atms = "1 " + context.getString(R.string.atm_singular);
//            } else {
//                atms = atmCount + " " + context.getString(R.string.atm_plural);
//            }
//        }
//
//        String title;
//        if (branchCount > 0 && atmCount > 0) {
//            title = branches + " " + context.getString(R.string.and) + " " + atms;
//        } else if (branchCount > 0) {
//            title = branches;
//        } else {
//            title = atms;
//        }
//
//        String snippet = "";
//        int titlesAdded = 0;
//        boolean hasMore = false;
//        for (int i = 0; i < branchCount; ++i) {
//            if (titlesAdded >= MAX_SUMMARY_TITLES) {
//                hasMore = true;
//                break;
//            }
//            snippet += startItem.getChildrenBranches().get(i).getTitle() + ", ";
//            ++titlesAdded;
//        }
//        for (int i = 0; i < atmCount; ++i) {
//            if (titlesAdded >= MAX_SUMMARY_TITLES) {
//                hasMore = true;
//                break;
//            }
//            snippet += startItem.getChildrenAtms().get(i).getTitle() + ", ";
//            ++titlesAdded;
//        }
//        snippet = snippet.substring(0, snippet.length() - 2);
//
//        if (titlesAdded == MAX_SUMMARY_TITLES && hasMore) {
//            snippet = snippet.concat("...");
//        }
//
//        final BankOverlayItem tempGroup = new BankOverlayItem(startItem.getPoint(), title, snippet,BankOverlayItem.BANK_ITEM_BRANCH);
//        tempGroup.setChildrenAtms(startItem.getChildrenAtms());
//        tempGroup.setChildrenBranches(startItem.getChildrenBranches());
//        tempGroup.setClustered(true);
//        tempGroup.setGroup(true);
//
//        return tempGroup;
//    }
//
//    /*
//     * Update the points at panned / zoom etc
//     */
//    @Override
//    public void dispatchDraw(final Canvas canvas) {
//        super.dispatchDraw(canvas);
//
//        if (getZoomLevel() < MIN_ZOOM_LEVEL) {
//            getController().setZoom(MIN_ZOOM_LEVEL);
//        } else if (getZoomLevel() > MAX_ZOOM_LEVEL) {
//            getController().setZoom(MAX_ZOOM_LEVEL);
//        }
//
//        if (getZoomLevel() != oldZoomLevel && sourceOverlayItems.size() > 0) {
//            placeOverlays();
//        }
//        oldZoomLevel = getZoomLevel();
//    }
//
//    public BankFacilityBaloonOverlay getBaloonOverlayAtms() {
//        return baloonOverlayAtms;
//    }
//
//    public BankFacilityBaloonOverlay getBaloonOverlayBranches() {
//        return baloonOverlayBranches;
//    }
//
//    public BankLocationOverlay getLocationsOverlay() {
//        return locationsOverlay;
//    }
//
//    public void placeOverlays() {
//        if (context != null && context instanceof Activity) {
//            if (baloonOverlayBranches != null) {
//                baloonOverlayBranches.hideAllBalloons();
//            }
//
//            if (baloonOverlayAtms != null) {
//                baloonOverlayAtms.hideAllBalloons();
//            }
//
//            baloonOverlayBranches = new BankFacilityBaloonOverlay(branchMarkerDrawable, LocatorMapView.this, context);
//            baloonOverlayBranches.setBalloonBottomOffset(branchMarkerDrawable.getIntrinsicHeight());
//
//            baloonOverlayAtms = new BankFacilityBaloonOverlay(atmMarkerDrawable, LocatorMapView.this, context);
//            baloonOverlayAtms.setBalloonBottomOffset(atmMarkerDrawable.getIntrinsicHeight());
//
//            new Timer().schedule(new TimerTask() {
//
//                @Override
//                public void run() {
//                    if (placeOverlaysTask != null) {
//                        placeOverlaysTask.cancel(false);
//                    }
//
//                    if (context != null && context instanceof Activity) {
//                        placeOverlaysTask = new PlaceOverlaysTask(context, new ObjectListener() {
//
//                            @SuppressWarnings("unchecked")
//                            @Override
//                            public void done(final Object data) {
//
//                                final List<BankOverlayItem> clusters = (ArrayList<BankOverlayItem>) data;
//
//                                for (final BankOverlayItem item : clusters) {
//                                    if (item.getChildrenBranches().size() == 0) {
//                                        baloonOverlayAtms.addOverlayItem(item);
//                                    } else {
//                                        baloonOverlayBranches.addOverlayItem(item);
//                                    }
//                                }
//
//                                baloonOverlayBranches.populateOverlay();
//                                baloonOverlayAtms.populateOverlay();
//
//                                handler.post(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        LocatorMapView.this.getOverlays().clear();
//                                        LocatorMapView.this.getOverlays().add(locationsOverlay);
//                                        LocatorMapView.this.getOverlays().add(baloonOverlayBranches);
//                                        LocatorMapView.this.getOverlays().add(baloonOverlayAtms);
//                                        LocatorMapView.this.invalidate();
//                                    }
//                                });
//                            }
//                        }, false, LocatorMapView.this.getZoomLevel());
//                        placeOverlaysTask.execute();
//                    }
//                }
//            }, MAP_LOAD_DELAY_MILIS);
//        }
//    }
//
//    public void setBaloonOverlayAtms(final BankFacilityBaloonOverlay baloonOverlayAtms) {
//        this.baloonOverlayAtms = baloonOverlayAtms;
//    }
//
//    public void setBaloonOverlayBranches(final BankFacilityBaloonOverlay baloonOverlayBranches) {
//        this.baloonOverlayBranches = baloonOverlayBranches;
//    }
//
//    public void setLocationsOverlay(final BankLocationOverlay locationsOverlay) {
//        this.locationsOverlay = locationsOverlay;
//        getOverlays().add(locationsOverlay);
//    }
//
//    public void setStartOverlays(final List<BankOverlayItem> overlayItems) {
//        sourceOverlayItems = overlayItems;
//        placeOverlays();
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//        if (context == null) {
//            if (placeOverlaysTask != null) {
//                placeOverlaysTask.cancel(false);
//            }
//        }
//    }
//}
