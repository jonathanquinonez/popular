//package com.popular.android.mibanco.locator;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.OverlayItem;
//import java.util.ArrayList;
//
///**
// * The bank facility overlay item.
// */
//public class BankOverlayItem extends OverlayItem {
//
//    public static final int BANK_ITEM_ATM = 0;
//
//    public static final int BANK_ITEM_BRANCH = 1;
//
//    private int id = -1;
//
//    private ArrayList<BankOverlayItem> childrenAtms = new ArrayList<BankOverlayItem>();
//
//    private ArrayList<BankOverlayItem> childrenBranches = new ArrayList<BankOverlayItem>();
//
//    private String direction;
//
//    private Float distance;
//
//    private boolean isClustered;
//
//    private boolean isGroup;
//
//    private int type;
//
//    /**
//     * Instantiates a new my overlay item as a copy of an existing item.
//     *
//     * @param source a source BankOverlayItem to copy fields values from
//     */
//    public BankOverlayItem(final BankOverlayItem source) {
//        super(source.getPoint(), source.getTitle(), source.getSnippet());
//        type = source.getType();
//    }
//
//    /**
//     * Instantiates a new my overlay item.
//     *
//     * @param point the geo point
//     * @param title the title
//     * @param snippet the snippet
//     * @param type the type of an item
//     */
//    public BankOverlayItem(final GeoPoint point, final String title, final String snippet, final int type) {
//        super(point, title, snippet);
//        this.type = type;
//    }
//
//    public ArrayList<BankOverlayItem> getChildrenAtms() {
//        return childrenAtms;
//    }
//
//    public ArrayList<BankOverlayItem> getChildrenBranches() {
//        return childrenBranches;
//    }
//
//    public String getDirection() {
//        return direction;
//    }
//
//    public Float getDistance() {
//        return distance;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public boolean isClustered() {
//        return isClustered;
//    }
//
//    public boolean isGroup() {
//        return isGroup;
//    }
//
//    public void setChildrenAtms(final ArrayList<BankOverlayItem> childrenAtms) {
//        this.childrenAtms = childrenAtms;
//    }
//
//    public void setChildrenBranches(final ArrayList<BankOverlayItem> children) {
//        childrenBranches = children;
//    }
//
//    public void setClustered(final boolean isClustered) {
//        this.isClustered = isClustered;
//    }
//
//    public void setDirection(final String direction) {
//        this.direction = direction;
//    }
//
//    public void setDistance(final float distance) {
//        this.distance = distance;
//    }
//
//    public void setGroup(final boolean isGroup) {
//        this.isGroup = isGroup;
//    }
//
//    public void setType(final int type) {
//        this.type = type;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//}
