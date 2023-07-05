package com.popular.android.mibanco.task;

import android.content.Context;
import android.os.Handler;
import android.text.Spanned;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.AthmException;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.AthmEnrollAccount;
import com.popular.android.mibanco.ws.response.AthmEnrollCard;
import com.popular.android.mibanco.ws.response.AthmEnrollConfirmation;
import com.popular.android.mibanco.ws.response.AthmEnrollInfo;
import com.popular.android.mibanco.ws.response.AthmEnrollPhone;
import com.popular.android.mibanco.ws.response.AthmEnrollPhoneCode;
import com.popular.android.mibanco.ws.response.AthmResponse;
import com.popular.android.mibanco.ws.response.AthmSSOInfo;
import com.popular.android.mibanco.ws.response.AthmSendMoneyConfirmation;
import com.popular.android.mibanco.ws.response.AthmSendMoneyInfo;
import com.popular.android.mibanco.ws.response.AthmSendMoneyInput;

import java.util.LinkedHashMap;


/**
 * Class that manages all ATH Móvil async tasks
 */
public class AthmTasks {

    /**
     * Interface to be implemented and manage the ATHM backend response
     * @param <T> The parameter
     */
    public interface AthmListener<T> extends TaskListener {
        void onAthmApiResponse(T result);
    }

    private static abstract class AthmTask<T> extends SessionAsyncTask {

        protected T response;

        public AthmTask(Context context, AthmListener<T> listener) {
            super(context, listener, true);
        }

