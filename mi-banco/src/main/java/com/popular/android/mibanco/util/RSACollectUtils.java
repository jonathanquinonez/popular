package com.popular.android.mibanco.util;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.rsa.mobilesdk.sdk.MobileAPI;

import java.util.Properties;

public final class RSACollectUtils {

    final static String hashPhoneNumber = "0";

    private RSACollectUtils() {}

    /**
     * Collecting device information MBFC-645
     * @param context context Activity
     */
    public static String collectDeviceInfo(Context context){

        String json = "";

        if (App.getApplicationInstance().getGlobalStatus().isDeviceInfoSdkInfoEnabled()){
            // Adding Custom Elements to the JSON String
            //Mode Two
            Properties properties = new Properties();

            properties.setProperty(MobileAPI.CONFIGURATION_KEY, String.valueOf(MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION));
            properties.setProperty(MobileAPI.HASH_PHONE_NUMBER_KEY, hashPhoneNumber);

            MobileAPI mobileAPI = MobileAPI.getInstance(context);
            mobileAPI.initSDK(properties);

            json = mobileAPI.collectInfo();
            mobileAPI.destroy();
        }

        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        return json.replaceAll(characterFilter,"");
    }


}
