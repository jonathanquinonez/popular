package com.popular.android.mibanco.locator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.AsyncTaskListener;

/**
 * Class that manages all related to the user's location
 */
public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String DIALOG_TAG = "Location Updates";

    /**
     * Fragment class to display error related to location
     */
    public static class ErrorDialogFragment extends DialogFragment {

        private Dialog dialog;

        public ErrorDialogFragment() {
            super();
            dialog = null;
        }

        public void setDialog(final Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            return dialog;
        }
    }

    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    public static final int FAST_CEILING_IN_SECONDS = 1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    private static final String TAG = "LocationManager";

    private final LocationRequest locationRequest;
    private Context context;
    private AsyncTaskListener listener;
    private GoogleApiClient googleApiClient;

    public LocationManager(final Context context) {
        this.context = context;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        googleApiClient = new GoogleApiClient.Builder(context).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    public void connect(final Context context, final AsyncTaskListener listener) {
        this.context = context;
        this.listener = listener;
        if (!googleApiClient.isConnected() && servicesConnected()) {
            googleApiClient.connect();
        }
    }

    public void disconnect() {
        if (googleApiClient.isConnected()) {
            stopPeriodicUpdates();
        }
        googleApiClient.disconnect();
        context = null;
        listener = null;
    }

    private void startPeriodicUpdates() {
        if (servicesConnected()) {
            if ( ContextCompat.checkSelfPermission( App.getApplicationInstance().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    private void stopPeriodicUpdates() {
        if (servicesConnected()) {
            if ( ContextCompat.checkSelfPermission( App.getApplicationInstance().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        App.getApplicationInstance().setUserLocation(location);
    }

    @Override
    public void onConnected(final Bundle dataBundle) {

        startPeriodicUpdates();
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (listener != null) {
                listener.onSuccess(lastLocation);
            }
            App.getApplicationInstance().setUserLocation(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        if (listener != null) {
            listener.onError(new BankException(connectionResult.toString()));
        }
        if (context instanceof Activity) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult((Activity) context, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (final IntentSender.SendIntentException e) {

                }
            } else {
                showErrorDialog(connectionResult.getErrorCode());
            }
        }
    }

    public boolean servicesConnected() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            if (context instanceof FragmentActivity) {
                final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, CONNECTION_FAILURE_RESOLUTION_REQUEST);

                if (errorDialog != null) {
                    final ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                    errorFragment.setDialog(errorDialog);
                    errorFragment.show(((FragmentActivity) context).getSupportFragmentManager(), DIALOG_TAG);
                }
            }
            return false;
        }
    }

    private void showErrorDialog(final int errorCode) {
        final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, (Activity) context, CONNECTION_FAILURE_RESOLUTION_REQUEST);

        if (errorDialog != null) {
            final ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(((FragmentActivity) context).getSupportFragmentManager(), TAG);
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
