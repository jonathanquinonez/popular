package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.model.EnrollmentLiteRequest;
import com.popular.android.mibanco.ws.response.AthmEnrollPhone;
import com.popular.android.mibanco.ws.response.EnrollmentLiteCompleteResponse;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;

public class LiteEnrollmentTasks {

    public interface LiteEnrollmentListener<T> extends TaskListener {
        void onLiteEnrollmentApiResponse(T result);
    }

    private static abstract class LiteEnrollmentTask<T> extends SessionAsyncTask {
        protected T response;

        public LiteEnrollmentTask(Context context, LiteEnrollmentTasks.LiteEnrollmentListener<T> listener) {
            super(context, listener, true);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                response = doAsync();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (taskListener != null) {
                ((LiteEnrollmentTasks.LiteEnrollmentListener<T>) taskListener).onLiteEnrollmentApiResponse(response);
            }
        }

        protected abstract T doAsync() throws Exception;
    }

    private static class EnrollLiteNonCustomerInfoSubmit extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {

        private EnrollmentLiteRequest liteRequest;
        public EnrollLiteNonCustomerInfoSubmit(Context context, EnrollmentLiteRequest request, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
            liteRequest = request;
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().liteNonCustomerInfoSubmit(liteRequest);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class EnrollLiteCustomerInfoSubmit extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {

        private EnrollmentLiteRequest liteRequest;
        public EnrollLiteCustomerInfoSubmit(Context context, EnrollmentLiteRequest request, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
            liteRequest = request;
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().liteCustomerInfoSubmit(liteRequest);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class MobilePhoneProviders extends LiteEnrollmentTasks.LiteEnrollmentTask<AthmEnrollPhone> {
        public MobilePhoneProviders(Context context,LiteEnrollmentTasks.LiteEnrollmentListener<AthmEnrollPhone> listener) {
            super(context, listener);
        }

        @Override
        protected AthmEnrollPhone doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().getPhoneProviders();
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class MobilePhoneInAlerts extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {
        public MobilePhoneInAlerts(Context context,LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().hasMobilePhoneInAlerts();
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class CustomerLiteEnrolled extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {

        private String token;
        public CustomerLiteEnrolled(Context context, String token, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
            this.token = token;
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().isCustomerLiteEnrolled(token);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class GenerateSmsCode extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {

        private boolean resendCode;
        public GenerateSmsCode(Context context, boolean resendCode, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
            this.resendCode = resendCode;
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().generateSmsCode(resendCode);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class ValidateSmsCode extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {

        private String code;
        public ValidateSmsCode(Context context, String code, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
            this.code = code;
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().validateSmsCode(code);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class BindCustomerDevice extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteResponse> {

        private String productType;
        private boolean bind;
        public BindCustomerDevice(Context context,String productType, boolean bind, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
            super(context, listener);
            this.productType = productType;
            this.bind = bind;
        }

        @Override
        protected EnrollmentLiteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().bindCustomerDevice(productType,bind);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class CreateLiteProfile extends LiteEnrollmentTasks.LiteEnrollmentTask<EnrollmentLiteCompleteResponse> {

        public CreateLiteProfile(Context context, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteCompleteResponse> listener) {
            super(context, listener);
        }

        @Override
        protected EnrollmentLiteCompleteResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().createLiteProfile();
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }else{
                App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
            }
            return response;
        }
    }

    public static void postLiteEnrollNonCustomerInfo(Context context, EnrollmentLiteRequest request, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.EnrollLiteNonCustomerInfoSubmit(context, request, listener).execute();
    }

    public static void postLiteEnrollCustomerInfo(Context context, EnrollmentLiteRequest request, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.EnrollLiteCustomerInfoSubmit(context, request, listener).execute();
    }

    public static void postIsCustomerLiteEnrolled(Context context, String token, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.CustomerLiteEnrolled(context, token, listener).execute();
    }

    public static void getPhoneProviders(Context context,LiteEnrollmentTasks.LiteEnrollmentListener<AthmEnrollPhone> listener) {
        new LiteEnrollmentTasks.MobilePhoneProviders(context, listener).execute();
    }

    public static void getHasMobilePhoneInAlerts(Context context,LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.MobilePhoneInAlerts(context, listener).execute();
    }

    public static void getGenerateSmsCode(Context context, boolean resendCode, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.GenerateSmsCode(context,resendCode, listener).execute();
    }

    public static void getValidateSmsCode(Context context, String code, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.ValidateSmsCode(context,code, listener).execute();
    }

    public static void bindCustomerDevice(Context context,String productType, boolean bind, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse> listener) {
        new LiteEnrollmentTasks.BindCustomerDevice(context,productType,bind,listener).execute();
    }

    public static void getCreateLiteProfile(Context context, LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteCompleteResponse> listener) {
        new LiteEnrollmentTasks.CreateLiteProfile(context, listener).execute();
    }
}
