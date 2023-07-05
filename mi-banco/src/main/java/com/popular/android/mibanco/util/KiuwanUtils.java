/**
 *  Project: CIBP
 *  Company: Evertec
 */
package com.popular.android.mibanco.util;

import android.util.Log;

import com.popular.android.mibanco.MiBancoConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Kiuwans Utils
 * @author Evertec
 * @version 1.0
 * @since Java 1.8
 * @see KiuwanUtils
 */
public class KiuwanUtils {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "Kiuwan"; // kiuwan value log tag

    /**
     * DATE_TAG
     */
    private static final String DATE_TAG = "Date"; // date value log tag

    /**
     * Constructor
     */
    private KiuwanUtils() {} // OK

    /**
     * Always check object type before cast
     * @param type class to cast
     * @param value instance to apply cast
     * @return casted instance
     */
    public static <Cast> Cast checkBeforeCast (Class<Cast> type, Object value){
        Cast castedInstance = null; // new instance

        try {
            castedInstance = type.cast(value);
        } catch (ClassCastException e){
            final String errorDescription = "Error in cast."; // error description

            Log.e(LOG_TAG, errorDescription);
        }
        return castedInstance;
    }

    /**
     * Date format class java.text.SimpleDateFormat spends many resources
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getDateFormat (){
        return new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT, Locale.getDefault());
    }

    /**
     * Date format class java.text.SimpleDateFormat spends many resources
     * @param strDateValue date to convert
     * @return Date
     */
    public static Date getDate (String strDateValue){
        Date date = null; // date
        try {
            if (strDateValue != null){
                date = KiuwanUtils.getDateFormat().parse(strDateValue);
            }
        } catch (final ParseException e) {
            final String errorDescription = "Error while parsing date string."; // error description

            Log.e(DATE_TAG, errorDescription);
        }
        return date;
    }

    /**
     * Do not use strings to compare characters
     * @param strValue string value
     * @param chrValue character value
     * @return boolean
     */
    public static boolean compareStringChar (String strValue, char chrValue){
        return strValue.length() > 0 && strValue.charAt(0) == chrValue;
    }

}
