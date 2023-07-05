//package com.popular.android.mibanco.locator;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.location.Location;
//
//import com.google.android.maps.MapView;
//import com.google.android.maps.OverlayItem;
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.activity.LocatorFacilityDetails;
//import com.popular.android.mibanco.activity.LocatorFacilityGroup;
//import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Sites overlay.
// */
//public class BankFacilityBaloonOverlay extends BalloonItemizedOverlay<OverlayItem> {
//
//    /** The Activity context. */
//    private final Context context;
//
//    /** The list of overlay items. */
//    private final List<BankOverlayItem> itemOverlays = new ArrayList<BankOverlayItem>();
//
//    private final MapView mapView;
//
//    /**
//     * Instantiates a new BankFacilityBaloonOverlay.
//     *
//     * @param marker the marker's Drawable
//     * @param mapView the MapView
//     * @param context the context
//     */
//    public BankFacilityBaloonOverlay(final Drawable marker, final MapView mapView, final Context context) {
//        super(boundCenterBottom(marker), mapView);
//        this.mapView = mapView;
//        this.context = context;
//        populate();
//    }
//
//    public void addOverlayItem(final BankOverlayItem overlay) {
//        itemOverlays.add(overlay);
//    }
//
//    /**
//     * Adds the locations.
//     *
//     * @param overlayItems the overlay items list
//     */
//    public void addOverlayItems(final ArrayList<BankOverlayItem> overlayItems) {
//        itemOverlays.addAll(overlayItems);
//        setLastFocusedIndex(-1);
//        populate();
//    }
//
//    public void clear() {
//        itemOverlays.clear();
//        mapView.removeAllViews();
//        setLastFocusedIndex(-1);
//        populate();
//    }
//
//
//    @Override
//    protected OverlayItem createItem(final int i) {
//        return itemOverlays.get(i);
//    }
//
//
//    @Override
//    protected boolean onBalloonTap(final int index, final OverlayItem item) {
//        hideAllBalloons();
//        final BankOverlayItem facilityItem = itemOverlays.get(index);
//        if (!facilityItem.isGroup()) {
//            final Intent intent = new Intent(context, LocatorFacilityDetails.class);
//            intent.putExtra("title", facilityItem.getTitle());
//            intent.putExtra("content", facilityItem.getSnippet());
//            intent.putExtra("latitude", facilityItem.getPoint().getLatitudeE6() / LocatorHelper.MILLION);
//            intent.putExtra("longitude", facilityItem.getPoint().getLongitudeE6() / LocatorHelper.MILLION);
//            intent.putExtra("type", facilityItem.getType());
//
//            final Location userLocation = ((App) ((Activity) context).getApplication()).getUserLocation();
//            if (userLocation != null) {
//                final Location locA = new Location("A");
//                locA.setLatitude(facilityItem.getPoint().getLatitudeE6() / LocatorHelper.MILLION);
//                locA.setLongitude(facilityItem.getPoint().getLongitudeE6() / LocatorHelper.MILLION);
//                intent.putExtra("distance", locA.distanceTo(userLocation));
//            }
//
//            context.startActivity(intent);
//        } else {
//            ((App) ((Activity) context).getApplication()).setLastDisplayedGroup(facilityItem);
//            final Intent intent = new Intent(context, LocatorFacilityGroup.class);
//            context.startActivity(intent);
//        }
//
//        return true;
//    }
//
//    public void populateOverlay() {
//        setLastFocusedIndex(-1);
//        populate();
//    }
//
//
//    @Override
//    public int size() {
//        return itemOverlays.size();
//    }
//}
