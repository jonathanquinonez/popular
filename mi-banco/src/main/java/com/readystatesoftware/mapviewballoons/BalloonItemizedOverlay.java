//package com.readystatesoftware.mapviewballoons;
//
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup.LayoutParams;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.ItemizedOverlay;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;
//import com.popular.android.mibanco.R;
//
//import java.util.List;
//
///**
// * An abstract extension of ItemizedOverlay for displaying an information balloon upon screen-tap of each marker overlay.
// *
// * @author Jeff Gilfelt
// */
//public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> {
//
//    private static final long BALLOON_INFLATION_TIME = 300;
//    private static Runnable finishBalloonInflation = new Runnable() {
//        @Override
//        public void run() {
//            isInflating = false;
//        }
//    };
//    private static Handler handler = new Handler();
//    private static boolean isInflating=false;
//
//    public static boolean isInflating() {
//        return isInflating;
//    }
//
//    private BalloonOverlayView<Item> balloonView;
//    private View clickRegion;
//    private View closeRegion;
//    private int currentFocusedIndex;
//    private Item currentFocusedItem;
//    private final MapView mapView;
//    final MapController mc;
//
//    private boolean showClose = true;
//    private boolean showDisclosure;
//    private boolean snapToCenter = true;
//
//    private int viewOffset;
//
//    /**
//     * Create a new BalloonItemizedOverlay
//     *
//     * @param defaultMarker - A bounded Drawable to be drawn on the map for each item in the overlay.
//     * @param mapView - The view upon which the overlay items are to be drawn.
//     */
//    public BalloonItemizedOverlay(final Drawable defaultMarker, final MapView mapView) {
//        super(defaultMarker);
//        this.mapView = mapView;
//        viewOffset = 0;
//        mc = mapView.getController();
//    }
//
//    /**
//     * Creates and displays the balloon overlay by recycling the current balloon or by inflating it from xml.
//     *
//     * @return true if the balloon was recycled false otherwise
//     */
//    private boolean createAndDisplayBalloonOverlay() {
//        boolean isRecycled;
//        if (balloonView == null) {
//            balloonView = createBalloonOverlayView();
//            clickRegion = balloonView.findViewById(R.id.balloon_inner_layout);
//            clickRegion.setOnTouchListener(createBalloonTouchListener());
//            closeRegion = balloonView.findViewById(R.id.balloon_close);
//            if (closeRegion != null) {
//                if (!showClose) {
//                    closeRegion.setVisibility(View.GONE);
//                } else {
//                    closeRegion.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(final View v) {
//                            hideBalloon();
//                        }
//                    });
//                }
//            }
//            if (showDisclosure && !showClose) {
//                final View v = balloonView.findViewById(R.id.balloon_disclosure);
//                if (v != null) {
//                    v.setVisibility(View.VISIBLE);
//                }
//            }
//            isRecycled = false;
//        } else {
//            isRecycled = true;
//        }
//
//        balloonView.setVisibility(View.GONE);
//
//        final List<Overlay> mapOverlays = mapView.getOverlays();
//        if (mapOverlays.size() > 1) {
//            hideOtherBalloons(mapOverlays);
//        }
//
//        if (currentFocusedItem != null) {
//            balloonView.setData(currentFocusedItem);
//        }
//
//        final GeoPoint point = currentFocusedItem.getPoint();
//        final MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.BOTTOM_CENTER);
//        params.mode = MapView.LayoutParams.MODE_MAP;
//
//        balloonView.setVisibility(View.VISIBLE);
//
//        if (isRecycled) {
//            balloonView.setLayoutParams(params);
//        } else {
//            mapView.addView(balloonView, params);
//        }
//
//        return isRecycled;
//    }
//
//    /**
//     * Creates the balloon view. Override to create a sub-classed view that can populate additional sub-views.
//     */
//    protected BalloonOverlayView<Item> createBalloonOverlayView() {
//        return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
//    }
//
//    /**
//     * Sets the onTouchListener for the balloon being displayed, calling the overridden {@link #onBalloonTap} method.
//     */
//    private OnTouchListener createBalloonTouchListener() {
//        return new OnTouchListener() {
//
//            float startX;
//            float startY;
//
//            @Override
//            public boolean onTouch(final View v, final MotionEvent event) {
//
//                final View l = ((View) v.getParent()).findViewById(R.id.rootView);
//                final Drawable d = l.getBackground();
//
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    final int[] states = { android.R.attr.state_pressed };
//                    if (d.setState(states)) {
//                        d.invalidateSelf();
//                    }
//                    startX = event.getX();
//                    startY = event.getY();
//                    return true;
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    final int newStates[] = {};
//                    if (d.setState(newStates)) {
//                        d.invalidateSelf();
//                    }
//                    if (Math.abs(startX - event.getX()) < 40 && Math.abs(startY - event.getY()) < 40) {
//                        // call overridden method
//                        onBalloonTap(currentFocusedIndex, currentFocusedItem);
//                    }
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        };
//    }
//
//    public int getBalloonBottomOffset() {
//        return viewOffset;
//    }
//
//
//    @Override
//    public Item getFocus() {
//        return currentFocusedItem;
//    }
//
//    /**
//     * Expose map view to subclasses. Helps with creation of balloon views.
//     */
//    protected MapView getMapView() {
//        return mapView;
//    }
//
//    public void hideAllBalloons() {
//        if (!isInflating) {
//            final List<Overlay> mapOverlays = mapView.getOverlays();
//            if (mapOverlays.size() > 1) {
//                hideOtherBalloons(mapOverlays);
//            }
//            hideBalloon();
//        }
//    }
//
//    /**
//     * Sets the visibility of this overlay's balloon view to GONE and unfocus the item.
//     */
//    public void hideBalloon() {
//        if (balloonView != null) {
//            balloonView.setVisibility(View.GONE);
//        }
//        currentFocusedItem = null;
//    }
//
//    /**
//     * Hides the balloon view for any other BalloonItemizedOverlay instances that might be present on the MapView.
//     *
//     * @param overlays - list of overlays (including this) on the MapView.
//     */
//    private void hideOtherBalloons(final List<Overlay> overlays) {
//
//        for (final Overlay overlay : overlays) {
//            if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
//                ((BalloonItemizedOverlay<?>) overlay).hideBalloon();
//            }
//        }
//
//    }
//
//    /**
//     * Override this method to perform actions upon an item being tapped before its balloon is displayed.
//     *
//     * @param index - The index of the item tapped.
//     */
//    protected void onBalloonOpen(final int index) {
//    }
//
//    /**
//     * Override this method to handle a "tap" on a balloon. By default, does nothing and returns false.
//     *
//     * @param index - The index of the item whose balloon is tapped.
//     * @param item - The item whose balloon is tapped.
//     * @return true if you handled the tap, otherwise false.
//     */
//    protected boolean onBalloonTap(final int index, final Item item) {
//        return false;
//    }
//
//
//    @Override
//    // protected final boolean onTap(int index) {
//    public final boolean onTap(final int index) {
//
//        handler.removeCallbacks(finishBalloonInflation);
//        isInflating = true;
//        handler.postDelayed(finishBalloonInflation, BALLOON_INFLATION_TIME);
//
//        currentFocusedIndex = index;
//        currentFocusedItem = createItem(index);
//        setLastFocusedIndex(index);
//
//        onBalloonOpen(index);
//        createAndDisplayBalloonOverlay();
//
//        if (snapToCenter) {
//            mc.animateTo(currentFocusedItem.getPoint());
//        }
//
//        return true;
//    }
//
//    /**
//     * Set the horizontal distance between the marker and the bottom of the information balloon. The default is 0 which works well for center bounded
//     * markers. If your marker is center-bottom bounded, call this before adding overlay items to ensure the balloon hovers exactly above the marker.
//     *
//     * @param pixels - The padding between the center point and the bottom of the information balloon.
//     */
//    public void setBalloonBottomOffset(final int pixels) {
//        viewOffset = pixels;
//    }
//
//
//    @Override
//    public void setFocus(final Item item) {
//        super.setFocus(item);
//        currentFocusedIndex = getLastFocusedIndex();
//        currentFocusedItem = item;
//        if (currentFocusedItem == null) {
//            hideBalloon();
//        } else {
//            createAndDisplayBalloonOverlay();
//        }
//    }
//
//    public void setShowClose(final boolean showClose) {
//        this.showClose = showClose;
//    }
//
//    public void setShowDisclosure(final boolean showDisclosure) {
//        this.showDisclosure = showDisclosure;
//    }
//
//    public void setSnapToCenter(final boolean snapToCenter) {
//        this.snapToCenter = snapToCenter;
//    }
//}
