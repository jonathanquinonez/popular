//package com.popular.android.mibanco.locator;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Point;
//import android.location.Location;
//import android.util.Log;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.MiBancoConstants;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.object.BankLocation;
//import com.popular.android.mibanco.object.BankLocationDetail;
//import com.popular.android.mibanco.util.Utils;
//
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//
///**
// * LocatorHelper provides obtaining user location functionality.
// */
//public final class LocatorHelper {
//
//    /** One million constant. */
//    public static final double MILLION = 1E6;
//
//    /** Static map request timeout value in milliseconds. */
//    public static final int STATIC_MAP_FETCH_TIMEOUT_MILLIS = 10000;
//
//    /**
//     * Converts meters to miles.
//     *
//     * @param meters meters to covert to miles
//     * @return a conversion result as a float value
//     */
//    public static float convertMetersToMiles(final float meters) {
//        final Double milesDouble = meters * 0.00062137;
//        return Float.parseFloat(milesDouble.toString());
//    }
//
//    /**
//     * Determines a cardinal direction towards a given target location.
//     *
//     * @param targetLocation the target point
//     * @param myLocation the point of origin
//     * @return the String indicating a cardinal direction initial (N, E, S, W)
//     */
//    public static String determineDirection(final Location targetLocation, final Location myLocation) {
//        final double longtitudeSubstractionResult = targetLocation.getLongitude() - myLocation.getLongitude();
//        final double latitudeSubstractionResult = targetLocation.getLatitude() - myLocation.getLatitude();
//        if (Math.abs(longtitudeSubstractionResult) > Math.abs(latitudeSubstractionResult)) {
//            return longtitudeSubstractionResult <= 0 ? "W" : "E";
//        } else {
//            return latitudeSubstractionResult <= 0 ? "S" : "N";
//        }
//    }
//
//    /**
//     * Finds the distance between two points on a map.
//     *
//     * @param x1 x coordinate of the first point
//     * @param y1 y coordinate of the first point
//     * @param x2 x coordinate of the second point
//     * @param y2 y coordinate of the second point
//     * @return the distance between given points
//     *
//     */
//    private static double findDistance(final float x1, final float y1, final float x2, final float y2) {
//        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
//    }
//
//    private static String getFacilityAddress3(final BankLocation item) {
//        return (item.getCity() == null || item.getCity().length() < 1 ? "" : item.getCity()
//                + (item.getState() == null || item.getState().length() < 1 ? "" : (", "+item.getState() + " ")))
//                + (item.getZipCode() == null || item.getZipCode().length() < 1 ? "" : item.getZipCode());
//    }
//
//    public static String getFacilityContent(final BankLocation item) {
//        String facilityAddress = getFacilityAddress3(item);
//        facilityAddress = (facilityAddress == null || facilityAddress.length() < 1 ? "" : Utils.concatenateStrings(new String[]{", ",facilityAddress}));
//        String street1= item.getStreet1() == null || item.getStreet1().length() < 1 ? "" : item.getStreet1();
//        String street2 = (item.getStreet2() == null || item.getStreet2().length() < 1 ? "" : Utils.concatenateStrings(new String[]{", ",item.getStreet2()}));
//
//        return (Utils.concatenateStrings(new String[]{street1,street2,facilityAddress}));
//    }
//
//
//    public static String getFacilityHours(BankLocationDetail branchDetail, Resources resources){
//
//        String hours = MiBancoConstants.NEW_LINE;
//        String titleSeparator = ": ";
//
//        if(branchDetail.getMondayOpeningTime() == null
//                || branchDetail.getTuesdayOpeningTime() == null
//                || branchDetail.getWednesdayOpeningTime() == null
//                || branchDetail.getThursdayOpeningTime() == null
//                || branchDetail.getFridayOpeningTime() == null
//                || branchDetail.getSaturdayOpeningTime() == null
//                || branchDetail.getSundayOpeningTime() == null){
//
//            return "";
//        }
//
//
//        if((branchDetail.getMondayOpeningTime().equals(branchDetail.getTuesdayOpeningTime())
//                && branchDetail.getTuesdayOpeningTime().equals(branchDetail.getWednesdayOpeningTime())
//                && branchDetail.getWednesdayOpeningTime().equals(branchDetail.getThursdayOpeningTime())
//                && branchDetail.getThursdayOpeningTime().equals(branchDetail.getFridayOpeningTime()))
//                &&
//
//                (branchDetail.getMondayClosingTime().equals(branchDetail.getTuesdayClosingTime())
//                        && branchDetail.getTuesdayClosingTime().equals(branchDetail.getWednesdayClosingTime())
//                        && branchDetail.getWednesdayClosingTime().equals(branchDetail.getThursdayClosingTime())
//                        && branchDetail.getThursdayClosingTime().equals(branchDetail.getFridayClosingTime()))){
//
//            hours = hours
//                    + resources.getString(R.string.week_monday)+" "
//                    + resources.getString(R.string.week_to)+" "
//                    + resources.getString(R.string.week_friday)+titleSeparator
//                    + hourRange(branchDetail.getMondayOpeningTime(),branchDetail.getMondayClosingTime(), resources)+MiBancoConstants.NEW_LINE;
//
//        }else{
//
//            hours = hours
//                    + resources.getString(R.string.week_monday)+titleSeparator
//                    + hourRange(branchDetail.getMondayOpeningTime(),branchDetail.getMondayClosingTime(), resources)+MiBancoConstants.NEW_LINE
//                    + resources.getString(R.string.week_tuesday)+titleSeparator
//                    + hourRange(branchDetail.getTuesdayOpeningTime(),branchDetail.getTuesdayClosingTime(), resources)+MiBancoConstants.NEW_LINE
//                    + resources.getString(R.string.week_wednesday)+titleSeparator
//                    + hourRange(branchDetail.getWednesdayOpeningTime(),branchDetail.getWednesdayClosingTime(), resources)+MiBancoConstants.NEW_LINE
//                    + resources.getString(R.string.week_thursday)+titleSeparator
//                    + hourRange(branchDetail.getThursdayOpeningTime(),branchDetail.getThursdayClosingTime(), resources)+MiBancoConstants.NEW_LINE
//                    + resources.getString(R.string.week_friday)+titleSeparator
//                    + hourRange(branchDetail.getFridayOpeningTime(),branchDetail.getFridayClosingTime(), resources)+MiBancoConstants.NEW_LINE;
//        }
//
//        String weekendHours = resources.getString(R.string.week_saturday)+titleSeparator
//                + hourRange(branchDetail.getSaturdayOpeningTime(),branchDetail.getSaturdayClosingTime(), resources)+MiBancoConstants.NEW_LINE
//                + resources.getString(R.string.week_sunday)+titleSeparator
//                + hourRange(branchDetail.getSundayOpeningTime(),branchDetail.getSundayClosingTime(), resources);
//
//        String holidays = "";
//        if(branchDetail.getHolidayNameEnglish() != null && !branchDetail.getHolidayNameEnglish().equals("")){
//            holidays = MiBancoConstants.NEW_LINE+MiBancoConstants.NEW_LINE+resources.getString(R.string.next_holiday)+titleSeparator;
//
//            if(App.getApplicationInstance().getLanguage() != null
//                    && App.getApplicationInstance().getLanguage().equals(MiBancoConstants.SPANISH_LANGUAGE_CODE)){
//                SimpleDateFormat spanishFormat = new SimpleDateFormat(MiBancoConstants.SPANISH_DATE_FORMAT);
//                holidays = holidays + branchDetail.getHolidayNameSpanish()+","
//                        +spanishFormat.format(branchDetail.getHolidayStartDate())+", "
//                        +hourRange(branchDetail.getHolidayOpeningTime(), branchDetail.getHolidayClosingTime(), resources);
//            }else{
//                SimpleDateFormat englishFormat = new SimpleDateFormat(MiBancoConstants.ENGLISH_DATE_FORMAT);
//                holidays = holidays + branchDetail.getHolidayNameEnglish()+", "
//                        +englishFormat.format(branchDetail.getHolidayStartDate())+", "
//                        +hourRange(branchDetail.getHolidayOpeningTime(), branchDetail.getHolidayClosingTime(), resources);
//            }
//
//
//        }
//
//        return (hours + weekendHours + holidays);
//    }
//
//    /**
//     * Method to parse services for display
//     * @param bankLocation The BankLocation object
//     * @param resources Resources
//     * @return The services string to be displayed
//     */
//    public static String getFacilityServices(BankLocation bankLocation, Resources resources)
//    {
//        String services = "";
//        String titleSeparator = ": ";
//        if(bankLocation.isPopularOne() || bankLocation.isPopularMortgage()|| bankLocation.isPopularSecurities()
//                || bankLocation.isCommercialLine()|| bankLocation.isNightDeposit() || bankLocation.isAutoBanco()){
//
//
//            if(bankLocation.isPopularOne()){
//                services = resources.getString(R.string.branch_service_popularone);
//            }
//
//            if(bankLocation.isPopularMortgage()){
//                if(!services.equals(""))
//                    services = services+", ";
//                services = services + resources.getString(R.string.branch_service_popularmortgage);
//            }
//
//            if(bankLocation.isPopularSecurities()){
//                if(!services.equals(""))
//                    services = services+", ";
//                services = services + resources.getString(R.string.branch_service_popularsecurities);
//            }
//            if(bankLocation.isCommercialLine()){
//                if(!services.equals(""))
//                    services = services+", ";
//                services = services + resources.getString(R.string.branch_service_commercial);
//            }
//
//            if(bankLocation.isNightDeposit()){
//                if(!services.equals(""))
//                    services = services+", ";
//                services = services + resources.getString(R.string.branch_service_nightdeposit);
//            }
//
//            if(bankLocation.isAutoBanco()){
//                if(!services.equals(""))
//                    services = services+", ";
//                services = services + resources.getString(R.string.branch_service_autobanco);
//            }
//        }
//
//        if(!services.equals(""))
//            services = MiBancoConstants.NEW_LINE+resources.getString(R.string.services)+titleSeparator +services;
//
//        return services;
//    }
//
//    private static String hourRange(String hourStart, String hourEnd, Resources resources)
//    {
//        if (!hourStart.matches(".*\\d+.*"))
//            return resources.getString(R.string.week_closed);
//
//        return (hourStart+" - "+hourEnd);
//    }
//
//
//    /**
//     * Finds the distance between the projection of GeoPoints in the mapView.
//     *
//     * @param item1 first point
//     * @param item2 second point
//     * @param mapView MapView control
//     * @return the distance in pixels between two GeoPoints on the map
//     */
//    public static double getOverLayItemDistance(final BankOverlayItem item1, final BankOverlayItem item2, final MapView mapView) {
//        final GeoPoint point = item1.getPoint();
//        final Point ptScreenCoord = new Point();
//        mapView.getProjection().toPixels(point, ptScreenCoord);
//
//        final GeoPoint slavePoint = item2.getPoint();
//        final Point slavePtScreenCoord = new Point();
//        mapView.getProjection().toPixels(slavePoint, slavePtScreenCoord);
//        return findDistance(ptScreenCoord.x, ptScreenCoord.y, slavePtScreenCoord.x, slavePtScreenCoord.y);
//    }
//
//    /**
//     * Gets a geo point.
//     *
//     * @param latitude the latitude
//     * @param longitude the longitude
//     * @return the GeoPoint point with given coordinates
//     */
//    public static GeoPoint getPoint(final double latitude, final double longitude) {
//        return new GeoPoint((int) (latitude * MILLION), (int) (longitude * MILLION));
//    }
//
//    /**
//     * Gets a translated distance caption.
//     *
//     * @param distance the distance
//     * @param direction the direction
//     * @param directionInitial the flag indicating whether a caption should include a direction initial
//     * @param context the context
//     * @return the distance caption
//     */
//    public static String getStringDistance(final float distance, final String direction, final boolean directionInitial, final Context context) {
//        try {
//            float tempDistance = convertMetersToMiles(distance);
//            BigDecimal bd = new BigDecimal(tempDistance);
//            final int decimalPlaces = 1;
//            bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
//            tempDistance = bd.floatValue();
//            if (!directionInitial) {
//                return Utils.concatenateStrings(new String[]{Float.toString(tempDistance)," ",context.getString(R.string.miles_away)});
//            } else {
//                return Utils.concatenateStrings(new String[]{Float.toString(tempDistance)," ", context.getString(R.string.miles), " ", direction});
//            }
//        } catch (final Exception e) {
//            Log.w("Exception", "Error Converting Distance.", e);
//        }
//        return "";
//    }
//}
