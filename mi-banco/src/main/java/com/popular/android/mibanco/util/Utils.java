package com.popular.android.mibanco.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.activity.ErrorView;
import com.popular.android.mibanco.activity.MaintenanceWithBalances;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.SessionListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.model.AthmPhoneProvider;
import com.popular.android.mibanco.model.Country;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.object.BankLocation;
import com.popular.android.mibanco.object.BankLocationDetail;
import com.popular.android.mibanco.view.DialogHolo;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {


    /**
     * This method converts DP units to equivalent device specific value in pixels.
     *
     * @param dp      A value in DIP (Device independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent Pixels equivalent to DP according to device
     */
    public static float convertDpToPixel(final float dp, final Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / MiBancoConstants.BASE_DPI);
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     */
    public static float convertPixelsToDp(final float px, final Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        return (px / (metrics.densityDpi / MiBancoConstants.BASE_DPI));
    }


    public static int pixelsToDp(final float px, final Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px, context.getResources()
                        .getDisplayMetrics());
    }


    /**
     * Returns a valid decimal format for the amount String with the currency $ symbol.
     *
     * @param currency the currency
     * @return the string
     * @throws BankException the exception
     */
    public static String currencyToBigDecimalFormat(final String currency) throws BankException {
        return currency.replaceAll(",", "");
    }

    /**
     * Returns a valid payee ID.
     *
     * @param payeeId the payee id String to validate and parse.
     * @return the valid payee ID integer value or 0 if conversion has failed
     */
    public static int getValidGlobalPayeeId(final String payeeId) {
        if (payeeId == null) {
            return 0;
        }

        final int totalLength = payeeId.length();
        String tempId = payeeId;
        if (totalLength > MiBancoConstants.GLOBAL_PAYEE_ID_MAX_DIGITS) {
            final int startLocation = totalLength - MiBancoConstants.GLOBAL_PAYEE_ID_MAX_DIGITS;
            tempId = payeeId.substring(startLocation);
        }

        tempId = tempId.trim();
        if (TextUtils.isEmpty(tempId)) {
            return 0;
        }

        try {
            return Integer.parseInt(tempId);
        } catch (final NumberFormatException e) {

            Log.w("App", e);
            return 0;
        }
    }

    /**
     * Checks if application update is required.
     *
     * @param minimumApiVersionRequired minimum banking API version required for the application
     * @return true, if the application update is required
     */
    public static boolean isUpdateRequired(final String minimumApiVersionRequired) {
        int minimumApi;
        int currentApi = BuildConfig.VERSION_CODE;

        try {
            minimumApi = Integer.parseInt(minimumApiVersionRequired);
        } catch (NumberFormatException e) {

            return false;
        }

        return currentApi < minimumApi;
    }

    /**
     * Checks if application update is required.
     *
     * @param minimumApiVersionRequired minimum banking API version required for the application
     * @param currentApiVersion         current banking API version supported by the application
     * @return true, if the application update is required
     */
    public static boolean isUpdateRequired(final String minimumApiVersionRequired, final String currentApiVersion) {
        int minimumApi;
        int currentApi;
        try {
            minimumApi = Integer.parseInt(minimumApiVersionRequired);
            currentApi = Integer.parseInt(currentApiVersion);
        } catch (NumberFormatException e) {

            return false;
        }

        return currentApi < minimumApi;
    }

    /**
     * Opens external URL in a browser.
     *
     * @param context the Activity context
     * @param url     the URL string
     */
    public static void openExternalUrl(final Context context, final String url) {
        try {
            final Intent viewUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            BPAnalytics.logEvent(BPAnalytics.EVENT_LAUNCHED_EXTERNAL_URL, "URL", url);
            viewUrlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewUrlIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("Utils", e.toString());
            Toast.makeText(context, context.getString(R.string.no_application_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Setups language.
     *
     * @param activityContext context of an Activity
     */
    public static void setupLanguage(final Context activityContext) {
        final App application = App.getApplicationInstance();
        if (application != null) {
            final String language = application.getLanguage();
            if (language == null || !(language.equalsIgnoreCase(MiBancoConstants.ENGLISH_LANGUAGE_CODE) || language.equalsIgnoreCase(MiBancoConstants.SPANISH_LANGUAGE_CODE))) {
                final String defaultLang = Locale.getDefault().getLanguage();
                final String languageSet = application.setLanguage(defaultLang, activityContext);
                saveLanguage(activityContext.getApplicationContext(), languageSet);
            } else
                application.setLanguage(language, activityContext);
        } else {
            Log.e("BaseActivity", "Language setup cannot be performed, application object is null!");
        }
    }

    public static String getOsInfo(){

        StringBuilder builder = new StringBuilder();

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String osName = field.getName();
            int val = -1;
            try {
                val = field.getInt(new Object());
            } catch (IllegalAccessException e) {
                Log.e("Utils", e.toString());
            }

            if (val == Build.VERSION.SDK_INT) {
                builder.append("OS: ").append(osName).append(", ").append(StringUtils.SPACE)
                        .append("Build: ").append(Build.VERSION.RELEASE).append(",").append(StringUtils.SPACE)
                        .append("SDK: ").append(val);
            }
        }

        return builder.toString();
    }


    public static void setLocale(Context context, Locale locale) {
        Locale.setDefault(locale);
        Context mContext = App.getApplicationInstance();
        final Resources resources = mContext.getResources();
        final Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        mContext.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    // ERRORS HANDLING

    /**
     * Show account blocked error.
     *
     * @param activityContext the activity context
     * @param message         the message
     */
    public static void showAccountBlockedError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_ACCOUNT_BLOCKED, message);
    }

    /**
     * Show account blocked error and exit to login.
     *
     * @param activityContext the activity context
     * @param message         the message
     */
    public static void showAccountBlockedErrorAndExit(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_GENERIC, message);
    }

    /**
     * Show error alert. OK button click does not return an user to the login screen.
     *
     * @param context  the context
     * @param taskException  the exception
     * @param taskListener the optional listener
     */
    public static void showAlertType(final Context context, final Exception taskException, final TaskListener taskListener) {
        if (taskException instanceof BankException) {
            BankException bankException = (BankException) taskException; //The bankException

            if (bankException.getTitle() == null) {
                Utils.showAlert(context, taskException.getMessage(), taskListener, bankException.isBackOnConfirm());
            } else {
                Utils.showDialogAlert(context, taskException.getMessage(), bankException.getTitle());
            }
        }
    }

    /**
     * Show error alert. OK button click does not return an user to the login screen.
     *
     * @param context  the context
     * @param message  the error message
     * @param listener the optional listener
     */
    public static void showAlert(final Context context, final String message, final TaskListener listener, final boolean backOnConfirm) {
        try {
            final DialogHolo dialog = new DialogHolo(context);
            dialog.setMessage(message);
            dialog.setNoTitleMode();
            dialog.setCancelable(false);
            dialog.setConfirmationButton(context.getString(R.string.ok), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    dismissDialog(dialog);
                    if (message.contains(context.getResources().getString(R.string.session_has_expired)) && listener != null) {
                        try {
                            ((SessionListener) listener).sessionHasExpired();
                        } catch (final Exception ex) {

                            Log.w("App", ex);
                        }
                    } else if (backOnConfirm) {
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                }

            });
            showDialog(dialog, context);
        } catch (final Exception e) {

            Log.w("App", e);
        }
    }

    /**
     * Show error alert. OK button click does not return an user to the login screen.
     *
     * @param context  the context
     * @param message  the error message
     * @param title   the error title
     */
    public static void showDialogAlert(final Context context, final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context); //The builder
        LayoutInflater inflateSetup = null;
        Button btnOk = null;

        if (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) instanceof LayoutInflater) {
            inflateSetup = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        final View viewInflate_setup = inflateSetup.inflate(R.layout.alert_dialog_excep, null);

        if (viewInflate_setup.findViewById(R.id.alertDialogBtnOK) instanceof Button) {
            btnOk = (Button) viewInflate_setup.findViewById(R.id.alertDialogBtnOK);
        }

        builder.setTitle(title).setMessage(message);
        builder.setView(viewInflate_setup);

        final AlertDialog alert = builder.create();

        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    /**
     * Show error alert. OK button click does not return an user to the login screen.
     *
     * @param context the context
     * @param title   the error title
     * @param message the error message
     */
    public static void showAlert(final Context context, final String title, final String message) {
        final DialogHolo dialog = new DialogHolo(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setConfirmationButton(context.getString(R.string.ok), new View.OnClickListener() {

            @Override
            public void onClick(final View paramView) {
                dismissDialog(dialog);
            }
        });
        showDialog(dialog, context);
    }

    /**
     * Show error alert. Listener can be used to trigger an action on the button click.
     *
     * @param context  the context
     * @param title    the error title
     * @param message  the error message
     * @param listener the listener called on the button click
     */
    public static void showAlert(final Context context, final String title, final String message, final SimpleListener listener) {
        final DialogHolo dialog = new DialogHolo(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setConfirmationButton(context.getString(R.string.ok), new View.OnClickListener() {

            @Override
            public void onClick(final View paramView) {
                dismissDialog(dialog);
                if (listener != null) {
                    listener.done();
                }
            }
        });
        showDialog(dialog, context);
    }

    /**
     * Show error alert. Listener can be used to trigger an action on the button click.
     *
     * @param context  the context
     * @param message  the error message
     * @param listener the listener called on the button click
     */
    public static void showAlert(final Context context, final String message, final SimpleListener listener) {
        final DialogHolo dialog = new DialogHolo(context);
        dialog.setNoTitleMode();
        dialog.setMessage(message);
        dialog.setConfirmationButton(context.getString(R.string.ok), new View.OnClickListener() {

            @Override
            public void onClick(final View paramView) {
                dismissDialog(dialog);
                if (listener != null) {
                    listener.done();
                }
            }
        });
        showDialog(dialog, context);
    }

    /**
     * Error alert shows up.
     *
     * @param context  the context
     * @param message  the error message
     */
    public static void showAlert(final Context context, final String message) {
        final DialogHolo dialog = new DialogHolo(context);
        dialog.setNoTitleMode();
        dialog.setMessage(message);
        dialog.setConfirmationButton(context.getString(R.string.ok), new View.OnClickListener() {

            @Override
            public void onClick(final View paramView) {
                dismissDialog(dialog);
            }
        });
        showDialog(dialog, context);
    }


    /**
     * TODO: LALR 	android.view.WindowManager$BadTokenException Utils.java line 370 in com.popular.android.mibanco.util.Utils.showAlertDialog()
     * Unable to add window -- token android.os.BinderProxy@e6d2b4 is not valid; is your activity running?
     * De donde vino?
     * EasyCashRedeem.java line 217
     * EasyCashRedeem.java line 203 in
     * MobileCashTasks.java line 56 in
     */
    public static void showAlertDialog(AlertDialogParameters parameters) {
        if (parameters != null) {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parameters.getContext(), R.style.AppCompatAlertDialogStyle).setCancelable(false);

            if (!isBlankOrNull(parameters.getTitle()))
                alertDialogBuilder.setTitle(parameters.getTitle());

            if (!isBlankOrNull(parameters.getStrMessage())) {
                if (!parameters.getIsHtmlFormat()) {
                    alertDialogBuilder.setMessage(parameters.getStrMessage());
                } else {
                    Spanned htmlMessage = getHtmlFormat(parameters.getStrMessage());

                    alertDialogBuilder.setMessage(htmlMessage);
                }
            } else {
                if (parameters.getMessage() != 0) {
                    alertDialogBuilder.setMessage(parameters.getContext().getString(parameters.getMessage()));
                } else {
                    alertDialogBuilder.setMessage(parameters.getStrMessage());
                }
            }

            if (parameters.getOnClickListener() == null) {
                parameters.setOnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            if (!isBlankOrNull(parameters.getPositiveButtonText())) {
                alertDialogBuilder.setPositiveButton(parameters.getPositiveButtonText(), parameters.getOnClickListener());

            }
            if (!isBlankOrNull(parameters.getNegativeButtonText())) {
                alertDialogBuilder.setNegativeButton(parameters.getNegativeButtonText(), parameters.getOnClickListener());
            }
            if (!isBlankOrNull(parameters.getNeutralButtonText())) {
                alertDialogBuilder.setNeutralButton(parameters.getNeutralButtonText(), parameters.getOnClickListener());
            }

            if (parameters.getCustomView() != null) {
                alertDialogBuilder.setView(parameters.getCustomView());
            }

            if (parameters.getInputEditText() != null) {
                alertDialogBuilder.setView(parameters.getInputEditText());
            } else if (parameters.getTextView() != null) {
                alertDialogBuilder.setView(parameters.getTextView());
            }

            if (parameters.getItems() != null) {
                alertDialogBuilder.setItems(parameters.getItems(), parameters.getOnClickListener());
            }

            if (parameters.getOnCancelListener() != null) {
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setOnCancelListener(parameters.getOnCancelListener());
            }

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

            if (!isBlankOrNull(parameters.getNegativeButtonText())) {
                Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (negativeButton != null) {
                    negativeButton.setTextColor(ContextCompat.getColor(parameters.getContext(), R.color.athm_header));
                    parameters.setNegativeButton(negativeButton);
                }
            }

            if (!isBlankOrNull(parameters.getPositiveButtonText())) {
                Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                if (positiveButton != null) {
                    positiveButton.setTextColor(ContextCompat.getColor(parameters.getContext(), R.color.athm_header));
                    parameters.setPositiveButton(positiveButton);
                }
            }

            if (!isBlankOrNull(parameters.getNeutralButtonText())) {
                Button neutralButton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                if (neutralButton != null) {
                    neutralButton.setTextColor(ContextCompat.getColor(parameters.getContext(), R.color.athm_header));
                    parameters.setNeutralButton(neutralButton);
                }
            }
        }
    }


    /**
     * Shown Maintenance page with an option to show balances if available
     */
    public static void showMaintenanceWithBalances(String maintenanceType, Context context) {
        Intent intent = new Intent(context, MaintenanceWithBalances.class);
        intent.putExtra(MiBancoConstants.MAINTENANCE_TYPE, maintenanceType);
        context.startActivity(intent);
    }

    private static Spanned getHtmlFormat(String message) {
        Spanned formatMessage;
        //New implementation for OS Nougat+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatMessage = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY);
        } else {
            formatMessage = Html.fromHtml(message);
        }
        return formatMessage;
    }

    public static boolean isBlankOrNull(String str) {
        return (str == null || str.trim().equals(""));
    }

    public static String getFormattedDollarAmount(String currentAmount) {
        String amount = currentAmount;
        if (amount == null || amount.equals(""))
            amount = "0.00";

        if (amount.contains("$"))
            amount = amount.replace("$", "").trim();

        Double amountDouble = Double.valueOf(amount);
        amount = String.format(Locale.ENGLISH, "%.2f", amountDouble);

        amount = "$" + amount;
        return amount;
    }

    public static int getAmountIntValue(String currentAmount) {
        currentAmount = currentAmount.replace("$", "").replace(",", "").replace("(", "-").replace(")", "").trim();
        Double amountDouble = Double.valueOf(currentAmount);
        return amountDouble.intValue();
    }

    public static int getDateComparison(Date theDate) {
        try {
            Date date = dateResetTime(theDate);
            Date todaysDate = dateResetTime(new Date());

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.setTime(todaysDate);
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            Date tomorrowDate = dateResetTime(tomorrow.getTime());

            Calendar yesterday = Calendar.getInstance();
            yesterday.setTime(todaysDate);
            yesterday.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterdayDate = dateResetTime(yesterday.getTime());

            if (date.compareTo(todaysDate) == 0)
                return MiBancoConstants.DATE_COMPARE_TODAY;
            else if (date.compareTo(tomorrowDate) == 0)
                return MiBancoConstants.DATE_COMPARE_TOMOROW;
            else if (date.compareTo(yesterdayDate) == 0)
                return MiBancoConstants.DATE_COMPARE_YESTERDAY;
            else
                return MiBancoConstants.DATE_COMPARE_MORE;

        } catch (Exception e) {
            Log.e("Utils", e.toString());
        }
        return -1;
    }

    public static Date dateResetTime(Date date) {
        Date dateModify;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        dateModify = calendar.getTime();
        return dateModify;
    }

    /**
     * Shows error screen. The final behavior of the screen depends on the error code. In all cases, pressing back screen moves an user to the login
     * screen.
     *
     * @param activityContext the activity context
     * @param errorCode       the error code
     * @param message         the optional error message, set null for a generic error message
     */
    private static void showErrorScreen(final Context activityContext, final int errorCode, final String message) {
        final Intent intent = new Intent(activityContext, ErrorView.class);
        intent.putExtra("errorCode", errorCode);
        intent.putExtra("errorMessage", message);
        activityContext.startActivity(intent);
    }

    /**
     * Show generic error screen.
     *
     * @param activityContext the activity context
     * @param message         the error message
     */
    public static void showGenericError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_GENERIC, message);
    }

    /**
     * Show "maintenance" error screen.
     *
     * @param activityContext the activity context
     * @param message         the error message
     */
    public static void showMaintenanceError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_MAINTENANCE, message);
    }

    /**
     * Show "no connection" error screen.
     *
     * @param activityContext the activity context
     * @param message         the error message
     */
    public static void showNoConnectionError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_NO_CONNECTION, message);
    }

    /**
     * Show "no connection" error screen.
     *
     * @param activityContext the activity context
     * @param message         the error message
     */
    public static void showNoConnectionDuringLoginError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_NO_CONNECTION_LOGIN, message);
    }

    /**
     * Show "no connection" error screen.
     *
     * @param activityContext the activity context
     * @param message         the error message
     */
    public static void showRefusedConnectionDuringLoginError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_REFUSED_CONNECTION_LOGIN, message);
    }

    /**
     * Show "not available" error screen.
     *
     * @param activityContext the activity context
     * @param message         the message
     */
    public static void showNotAvailableError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_NOT_AVAILABLE, message);
    }

    /**
     * Show "update required" error screen.
     *
     * @param activityContext the activity context
     * @param message         the error message
     */
    public static void showUpdateRequiredError(final Context activityContext, final String message) {
        showErrorScreen(activityContext, ErrorView.ERROR_UPDATE_REQUIRED, message);
    }

    public static void setAccountImagePath(CustomerAccount account, String path, Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(getAccountImagePathKeyHash(account), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("path", path);
        editor.commit();
    }

    public static void clearAccountImagePath(CustomerAccount account, Context context) {
        String pathKeyHash = getAccountImagePathKeyHash(account);
        SharedPreferences prefs = context.getSharedPreferences(pathKeyHash, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        deletePrefsFile(concatenateStrings(new String[]{pathKeyHash, ".xml"}), context);
        deletePrefsFile(concatenateStrings(new String[]{pathKeyHash, ".bak"}), context);

        try {
            String pathKey = getAccountImagePathKey(account);
            prefs = context.getSharedPreferences(pathKey, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.clear();
            editor.commit();

            deletePrefsFile(concatenateStrings(new String[]{pathKey, ".xml"}), context);
            deletePrefsFile(concatenateStrings(new String[]{pathKey, ".bak"}), context);
        } catch (IllegalArgumentException e) {
            Log.e("Utils", e.toString());
        }
    }

    public static String concatenateStrings(String[] strings) {
        String concatenatedString = "";
        StringBuilder stringBuilder = new StringBuilder(128);
        for (String str : strings) {
            concatenatedString = stringBuilder.append(str).toString();
        }
        return concatenatedString;
    }

    public static String getAccountImagePathKey(CustomerAccount account) {
        if (account != null) {
            return "itemId" + account.getNickname() + account.getAccountLast4Num() + account.getAccountNumberSuffix();
        }
        return "";
    }

    public static String getAccountImagePathKeyHash(CustomerAccount account) {
        if (account != null) {
            return sha256(account.getApiAccountKey() + account.getSubtype() + account.getAccountSection());
        }
        return "";
    }

    public static String getAccountImagePath(CustomerAccount account, Context context) {
        try {
            String pathKey = getAccountImagePathKey(account);
            String pathOld = context.getSharedPreferences(pathKey, Context.MODE_PRIVATE).getString("path", "");

            if (!pathOld.equals("")) {
                SharedPreferences prefs = context.getSharedPreferences(pathKey, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();

                setAccountImagePath(account, pathOld, context);
            }

            deletePrefsFile(concatenateStrings(new String[]{pathKey, ".xml"}), context);
            deletePrefsFile(concatenateStrings(new String[]{pathKey, ".bak"}), context);

            if (!pathOld.equals("")) {
                return pathOld;
            }
        } catch (IllegalArgumentException e) {

            Log.e("Utils", e.toString());
        }

        String pathNew = context.getSharedPreferences(getAccountImagePathKeyHash(account), Context.MODE_PRIVATE).getString("path", "");
        if (!pathNew.equals("")) {
            return pathNew;
        }

        return null;
    }

    public static void deletePrefsFile(String fileName, Context context) {
        final String basePath = concatenateStrings(new String[]{context.getApplicationContext().getApplicationInfo().dataDir, "/shared_prefs/", fileName});
        File prefsFile = new File(basePath);
        if (prefsFile.exists()) {
            prefsFile.delete();
        }
    }

    public static String getPrefsString(String key, Context context) {
        final SharedPreferences prefs = getSecuredSharedPreferences(context);
        return prefs.getString(key, null);
    }

    public static void setPrefsString(String key, String value, Context context) {
        final SharedPreferences prefs = getSecuredSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void removePrefsString(String key, Context context) {
        final SharedPreferences prefs = getSecuredSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public static String sha256(String message) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return bin2hex(digest.digest(message.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            Log.e("Utils", e.toString());

        }

        return message;
    }

    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
    }

    /**
     * A safe method to dismiss a dialog.
     *
     * @param dialog a Dialog to dismiss
     */
    public static void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (final Exception e) {
                Log.w("App", e);
            }
        }
    }

    /**
     * A safe method to show a dialog.
     *
     * @param dialog          a Dialog to show
     * @param activityContext the Activity dialog belongs to
     * @return true if dialog was shown or false otherwise
     */
    public static boolean showDialog(Dialog dialog, Context activityContext) {
        if (dialog != null) {
            try {
                if (activityContext != null && !((Activity) activityContext).isFinishing()) {
                    if (activityContext instanceof BaseActivity) {
                        ((BaseActivity) activityContext).getBaseActivityHelper().addDialogToDismissOnDestroy(dialog);
//                    } else if (activityContext instanceof LocatorTabs) {
//                        ((LocatorTabs) activityContext).getBaseActivityHelper().addDialogToDismissOnDestroy(dialog);
                    }
                    dialog.show();
                    return true;
                }
            } catch (final Exception e) {
                Log.e("Utils", e.toString());
            }
        }
        return false;
    }

    /**
     * Adds "My amount".
     *
     * @param context the context
     * @param name    the amount's unique name
     * @param amount  the amount to associate with the name
     * @return true, if the new amount was successfully saved in the shared preferences.
     */
    public static boolean addMyAmount(final Context context, final String name, final int amount) {
        final SharedPreferences savedSession = context.getSharedPreferences(concatenateStrings(new String[]{App.getApplicationInstance().getUid(), "_", MiBancoConstants.MY_AMOUNTS_KEY}), Context.MODE_PRIVATE);
        final String list = savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "");
        if (!(concatenateStrings(new String[]{";", list, ";"}).contains(concatenateStrings(new String[]{";", name, ";"})))) {
            final Editor editor = savedSession.edit();
            editor.putString(MiBancoConstants.MY_AMOUNTS_LIST, savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "").concat(concatenateStrings(new String[]{name, ";"})));
            editor.putInt(concatenateStrings(new String[]{name, "_amount"}), amount);
            return editor.commit();
        }

        return false;
    }

    /**
     * Gets "My amounts" dictionary.
     *
     * @param context the context
     * @return "My amounts" dictionary as HashMap<String, Integer>
     */
    public static HashMap<String, Integer> getMyAmounts(final Context context) {
        final SharedPreferences savedSession = context.getSharedPreferences(concatenateStrings(new String[]{App.getApplicationInstance().getUid(), "_", MiBancoConstants.MY_AMOUNTS_KEY}), Context.MODE_PRIVATE);
        final HashMap<String, Integer> hm = new HashMap<String, Integer>();
        String lst = savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "");
        int lsLong = lst.indexOf(';');
        while (lsLong > -1) {
            final String name = lst.substring(0, lst.indexOf(';'));
            lst = lst.substring(lst.indexOf(';') + 1);
            lsLong = lst.indexOf(';');
            if (name.length() > 0) {
                hm.put(name, savedSession.getInt(concatenateStrings(new String[]{name, "_amount"}), 0));
            }
        }

        return hm;
    }

    /**
     * Gets the payee card Drawable.
     *
     * @param payeeId the payee id
     * @return the payee card Drawable resource id
     */
    public static int getPayeeDrawableResource(final int payeeId) {
        final int res = App.getApplicationInstance().getResources().getIdentifier("payee_number_" + payeeId, "drawable", App.getApplicationInstance().getPackageName());
        if (res != 0) {
            return res;
        }

        // no matching resource found, return id of the default resource id
        return R.drawable.merchant_image_default;
    }

    /**
     * Gets saved user's phone numbers.
     *
     * @param context the context
     * @return a comma-separated String of phone numbers
     */
    public static String getPhones(final Context context) {
        final SharedPreferences savedSession = getSecuredSharedPreferences(context);
        return savedSession.getString("phones", null);
    }

    public static boolean sendTextMessage(Context context, String phoneNumber, String textMessage) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + phoneNumber));
            sendIntent.putExtra("sms_body", textMessage);
            context.startActivity(sendIntent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Format amount's integer value.
     *
     * @param amount the amount integer value
     * @return the formatted amount's String
     */
    public static String formatAmount(final int amount) {
        return MiBancoConstants.CURRENCY_SYMBOL + formatAmountForWs(amount);
    }

    /**
     * Format amount for web services.
     *
     * @param amount the amount
     * @return the amount's String without a currency sign
     */
    public static String formatAmountForWs(final int amount) {
        String amountStr = String.valueOf(amount);
        if (amountStr.length() < 2) {
            amountStr = "0.0" + amountStr;
        } else if (amountStr.length() == 2) {
            amountStr = "0." + amountStr;
        } else {
            amountStr = amountStr.substring(0, amountStr.length() - 2) + '.' + amountStr.substring(amountStr.length() - 2, amountStr.length());
            if (amountStr.length() > MiBancoConstants.AMOUNT_STRING_LEN) {
                amountStr = amountStr.substring(0, amountStr.length() - MiBancoConstants.AMOUNT_STRING_LEN) + ','
                        + amountStr.substring(amountStr.length() - MiBancoConstants.AMOUNT_STRING_LEN, amountStr.length());
            }
        }
        return amountStr;
    }

    /**
     * Format amount for web services.
     *
     * @param amount the amount
     * @return the amount's String without a currency sign
     */
    public static String formatAmountForWsWithoutCommas(final int amount) {
        String amountStr = String.valueOf(amount);
        if (amountStr.length() < 2) {
            amountStr = "0.0" + amountStr;
        } else if (amountStr.length() == 2) {
            amountStr = "0." + amountStr;
        } else {
            amountStr = amountStr.substring(0, amountStr.length() - 2) + '.' + amountStr.substring(amountStr.length() - 2);
        }
        return amountStr;
    }

    /**
     * Loads raw text resource.
     *
     * @param resourceId the resource id
     * @return the String containing the text read or empty String on error
     */
    public static String loadRawTextResource(final int resourceId) {
        try {
            final Resources res = App.getApplicationInstance().getResources();
            final InputStream inputStream = res.openRawResource(resourceId);
            final byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            return new String(b);
        } catch (final Exception e) {
            Log.e("App", "Error loading raw text resource.");
        }
        return "";
    }

    /**
     * Removes an amount identified by "name" parameter from "My amounts" list.
     *
     * @param context the context
     * @param name    the amount name
     * @return true, if successful
     */
    public static boolean removeFromMyAmounts(final Context context, final String name) {
        final SharedPreferences savedSession = context.getSharedPreferences(concatenateStrings(new String[]{App.getApplicationInstance().getUid(), "_", MiBancoConstants.MY_AMOUNTS_KEY}), Context.MODE_PRIVATE);
        if (savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "").contains(name)) {
            final Editor editor = savedSession.edit();
            editor.putString(MiBancoConstants.MY_AMOUNTS_LIST, savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "").replaceAll(concatenateStrings(new String[]{name, ";"}), ""));
            editor.remove(concatenateStrings(new String[]{name, "_amount"}));
            return editor.commit();
        } else {
            return false;
        }
    }

    /**
     * Updates "My amounts" entry.
     *
     * @param context the context
     * @param name    the name of amount to update
     * @param amount  the new amount value
     * @return true, if successful
     */
    public static boolean updateMyAmount(final Context context, final String name, final int amount) {
        final SharedPreferences savedSession = context.getSharedPreferences(concatenateStrings(new String[]{App.getApplicationInstance().getUid(), "_", MiBancoConstants.MY_AMOUNTS_KEY}), Context.MODE_PRIVATE);
        final Editor editor = savedSession.edit();
        final String list = savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "");
        if (!(concatenateStrings(new String[]{";", list, ";"}).contains(concatenateStrings(new String[]{";", name, ";"})))) {
            editor.putString(concatenateStrings(new String[]{App.getApplicationInstance().getUid(), "_", MiBancoConstants.MY_AMOUNTS_LIST}), savedSession.getString(MiBancoConstants.MY_AMOUNTS_LIST, "").concat(concatenateStrings(new String[]{name, ";"})));
        } else {
            editor.remove(concatenateStrings(new String[]{name, "_amount"}));
        }
        editor.putInt(concatenateStrings(new String[]{name, "_amount"}), amount);
        return editor.commit();
    }


    /**
     * Saves application language.
     *
     * @param context  the context
     * @param language the actual language code
     * @return true, if successful
     */
    public static boolean saveLanguage(final Context context, final String language) {
        return saveStringContentToShared(context, "language", language);
    }

    /**
     * Saves user phones to the preferences. Used by call back.
     *
     * @param context the context
     * @param phones  the comma-separated String of phones
     * @return true, if successful
     */
    public static boolean savePhones(final Context context, final String phones) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        editor.putString("phones", phones);
        return editor.commit();
    }


    //region USERNAME MANAGEMENT METHODS ************************

    /**
     * Saves a username.
     *
     * @param context  the context
     * @param username the username to save
     * @return true, if successful
     */
    public static boolean saveUsername(final Context context, final String username) {
        List<User> savedUsernames = getUsernames(context);
        for (User user : savedUsernames) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        User newUser = new User();
        newUser.setUsername(username);
        savedUsernames.add(newUser);

        Gson gS = new Gson();
        String jsonUsernames = gS.toJson(savedUsernames);
        return saveStringContentToShared(context, "usernames", jsonUsernames);
    }

    public static boolean saveUser(final Context context, final User user) {
        List<User> savedUsernames = getUsernames(context);
        boolean userFound = false;
        for (User savedUser : savedUsernames) {
            if (savedUser.getUsername().equals(user.getUsername())) {
                if (!Utils.isBlankOrNull(user.getEncryptedPassword())) {
                    savedUser.setEncryptedPassword(user.getEncryptedPassword());
                }

                if (user.getFingerprintBindDate() != null) {
                    savedUser.setFingerprintBindDate(user.getFingerprintBindDate());
                }

                userFound = true;
                break;
            }
        }

        if (!userFound) {
            savedUsernames.add(user);
        }

        Gson gS = new Gson();
        String jsonUserList = gS.toJson(savedUsernames);
        return saveStringContentToShared(context, "usernames", jsonUserList);
    }


    /**
     * Gets the usernames.
     *
     * @param context the context
     * @return the usernames
     */
    public static List<User> getUsernames(final Context context) {
        String usernames = getStringContentFromShared(context, "usernames");
        if (usernames != null && !usernames.equals("")) {
            Gson gson = new Gson();
            if (usernames.contains("[{")) {
                Type type = new TypeToken<List<User>>() {
                }.getType();
                return gson.fromJson(usernames, type);

            } else { // This validation could eventually be removed. Just created to transition from simple string to json
                String[] users = usernames.split(",");
                List<User> usersList = new LinkedList<>();
                for (String username : users) {
                    User user = new User();
                    user.setUsername(username);
                    usersList.add(user);
                }
                String json = gson.toJson(usersList);
                saveStringContentToShared(context, "usernames", json);
                return usersList;
            }
        }
        return new LinkedList<>();
    }

    /**
     * Removes the username from the list of saved usernames.
     *
     * @param context  the context
     * @param username the username to remove
     */
    public static void removeUsername(final Context context, final String username) {
        List<User> savedUsernames = getUsernames(context);
        for (User user : savedUsernames) {
            if (user.getUsername().equals(username)) {
                savedUsernames.remove(user);
                break;
            }
        }

        if (savedUsernames.size() == 0) {
            saveStringContentToShared(context, "usernames", "");
        } else {
            Gson gson = new Gson();
            saveStringContentToShared(context, "usernames", gson.toJson(savedUsernames));
        }


        // Clear RSA cookies for security reasons
        try {
            App.getApplicationInstance().getApiClient().getSyncRestClient().getCookieManager().clear();
        } catch (Exception e) {
            Log.e("Utils", e.toString());
        }

    }

    //endregion

    public static boolean saveStringContentToShared(final Context context, String key, String value) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        try {
            editor.putString(key, value);
            return editor.commit();
        } catch (Exception e) {
            return false;
        }

    }

    public static String getStringContentFromShared(final Context context, String key) {
        try {
            final SharedPreferences savedSession = getSecuredSharedPreferences(context);
            return savedSession.getString(key, null);
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean savePasswordWithEncryption(final Context context, User user, String plainTextPassword) {
        if (user != null && !Utils.isBlankOrNull(plainTextPassword)) {
            try {
                AESCrypt crypt = new AESCrypt(user.getUsername());
                user.setEncryptedPassword(crypt.encrypt(plainTextPassword));
                return saveUser(context, user);

            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean saveFingerprintDate(final Context context, User user, String registeredDate) {
        if (user != null) {
            try {
                user.setFingerprintBindDate(registeredDate);
                return saveUser(context, user);

            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static String getPasswordDecrypted(User user) {
        try {
            if (user != null && !Utils.isBlankOrNull(user.getUsername()) && !Utils.isBlankOrNull(user.getEncryptedPassword())) {

                AESCrypt crypt = new AESCrypt(user.getUsername());
                return (crypt.decrypt(user.getEncryptedPassword()));
            }
            return "";
        } catch (Exception e) {
            return "";

        }
    }

    public static boolean saveFingerprintCampaign(final Context context, boolean isViewed) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.FINGERPRINT_CAMPAIGN_KEY, isViewed);
        return editor.commit();
    }


    public static boolean isFingerprintCampaignViewed(final Context context) {
        final SharedPreferences savedSession = getSecuredSharedPreferences(context);
        return savedSession.getBoolean(MiBancoConstants.FINGERPRINT_CAMPAIGN_KEY, false);
    }

    public static boolean saveAthmWelcomeSplash(final Context context, boolean isViewed) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.ATHM_SSO_WELCOME_SPLASH_KEY, isViewed);
        return editor.commit();
    }

    public static boolean isAthmWelcomeSplashViewed(final Context context) {
        final SharedPreferences savedSession = getSecuredSharedPreferences(context);
        return savedSession.getBoolean(MiBancoConstants.ATHM_SSO_WELCOME_SPLASH_KEY, false);
    }


    public static boolean setCustomImageBtnClicked(final Context context, boolean isViewed) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.CUSTOM_IMAGE_CAMPAIGN_KEY, isViewed);
        return editor.commit();
    }

    public static boolean wasCustomImageBtnClicked(final Context context) {
        final SharedPreferences savedSession = getSecuredSharedPreferences(context);
        return savedSession.getBoolean(MiBancoConstants.CUSTOM_IMAGE_CAMPAIGN_KEY, false);
    }


    public static boolean removePassword(final Context context) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        editor.putString(MiBancoConstants.PASSWORD_KEY, null);
        return editor.commit();
    }

    public static boolean setPushToggleChecked(final Context context, boolean isChecked) {
        final Editor editor = getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.PUSH_TOGGLE_STATE, isChecked);
        return editor.commit();
    }

    public static boolean isPushToggleChecked(final Context context) {
        final SharedPreferences savedSession = getSecuredSharedPreferences(context);
        return savedSession.getBoolean(MiBancoConstants.PUSH_TOGGLE_STATE, false);
    }


    /**
     * Reads a single facility location.
     *
     * @param reader the JSON reader object
     * @return the ATM or branch location as the BankLocation object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static BankLocation readLocation(final JsonReader reader) throws IOException {


        BankLocation location = new BankLocation();
        try {
            reader.beginObject();
            while (reader.hasNext()) {

                final String name = reader.nextName();
                JsonToken check = reader.peek();

                if (check == JsonToken.NULL) {
                    reader.skipValue();

                } else if (name.equals("id")) {
                    location.setId(reader.nextInt());

                } else if (name.equals("prId")) {
                    location.setPrId(reader.nextString());

                } else if (name.equals("prefix")) {
                    location.setPrefix(reader.nextInt());

                } else if (name.equals("name")) {
                    location.setName(reader.nextString());

                } else if (name.equals("city")) {
                    location.setCity(reader.nextString());

                } else if (name.equals("street1")) {
                    location.setStreet1(reader.nextString());

                } else if (name.equals("street2")) {
                    location.setStreet2(reader.nextString());

                } else if (name.equals("state")) {
                    location.setState(reader.nextString());

                } else if (name.equals("zipCode")) {
                    location.setZipCode(reader.nextString());

                } else if (name.equals("latitude")) {
                    location.setLatitude(reader.nextDouble());

                } else if (name.equals("longitude")) {
                    location.setLongitude(reader.nextDouble());

                } else if (name.equals("location")) {
                    location.setLocation(reader.nextString());

                } else if (name.equals("euro")) {
                    location.setEuro(reader.nextBoolean());

                } else if (name.equals("singleCheckDeposit")) {
                    location.setSingleCheckDeposit(reader.nextBoolean());

                } else if (name.equals("cashDeposit")) {
                    location.setCashDeposit(reader.nextBoolean());

                } else if (name.equals("voiceGuidance")) {
                    location.setVoiceGuidance(reader.nextBoolean());

                } else if (name.equals("popularOne")) {
                    location.setPopularOne(reader.nextBoolean());

                } else if (name.equals("popularMortgage")) {
                    location.setPopularMortgage(reader.nextBoolean());

                } else if (name.equals("popularSecurities")) {
                    location.setPopularSecurities(reader.nextBoolean());

                } else if (name.equals("commercialLine")) {
                    location.setCommercialLine(reader.nextBoolean());

                } else if (name.equals("nightDeposit")) {
                    location.setNightDeposit(reader.nextBoolean());

                } else if (name.equals("autoBanco")) {
                    location.setAutoBanco(reader.nextBoolean());

                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            Log.e("Utils", e.toString());
        }
        return location;
    }

    private static BankLocationDetail readLocationDetail(final JsonReader reader) throws IOException {


        BankLocationDetail locationDetail = new BankLocationDetail();
        try {
            reader.beginObject();
            while (reader.hasNext()) {

                final String name = reader.nextName();
                JsonToken check = reader.peek();

                if (check == JsonToken.NULL) {
                    reader.skipValue();

                } else if (name.equals("sundayOpeningTime")) {
                    locationDetail.setSundayOpeningTime(reader.nextString());

                } else if (name.equals("sundayClosingTime")) {
                    locationDetail.setSundayClosingTime(reader.nextString());

                } else if (name.equals("mondayOpeningTime")) {
                    locationDetail.setMondayOpeningTime(reader.nextString());

                } else if (name.equals("mondayClosingTime")) {
                    locationDetail.setMondayClosingTime(reader.nextString());

                } else if (name.equals("tuesdayOpeningTime")) {
                    locationDetail.setTuesdayOpeningTime(reader.nextString());

                } else if (name.equals("tuesdayClosingTime")) {
                    locationDetail.setTuesdayClosingTime(reader.nextString());

                } else if (name.equals("wednesdayOpeningTime")) {
                    locationDetail.setWednesdayOpeningTime(reader.nextString());

                } else if (name.equals("wednesdayClosingTime")) {
                    locationDetail.setWednesdayClosingTime(reader.nextString());

                } else if (name.equals("thursdayOpeningTime")) {
                    locationDetail.setThursdayOpeningTime(reader.nextString());

                } else if (name.equals("thursdayClosingTime")) {
                    locationDetail.setThursdayClosingTime(reader.nextString());

                } else if (name.equals("fridayOpeningTime")) {
                    locationDetail.setFridayOpeningTime(reader.nextString());

                } else if (name.equals("fridayClosingTime")) {
                    locationDetail.setFridayClosingTime(reader.nextString());

                } else if (name.equals("saturdayOpeningTime")) {
                    locationDetail.setSaturdayOpeningTime(reader.nextString());

                } else if (name.equals("saturdayClosingTime")) {
                    locationDetail.setSaturdayClosingTime(reader.nextString());

                } else if (name.equals("phone")) {
                    locationDetail.setPhone(reader.nextString());

                } else if (name.equals("nameEnglish")) {
                    locationDetail.setHolidayNameEnglish(reader.nextString());

                } else if (name.equals("nameEspanol")) {
                    locationDetail.setHolidayNameSpanish(reader.nextString());

                } else if (name.equals("descriptionEnglish")) {
                    locationDetail.setHolidayDescriptionEnglish(reader.nextString());

                } else if (name.equals("descriptionEspanol")) {
                    locationDetail.setHolidayDescriptionSpanish(reader.nextString());

                } else if (name.equals("startDate")) {
                    locationDetail.setHolidayStartDate(reader.nextString());

                } else if (name.equals("endDate")) {
                    locationDetail.setHolidayEndDate(reader.nextString());

                } else if (name.equals("openingTime")) {
                    locationDetail.setHolidayOpeningTime(reader.nextString());

                } else if (name.equals("closingTime")) {
                    locationDetail.setHolidayClosingTime(reader.nextString());

                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            Log.e("Utils", e.toString());
        }
        return locationDetail;
    }

    /**
     * Reads facilities (ATMs, branches) locations into array list.
     *
     * @param reader the JSON reader
     * @return the list of parsed facilities locations
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static ArrayList<BankLocation> readLocationsArray(final JsonReader reader) throws IOException {
        final ArrayList<BankLocation> messages = new ArrayList<BankLocation>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readLocation(reader));
        }
        reader.endArray();
        return messages;
    }

    public static BankLocationDetail readLocationDetailsArray(final JsonReader reader) throws IOException {
        BankLocationDetail locationDetail = new BankLocationDetail();
        reader.beginArray();
        while (reader.hasNext()) {
            locationDetail = readLocationDetail(reader);
        }
        reader.endArray();
        return locationDetail;
    }

    /**
     * Adds currency prefix to the amount String.
     *
     * @param amount the amount String
     * @return the amount String with the currency sign in front of it
     */
    public static String addCurrencySign(final String amount) {
        return MiBancoConstants.CURRENCY_SYMBOL + amount;
    }

    public static String stripUrlQueryParameters(String url) {
        if (url == null) {
            return null;
        }
        int urlQueryParametersPosition = url.indexOf('?') == -1 ? url.length() : url.indexOf('?');
        return url.substring(0, urlQueryParametersPosition);
    }

    public static boolean showNoPayeesDialog(final Context context, final int message, final boolean finishOnClose, final SimpleListener listener) {
        Payment paymentsInfo = App.getApplicationInstance().getPaymentsInfo();
        boolean noPayees = false;
        if (paymentsInfo == null) {
            return false;
        } else if (paymentsInfo.getAccountsTo() == null) {
            noPayees = true;
        } else if (paymentsInfo.getAccountsTo().size() <= 0) {
            noPayees = true;
        } else if (paymentsInfo.getAccountsTo().size() == 1 && TextUtils.isEmpty(paymentsInfo.getAccountsTo().get(0).getAccountLast4Num())) {
            noPayees = true;
        }

        if (noPayees) {
            final DialogHolo dialog = new DialogHolo(context);
            dialog.setNoTitleMode();
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setConfirmationButton(context.getString(R.string.ok), new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Utils.dismissDialog(dialog);
                    if (finishOnClose) {
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    } else if (listener != null) {
                        listener.done();
                    }
                }
            });
            Utils.showDialog(dialog, context);
            return true;
        }

        return false;
    }

    /**
     * Unbinds Drawable resources starting from the given view and moving down the element tree.
     *
     * @param view the root view
     */
    public static void unbindDrawables(final View view) {
        try {
            if (view != null) {
                if (view.getBackground() != null) {
                    view.getBackground().setCallback(null);
                }
                if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                    int childCount = ((ViewGroup) view).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        unbindDrawables(((ViewGroup) view).getChildAt(i));
                    }
                    ((ViewGroup) view).removeAllViews();
                }
            }
        } catch (final Exception e) {

            Log.w("App", e);
        }
    }

    public static String getAbsoluteUrl(final String relativePath) {
        return MiBancoEnviromentConstants.API_URL + relativePath;
    }

    public static String[] urlBlacklist(Context context) {
        String[] urlBlacklist = context.getResources().getStringArray(R.array.web_view_url_blacklist);
        for (int i = 0; i < urlBlacklist.length; ++i) {
            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
        }
        return urlBlacklist;
    }

    public static int getSuccessfulLoginsCount() {
        final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(App.getApplicationInstance());
        return sharedPreferences.getInt(MiBancoConstants.SUCCESSFUL_LOGINS_PREFS_KEY, 0);
    }

    public static void displayAccountImage(ImageView imageView, CustomerAccount customerAccount) {
        if (customerAccount != null) {
            String uri = Utils.getAccountImagePath(customerAccount, App.getApplicationInstance());
            if (!TextUtils.isEmpty(uri)) {
                DisplayImageOptions.Builder displayImageOptions = new DisplayImageOptions.Builder().cloneFrom(App.getDefaultDisplayImageOptions()).showImageOnFail(customerAccount.getImgResource())
                        .showImageForEmptyUri(customerAccount.getImgResource());
                ImageLoader.getInstance().displayImage("file://" + uri, imageView, displayImageOptions.build());
            } else {
                imageView.setImageResource(customerAccount.getImgResource());
            }
        } else {
            imageView.setImageResource(R.drawable.account_image_default);
        }
    }


    public static final Comparator<Country> countryTitleComparator = new Comparator<Country>() {
        @Override
        public int compare(final Country lhs, final Country rhs) {
            return lhs.getCountryName().compareTo(rhs.getCountryName());
        }
    };

    public static final Comparator<AthmPhoneProvider> providerTitleComparator = new Comparator<AthmPhoneProvider>() {
        @Override
        public int compare(final AthmPhoneProvider lhs, final AthmPhoneProvider rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };


    public static String getParsedPort() {
        String savedUrl = App.getApplicationInstance().getApiUrl();

        String urlDirection = savedUrl.replace("cibp-web/", "").replace("://", "");

        String urlPort = urlDirection.substring(urlDirection.indexOf(":") + 1, urlDirection.indexOf("/"));

        String portString;

        switch (urlPort) {
            case "6443":
                portString = "Cert 4";
                break;
            case "7443":
                portString = "Cert 3";
                break;
            case "8443":
                portString = "Cert 1";
                break;
            case "9443":
                portString = "Cert 2";
                break;
            default:
                portString = urlPort;
                break;
        }

        return portString;
    }

    public static String changeHostIp(String host) {
        Context mContext = App.getApplicationInstance();
        if (FeatureFlags.SDG_WIFI() && host.startsWith(mContext.getResources().getString(R.string.origin_host))) {
            host = host.replace(mContext.getResources().getString(R.string.origin_host), mContext.getResources().getString(R.string.final_host));
            saveNewURL(host);
        } else if (!FeatureFlags.SDG_WIFI() && host.startsWith(mContext.getResources().getString(R.string.final_host))) {
            host = host.replace(mContext.getResources().getString(R.string.final_host), mContext.getResources().getString(R.string.origin_host));
            saveNewURL(host);
        }
        return host;
    }

    protected static void saveNewURL(String url) {
        final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(App.getApplicationInstance()).edit();
        editor.putString(MiBancoConstants.CUSTOM_URL_KEY, url);
        editor.apply();

        App.getApplicationInstance().setApiUrl(url);

    }

    public static long dateDifferenceInSeconds(Date date2, Date date1) {
        long diff = (date2.getTime() - date1.getTime());
        return TimeUnit.MILLISECONDS.toSeconds(diff);
    }

    //delay duration taking into account the process duration, return 0 if was more than the expected time
    public static long addDelayDuration(long startTime, long endTime, long expectedTime) {
        long estimatedTime = endTime - startTime;
        return estimatedTime <= expectedTime ? expectedTime - estimatedTime : 0;
    }

    public static long dateDifferenceInMinutes(Date date2, Date date1) {
        long diff = (date2.getTime() - date1.getTime());
        return TimeUnit.MILLISECONDS.toMinutes(diff);
    }

    public static long dateDifferenceInHours(Date date2, Date date1) {
        long diff = (date2.getTime() - date1.getTime());
        return TimeUnit.MILLISECONDS.toHours(diff);
    }

    //Mask Username with the unicode between index 1 and last one
    public static String maskUsername(String s, int maskLength) {
        return s.trim().replaceAll("(\\w{2}).*([\\w])", "$1" + StringUtils.repeat(MiBancoConstants.MASK_UNICODE, maskLength) + "$2");
    }

    //Mask Email with the unicode between index 1 and last one
    public static String maskEmail(String s, int maskLength) {

        String firstHalf = s.substring(0,s.indexOf("@"));
        String secondHalf = s.substring(s.indexOf("@"));
        String mask = firstHalf.charAt(0) +
                StringUtils.repeat(MiBancoConstants.MASK_UNICODE_ASTERISK, maskLength) +
                firstHalf.charAt(firstHalf.length() - 1);

        return StringUtils.join(mask, secondHalf);
    }

    public static DialogInterface.OnClickListener openPermissionSettings(final Context context) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public static DisplayMetrics resetFontMetrics(Context context, Configuration configuration) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);


        if (Float.compare(configuration.fontScale, 1.15f) == 1) {
            configuration.fontScale = (float) 1.15;
        }

        if (configuration.densityDpi > 612) {
            configuration.densityDpi = 612;
        }
        if (metrics.densityDpi > 612) {
            metrics.densityDpi = 612;
        }

        if (metrics.heightPixels < 2376) {
            metrics.heightPixels = 2376;
        }

        if (Float.compare(metrics.density, 3.825f) == 1) {
            metrics.density = (float) 3.825;
        }

        metrics.scaledDensity = configuration.fontScale * metrics.density;
        metrics.setTo(metrics);

        return metrics;
    }

    public static void navigateToPortal(Context context){
        final Intent intent = new Intent(context, Accounts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    //Onsen UI used in Alerts WebView is supported in Android 5.0 and up
    public static boolean isOnsenSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * This method allows to show the button to redirect to the openaccount
     *      * MBDP-2519
     * @param action can be VISIBLE or GONE(not visible)
     * @param view
     */
    public static void hideOrShowView(final int action, android.view.View view) {
        view.setVisibility(action);
    }

    /**
     * This method generate secured encrypted sharedPreferences file to save
     * local data.
     * only works to device with Api Level 23 or more
     * @param mContext Application or activity context from load sharedPreferences.
     * * MBCI-2135
     */
    public static SharedPreferences getSecuredSharedPreferences(Context mContext) {
        SharedPreferences sharedPreferences = null;
        try {
            if (isDeviceSOSecure()){
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                sharedPreferences = EncryptedSharedPreferences.create(
                        MiBancoConstants.PREFS_KEY,
                        masterKeyAlias,
                        mContext,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } else{
               sharedPreferences = mContext.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE);
            }
            return sharedPreferences;
        } catch (GeneralSecurityException ex) {
            Log.e("App", ex.getMessage());
        } catch (IOException ex){
            Log.e("App", ex.getMessage());
        }
      return sharedPreferences;
    }

    public static boolean isDeviceSOSecure(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getPrefsStringNotNull(String key, Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static boolean isValidUrl(String url, Context context) {
        try {
            URL uri = new URL(url);
            String[] urlAllowList = context.getResources().getStringArray(R.array.allowed_host);
            String host = uri.getHost().replace("www.","");
            return Arrays.asList(urlAllowList).contains(host);
        }catch (MalformedURLException ex){
            Log.e("App", ex.getMessage());
        }
        return false;
    }

    public static String getLocaleStringResource(Locale requestedLocale, int resourceId, Context context) {
        String result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(requestedLocale);
            result = context.createConfigurationContext(config).getText(resourceId).toString();
        }
        else { // support older android versions
            Resources resources = context.getResources();
            Configuration conf = resources.getConfiguration();
            Locale savedLocale = conf.locale;
            conf.locale = requestedLocale;
            resources.updateConfiguration(conf, null);

            // retrieve resources from desired locale
            result = resources.getString(resourceId);

            // restore original locale
            conf.locale = savedLocale;
            resources.updateConfiguration(conf, null);
        }

        return result;
    }
}

