package com.popular.android.mibanco.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.popular.android.mibanco.R;

/**
 * Utility class for the Premia initiative.
 * @author Evertec
 * @since Java 1.8
 * @version 1.0
 * @see PremiaUtils
 */
public class PremiaUtils {

    /**
     * Private constructor for a utility class.
     */
    private PremiaUtils (){}

    /**
     * Method to show a loading dialog when being redirected to the Premia Catalog.
     * @param context
     * @return ProgressDialog
     */
    public static ProgressDialog openPremiaLoadingDialog (final Context context) {
        final ProgressDialog progressDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT); // Loading Dialog Widget.
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait_premia_catalog));
        return progressDialog;
    }

}
