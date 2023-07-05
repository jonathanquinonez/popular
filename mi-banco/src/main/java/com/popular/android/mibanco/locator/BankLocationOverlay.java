//package com.popular.android.mibanco.locator;
//
//import android.content.Context;
//import android.location.Location;
//
//import com.google.android.maps.MapView;
//import com.google.android.maps.MyLocationOverlay;
//import com.popular.android.mibanco.listener.ObjectListener;
//
///**
// * Bank location overlay.
// */
//public class BankLocationOverlay extends MyLocationOverlay {
//
//    /** The listener to notify about location changes. */
//    private final ObjectListener listener;
//
//    /**
//     * Instantiates a new BankLocationOverlay.
//     *
//     * @param context the context
//     * @param mapView the MapView
//     * @param listener the listener
//     */
//    public BankLocationOverlay(final Context context, final MapView mapView, final ObjectListener listener) {
//        super(context, mapView);
//        this.listener = listener;
//    }
//
//
//    @Override
//    public synchronized void onLocationChanged(final Location location) {
//        if (listener != null) {
//            listener.done(location);
//        }
//        super.onLocationChanged(location);
//    }
//
//
//    @Override
//    public void onProviderDisabled(final String provider) {
//        super.onProviderDisabled(provider);
//    }
//}
