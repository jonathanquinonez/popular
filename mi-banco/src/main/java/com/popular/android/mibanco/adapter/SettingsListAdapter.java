package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.AsyncTaskListener;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.object.SettingsItem;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.EnrollmentLiteStatus;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.ProductType;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.RSAUtils;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;
import com.popular.android.mibanco.ws.response.PushTokenRequest;
import com.popular.android.mibanco.ws.response.PushTokenResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Adapter class to manage data in the settings list
 */
public class SettingsListAdapter extends BaseAdapter {


    private final ArrayList<SettingsItem> items = new ArrayList<>();
    private final LayoutInflater inflater;
    private boolean isSessionActive;
    private final Context mContext;
    private SwitchCompat swt;
    private SwitchCompat pushToggle;
    private RelativeLayout alertsLayout;
    private LinearLayout warningLayout;
    private TextView goToSettings;
    private boolean pushTogglePressed = true;


    public SettingsListAdapter(final Context context, boolean isSessionActive) {
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isSessionActive = isSessionActive;
    }

    public void addItem(final SettingsItem item) {
        items.add(item);
    }

    /**
     * Remove item from List
     * @param item
     */
    public void removeItem(final SettingsItem item) {
        items.remove(item);
    }

    /**
     * Add item at position
     * @param position
     * @param item
     * @return true if item added
     */
    public boolean addItemAt(final int position, final SettingsItem item) {
        if(position >= 0 && position < items.size()) {
            items.add(position, item);
            return true;
        } else return false;
    }