        public AthmTask(Context context, String message, long duration, AthmListener<T> listener) {
            super(context, listener, true, message, duration);
            this.startTime = System.currentTimeMillis();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                response = doAsync();
                if (response instanceof AthmSSOInfo) {
                    AthmResponse athmResponse = (AthmResponse) response;
                    if (athmResponse.isBlocked() || athmResponse.isAlertError()) {
                        athmResponse.setDowntime(false);
                        throw new AthmException(athmResponse);
                    }
                }
                else if (response instanceof AthmResponse) {
                    AthmResponse athmResponse = (AthmResponse) response;
                    if (athmResponse.isDowntime() || athmResponse.isBlocked() || athmResponse.isAlertError()) {
                        throw new AthmException(athmResponse);
                    }
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {

            if (addDelay) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AthmTask.super.onPostExecute(result);
                        if (taskListener != null) {
                            ((AthmListener<T>) taskListener).onAthmApiResponse(response);
                        }
                        if (progressDialog.isShowing()) {
                            cleanUp();
                        }
                    }
                }, Utils.addDelayDuration(startTime, System.currentTimeMillis(), expectedDuration));


            } else {
                super.onPostExecute(result);
                if (taskListener != null) {
                    ((AthmListener<T>) taskListener).onAthmApiResponse(response);
                }
            }

        }

        protected abstract T doAsync() throws Exception;
    }

    private static class GetPhoneContactsTask extends AthmTask<LinkedHashMap<String, PhonebookContact>> {

        public GetPhoneContactsTask(Context context, AthmListener<LinkedHashMap<String, PhonebookContact>> listener) {
            super(context, listener);
        }

        @Override
        protected LinkedHashMap<String, PhonebookContact> doAsync() throws Exception {
            return ContactsManagementUtils.getContactsWithPhones(context);
        }
    }

    private static class AthmSendMoneyInfoTask extends AthmTask<AthmSendMoneyInfo> {

        public AthmSendMoneyInfoTask(Context context, AthmListener<AthmSendMoneyInfo> listener) {
            super(context, listener);
        }

        @Override
        protected AthmSendMoneyInfo doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().getAthmSendMoneyInfo();
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmsendmoney") || !response.getResponderMessage().equalsIgnoreCase("send_money_info")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                CustomerAccount account = App.getApplicationInstance().getAccount(response.getFromAccount().getApiAccountKey());
                response.getFromAccount().setBalance(account.getPortalBalance());
                response.getFromAccount().setCardResourceUri("drawable://" + App.getApplicationInstance().getAccountCardResource(account));

                return response;
            }
            return new AthmSendMoneyInfo();
        }
    }

    private static class AthmSendMoneyTask extends AthmTask<AthmSendMoneyConfirmation> {

        private String amount;
        private String message;
        private String phoneNumber;

        public AthmSendMoneyTask(Context context, String amount, String message, String phoneNumber, AthmListener<AthmSendMoneyConfirmation> listener) {
            super(context, listener);
            this.amount = amount;
            this.message = message;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected AthmSendMoneyConfirmation doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                AthmSendMoneyInput inputResponse = App.getApplicationInstance().getApiClient().postAthmSendMoneyInput(amount, message, phoneNumber);
                responderName = inputResponse.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (inputResponse.getAlertMessage() != null && !inputResponse.getAlertMessage().equals("")) {
                    String errorMessage = android.text.Html.fromHtml(inputResponse.getAlertMessage()).toString();
                    throw new BankException(errorMessage, false);

                } else if (!(responderName.equalsIgnoreCase("athmsendmoney") || inputResponse.getResponderMessage().equalsIgnoreCase("send_money_input"))) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                response = App.getApplicationInstance().getApiClient().postAthmSendMoneyConfirmation();
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    return response;
                } else if (!responderName.equalsIgnoreCase("athmsendmoney") || !response.getResponderMessage().equalsIgnoreCase("send_money_confirmation")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                return response;
            }
            return new AthmSendMoneyConfirmation();
        }
    }

    private static class AthmEnrollInfoTask extends AthmTask<AthmEnrollInfo> {

        public AthmEnrollInfoTask(Context context, AthmListener<AthmEnrollInfo> listener) {
            super(context, listener);
        }

        @Override
        protected AthmEnrollInfo doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().getAthmEnrollInfo();
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !response.getResponderMessage().equalsIgnoreCase("enroll_info")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                return response;
            }
            return new AthmEnrollInfo();
        }
    }

    private static class AthmEnrollPhoneTask extends AthmTask<AthmEnrollPhone> {

        public AthmEnrollPhoneTask(Context context, AthmListener<AthmEnrollPhone> listener) {
            super(context, listener);
        }

        @Override
        protected AthmEnrollPhone doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmEnrollPhone();
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !response.getResponderMessage().equalsIgnoreCase("enroll_phone")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                return response;
            }
            return new AthmEnrollPhone();
        }
    }

    private static class AthmEnrollPhoneNumberTask extends AthmTask<AthmEnrollPhoneCode> {

        private String phoneNumber;
        private String phoneProvider;

        public AthmEnrollPhoneNumberTask(Context context, String phoneNumber, String phoneProvider, AthmListener<AthmEnrollPhoneCode> listener) {
            super(context, listener);
            this.phoneNumber = phoneNumber;
            this.phoneProvider = phoneProvider;
        }

        @Override
        protected AthmEnrollPhoneCode doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmEnrollPhone(phoneNumber, phoneProvider);
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !response.getResponderMessage().equalsIgnoreCase("enroll_confirmation_code")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                return response;
            }
            return new AthmEnrollPhoneCode();
        }
    }

    private static class AthmEnrollCardTask extends AthmTask<AthmEnrollCard> {

        public AthmEnrollCardTask(Context context, AthmListener<AthmEnrollCard> listener) {
            super(context, listener);
        }

        @Override
        protected AthmEnrollCard doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmEnrollCard();
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !response.getResponderMessage().equalsIgnoreCase("enroll_card")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                return response;
            }
            return new AthmEnrollCard();
        }
    }

    private static class AthmEnrollAccountTask extends AthmTask<AthmEnrollAccount> {

        private String account;
        private String plasticExpDateMonth;
        private String plasticExpDateYear;
        private String plasticNum;

        public AthmEnrollAccountTask(Context context, String account, String plasticExpDateMonth, String plasticExpDateYear, String plasticNum, AthmListener<AthmEnrollAccount> listener) {
            super(context, listener);
            this.account = account;
            this.plasticExpDateMonth = plasticExpDateMonth;
            this.plasticExpDateYear = plasticExpDateYear;
            this.plasticNum = plasticNum;
        }

        @Override
        protected AthmEnrollAccount doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmEnrollAccount(account, plasticExpDateMonth, plasticExpDateYear, plasticNum);
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.isBlocked() && response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    String message = android.text.Html.fromHtml(response.getAlertMessage()).toString();
                    throw new BankException(message, true);

                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !(response.getResponderMessage().equalsIgnoreCase("enroll_register") || response.getResponderMessage().equalsIgnoreCase("enroll_auth"))) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                return response;
            }
            return new AthmEnrollAccount();
        }
    }

    private static class AthmEnrollLoginTask extends AthmTask<AthmEnrollConfirmation> {

        private String usernameLogin;
        private String passwordLogin;

        public AthmEnrollLoginTask(Context context, String usernameLogin, String passwordLogin, AthmListener<AthmEnrollConfirmation> listener) {
            super(context, listener);
            this.usernameLogin = usernameLogin;
            this.passwordLogin = passwordLogin;
        }

        @Override
        protected AthmEnrollConfirmation doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmEnrollLogin(usernameLogin, passwordLogin);
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !response.getResponderMessage().equalsIgnoreCase("enroll_confirmation")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                try {
                    App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
                } catch (Exception e) {
                    CustomerEntitlements customerEntitlements = new CustomerEntitlements();
                    App.getApplicationInstance().setCustomerEntitlements(customerEntitlements);
                }

                return response;
            }
            return new AthmEnrollConfirmation();
        }
    }

    private static class AthmEnrollRegistrationTask extends AthmTask<AthmEnrollConfirmation> {

        private String usernameEnroll;
        private String passwordEnroll;
        private String passwordConfirm;
        private String termsConfirmation;

        public AthmEnrollRegistrationTask(Context context, String usernameEnroll, String passwordEnroll, String passwordConfirm,String termsConfirmation, AthmListener<AthmEnrollConfirmation> listener) {
            super(context, listener);
            this.usernameEnroll = usernameEnroll;
            this.passwordEnroll = passwordEnroll;
            this.passwordConfirm = passwordConfirm;
            this.termsConfirmation = termsConfirmation;
        }

        @Override
        protected AthmEnrollConfirmation doAsync() throws Exception {

            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmEnrollRegistration(usernameEnroll, passwordEnroll, passwordConfirm, termsConfirmation);
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmenroll") || !response.getResponderMessage().equalsIgnoreCase("enroll_confirmation")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                try {
                    App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
                } catch (Exception e) {
                    CustomerEntitlements customerEntitlements = new CustomerEntitlements();
                    App.getApplicationInstance().setCustomerEntitlements(customerEntitlements);
                }

                return response;
            }
            return new AthmEnrollConfirmation();
        }
    }

    private static class AthmTokenInfoTask extends AthmTask<AthmSSOInfo> {
        private String mAppToken;
        private boolean mGenerateToken;

        public AthmTokenInfoTask(Context context, String appToken, boolean generateToken, AthmListener<AthmSSOInfo> listener) {
            super(context, listener);
            mAppToken = appToken;
            mGenerateToken = generateToken;
        }

        public AthmTokenInfoTask(Context context, String appToken, boolean generateToken, String message, long duration, AthmListener<AthmSSOInfo> listener) {
            super(context,message,duration,listener);
            mAppToken = appToken;
            mGenerateToken = generateToken;
        }

        @Override
        protected AthmSSOInfo doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().postAthmTokenInfo(mAppToken, mGenerateToken);
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmssotoken") || !response.getResponderMessage().equalsIgnoreCase("athm_sso_token")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                try {
                    App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
                } catch (Exception e) {
                    CustomerEntitlements customerEntitlements = new CustomerEntitlements();
                    App.getApplicationInstance().setCustomerEntitlements(customerEntitlements);
                }

                return response;
            }
            return new AthmSSOInfo();
        }
    }

    private static class LogoutAthmSsoTask extends AthmTask<AthmSSOInfo> {

        public LogoutAthmSsoTask(Context context, AthmListener<AthmSSOInfo> listener) {
            super(context, listener);
        }

        @Override
        protected AthmSSOInfo doAsync() throws Exception {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
                response = App.getApplicationInstance().getApiClient().logoutAthmSso();
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                } else if (response.getAlertMessage() != null && !response.getAlertMessage().equals("")) {
                    Spanned msg = android.text.Html.fromHtml(response.getAlertMessage());
                    String errorMessage = msg.toString();
                    throw new BankException(errorMessage, false);

                } else if (!responderName.equalsIgnoreCase("athmssounbind") || !response.getResponderMessage().equalsIgnoreCase("athm_sso_unbind")) {
                    throw new BankException(context.getString(R.string.athm_generic_error_processing_alert), true);
                }

                try {
                    App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
                } catch (Exception e) {
                    CustomerEntitlements customerEntitlements = new CustomerEntitlements();
                    App.getApplicationInstance().setCustomerEntitlements(customerEntitlements);
                }

                return response;
            }
            return new AthmSSOInfo();
        }
    }

    public static void getPhoneContacts(final Context context, AthmListener<LinkedHashMap<String, PhonebookContact>> listener) {
        new GetPhoneContactsTask(context, listener).execute();
    }

    public static void getAthmSendMoneyInfo(Context context, AthmListener<AthmSendMoneyInfo> listener) {
        new AthmSendMoneyInfoTask(context, listener).execute();
    }

    public static void postAthmSendMoney(Context context, String amount, String message, String phoneNumber, AthmListener<AthmSendMoneyConfirmation> listener) {
        new AthmSendMoneyTask(context, amount, message, phoneNumber, listener).execute();
    }

    public static void getAthmEnrollInfo(Context context, AthmListener<AthmEnrollInfo> listener) {
        new AthmEnrollInfoTask(context, listener).execute();
    }

    public static void postAthmEnrollPhone(Context context, AthmListener<AthmEnrollPhone> listener) {
        new AthmEnrollPhoneTask(context, listener).execute();
    }

    public static void postAthmEnrollPhone(Context context, String phoneNumber, String phoneProvider, AthmListener<AthmEnrollPhoneCode> listener) {
        new AthmEnrollPhoneNumberTask(context, phoneNumber, phoneProvider, listener).execute();
    }

    public static void postAthmEnrollCard(Context context, AthmListener<AthmEnrollCard> listener) {
        new AthmEnrollCardTask(context, listener).execute();
    }

    public static void postAthmEnrollAccount(Context context, String account, String plasticExpDateMonth, String plasticExpDateYear, String plasticNum, AthmListener<AthmEnrollAccount> listener) {
        new AthmEnrollAccountTask(context, account, plasticExpDateMonth, plasticExpDateYear, plasticNum, listener).execute();
    }

    public static void postAthmEnrollLogin(Context context, String usernameLogin, String passwordLogin, AthmListener<AthmEnrollConfirmation> listener) {
        new AthmEnrollLoginTask(context, usernameLogin, passwordLogin, listener).execute();
    }

    public static void postAthmEnrollRegistration(Context context, String usernameEnroll, String passwordEnroll, String passwordConfirm, String termsConfirmation, AthmListener<AthmEnrollConfirmation> listener) {
        new AthmEnrollRegistrationTask(context, usernameEnroll, passwordEnroll, passwordConfirm, termsConfirmation,listener).execute();
    }

    public static void getAthmTokenInfo(Context context, String appToken, boolean generateToken, AthmListener<AthmSSOInfo> listener) {
        new AthmTokenInfoTask(context, appToken, generateToken, listener).execute();
    }

    public static void getAthmTokenInfo(Context context, String appToken, String message, long duration, boolean generateToken, AthmListener<AthmSSOInfo> listener) {
        new AthmTokenInfoTask(context, appToken, generateToken, message, duration, listener).execute();

    }

    public static void logoutAthmSso(Context context, AthmListener<AthmSSOInfo> listener) {
        new LogoutAthmSsoTask(context, listener).execute();
    }
}