    /**
     * Remove item at position
     * @param position
     * @return true if item removed
     */
    public boolean removeItemAt(final int position) {
        if(position >= 0 && position < items.size()) {
            items.remove(position);
            return true;
        } else return false;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        if (items.size() > 0 && items.size() - 1 >= position) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Verify if item exist on List by title
     * @param title
     * @return position item else -1 if not found
     */
    public int containsItemByTitle(String title) {
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getTitle().equalsIgnoreCase(title)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView;
        final SettingsItem item = items.get(position);


        if (item.isTitle()) {
            myConvertView = inflater.inflate(R.layout.list_item_holo_header, parent, false);
            myConvertView.setEnabled(false);
            myConvertView.setClickable(false);

        } else if (item.isFooter()) {
            myConvertView = inflater.inflate(R.layout.list_item_holo_footer, parent, false);
            myConvertView.setEnabled(false);
            myConvertView.setClickable(false);

        } else if (item.isDescription()) {
            myConvertView = inflater.inflate(R.layout.list_item_holo_description, parent, false);
            myConvertView.setEnabled(false);

            ((TextView) myConvertView.findViewById(R.id.title_description)).setText(item.getDescription());

        } else if (item.isDisplaySwitch()) { //Fingerprint Options Configuration
            myConvertView = inflater.inflate(R.layout.list_item_settings_switch, parent, false);
            myConvertView.setClickable(false);
            swt = (SwitchCompat) myConvertView.findViewById(R.id.switchOption);
            swt.setChecked(AutoLoginUtils.getFingerprintPreference(App.getApplicationInstance().getApplicationContext()));
            swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        switchValueChanged(buttonView.getRootView().getContext(), !isChecked);
                    }
                }
            });

        } else if (item.isPushSettings()) {
            //Configure Push Settings Options
            myConvertView = inflater.inflate(R.layout.list_item_settings_push, parent, false);
            myConvertView.setEnabled(true);

            warningLayout = myConvertView.findViewById(R.id.warningView);
            alertsLayout = myConvertView.findViewById(R.id.alertsView);
            ((TextView) alertsLayout.findViewById(R.id.title_text_alerts)).setText(item.getSubItemTitle());
            goToSettings = myConvertView.findViewById(R.id.settings_btn);
            pushToggle = myConvertView.findViewById(R.id.push_switch);
            boolean isPushActive = PushUtils.isPushToggleChecked(mContext, App.getApplicationInstance().getCurrentUser().getUsername());
            pushToggle.setChecked(isPushActive);
            if (isPushActive) {
                alertsLayout.setVisibility(View.VISIBLE);
                alertsLayout.setClickable(true);
                alertsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertsLayout.setClickable(false);
                        item.getAlertsOnClickListener().onClick(view);
                    }
                });
                if (PushUtils.areNotificationsEnabled(mContext)) {
                    warningLayout.setVisibility(View.GONE);
                } else {
                    warningLayout.setVisibility(View.VISIBLE);
                    goToSettings.setClickable(true);
                    goToSettings.setOnClickListener(PushUtils.getNotificationSettingsListener(mContext));
                }
            }

            pushToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    if (pushTogglePressed) {
                        pushTogglePressed = false;
                        buttonView.setEnabled(false);
                        RSAUtils.challengeRSAStatus(App.getApplicationInstance().getActivityContext(), new AsyncTaskListener() {
                            @Override
                            public void onSuccess(Object result) {
                                savePushTokensPreferences(App.getApplicationInstance().getActivityContext(), isChecked, item);
                            }

                            @Override
                            public boolean onError(Throwable error) {
                                pushToggle.setChecked(!isChecked);
                                enablePushToggle();
                                return false;
                            }

                            @Override
                            public void onCancelled() {
                                pushToggle.setChecked(!isChecked);
                                enablePushToggle();
                            }
                        });
                    }
                }
            });

            if (PushUtils.isPushEnabled()) {
                //Verify is push is enabled in device settings, if not, update fcm token
                PushUtils.checkIfPushIsEnabled(App.getApplicationInstance().getActivityContext(), warningLayout, pushToggle, goToSettings);
            }

        } else {


            if(isPreviousElementPushToggle(position)) {
                myConvertView = inflater.inflate(R.layout.list_item_setting_two_lines, parent, false);
            } else {
                myConvertView = inflater.inflate(R.layout.list_item_settings, parent, false);
            }

        }


        ((TextView) myConvertView.findViewById(R.id.title_text)).setText(item.getTitle());
        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }

    private void registerDevice(Context context, boolean bind) {
        LiteEnrollmentTasks.bindCustomerDevice(context,ProductType.FINGERPRINT.toString(),bind, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                if(result != null){
                    if(result.getStatus() == EnrollmentLiteStatus.FINGERPRINT_DISABLED.getCode()
                            || result.getStatus() == EnrollmentLiteStatus.FINGERPRINT_NOT_REGISTERED.getCode()){

                        AutoLoginUtils.saveFingerprintPreference(App.getApplicationInstance().getApplicationContext(), swt.isChecked());
                        AutoLoginUtils.setUpCountdownFingerprint(mContext,false);
                    }else {
                        AutoLoginUtils.saveFingerprintPreference(App.getApplicationInstance().getApplicationContext(), swt.isChecked());
                        AutoLoginUtils.setUpCountdownFingerprint(mContext,true);
                    }
                }
            }
        });
    }

    private void switchValueChanged(Context context, boolean isChecked) {
        if (isChecked) {
            //FINGERPRINT UNBIND
            if (!isSessionActive) {
                MobileCashUtils.informativeMessage(context, R.string.fingerprint_disable_message);
                swt.setChecked(true);
            } else {
                registerDevice(App.getApplicationInstance().getActivityContext(), false);
            }
        } else {
            //FINGERPRINT BIND
            if (!isSessionActive) {
                MobileCashUtils.informativeMessage(context, R.string.fp_password_setup);
                swt.setChecked(false);
            } else {
                registerDevice(App.getApplicationInstance().getActivityContext(), true);
            }
            AutoLoginUtils.saveFingerprintPreference(App.getApplicationInstance().getApplicationContext(), isChecked);
        }

    }

    private void savePushTokensPreferences(final Context context, final boolean isChecked, final SettingsItem item) {
        PushTokenRequest request = new PushTokenRequest(context);
        String token = PushUtils.getPushToken(context);
        if (StringUtils.isNotEmpty(token)) {
            request.setPushToken(PushUtils.getPushToken(context));
            request.setActive(isChecked);

            App.getApplicationInstance().getAsyncTasksManager().PushTask(context, request, false, new ResponderListener() {
                @Override
                public void responder(String responderName, Object data) {
                    PushTokenResponse response = (PushTokenResponse) data;
                    if (response != null) {
                        if (StringUtils.equals(response.getStatus(), PushTokenResponse.SUCCESS)) {
                            PushUtils.setPushToggleChecked(context, isChecked, App.getApplicationInstance().getCurrentUser().getUsername());
                            pushToggle.setChecked(isChecked);
                            BPAnalytics.logEvent(isChecked ? BPAnalytics.EVENT_PUSH_SWITCH_ON : BPAnalytics.EVENT_PUSH_SWITCH_OFF);
                            configurePushOptions(isChecked, item.getAlertsOnClickListener());
                        } else {
                            pushToggle.setChecked(!isChecked);
                            MobileCashUtils.informativeMessage(context, R.string.mc_service_error_message);
                        }
                    } else {
                        pushToggle.setChecked(!isChecked);
                        MobileCashUtils.informativeMessage(context, R.string.mc_service_error_message);
                    }
                    enablePushToggle();
                }
                @Override
                public void sessionHasExpired() {}
            });


        } else { //Push Token was not generated
            pushToggle.setChecked(!isChecked);
            enablePushToggle();
            MobileCashUtils.informativeMessage(context, R.string.mc_service_error_message);
        }

    }

    private void configurePushOptions (boolean isChecked, final View.OnClickListener clickListener) {
        Animation fadeIn = AnimationUtils.loadAnimation(pushToggle.getRootView().getContext(), R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(pushToggle.getRootView().getContext(), R.anim.fade_out);
        if (isChecked) {
            alertsLayout.setVisibility(View.VISIBLE);
            alertsLayout.startAnimation(fadeIn);
            alertsLayout.setClickable(true);
            alertsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertsLayout.setClickable(false);
                    clickListener.onClick(view);
                }
            });
            if (PushUtils.areNotificationsEnabled(mContext)) {
                warningLayout.setVisibility(View.GONE);
                App.getApplicationInstance().setPushWarningShowing(false);

            } else {
                warningLayout.setVisibility(View.VISIBLE);
                App.getApplicationInstance().setPushWarningShowing(true);
                warningLayout.startAnimation(fadeIn);
                goToSettings.setClickable(true);
                goToSettings.setOnClickListener(PushUtils.getNotificationSettingsListener(mContext));
            }
        } else {
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    alertsLayout.setVisibility(View.GONE);
                    warningLayout.setVisibility(View.GONE);
                    App.getApplicationInstance().setPushWarningShowing(false);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            if (alertsLayout.getVisibility() == View.VISIBLE) {
                alertsLayout.startAnimation(fadeOut);
            }
            if (warningLayout.getVisibility() == View.VISIBLE) {
                warningLayout.startAnimation(fadeOut);
                App.getApplicationInstance().setPushWarningShowing(false);
            }
        }
    }

    private void enablePushToggle() {
        pushToggle.setEnabled(true);
        pushTogglePressed = true;
    }

    private boolean isPreviousElementPushToggle(int currentPosition) {
        if(currentPosition > 0 && currentPosition < items.size()){
            return items.get(currentPosition - 1).isPushSettings();
        } else {
            return false;
        }
    }
}
