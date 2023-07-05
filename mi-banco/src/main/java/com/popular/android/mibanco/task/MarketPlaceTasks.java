package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.ws.response.GenerateOtpCode;
import com.popular.android.mibanco.ws.response.MarketPlaceTermsResponse;
import com.popular.android.mibanco.ws.response.MarketplaceDeterminateChallenge;
import com.popular.android.mibanco.ws.response.UnicaUrl;
import com.popular.android.mibanco.ws.response.ValidateOtpCode;

/**
 * Class that manages all Marketplace async tasks
 */
public class MarketPlaceTasks {

    /**
     * Interface to be implemented and manage the Marketplace backend response
     * @param <T> The parameter
     */
    public interface MarketPlaceListener<T> extends TaskListener {
        MarketPlaceTermsResponse onMarketPlaceApiResponse(T result);
    }

    public interface DeterminateChallengeListener<T> extends TaskListener {
        MarketplaceDeterminateChallenge onMarketChallengePlaceApiResponse(T result);
    }

    public interface GenerateOtpListener<T> extends TaskListener {
        GenerateOtpCode onGenerateOtpApiResponse(T result);
    }

    public interface ValidateOtpListener<T> extends TaskListener {
        ValidateOtpCode onValidateOptApiResponse(T result);
    }

    public interface GetUrlUnica<T> extends TaskListener {
        UnicaUrl onGetUrlUnica(T result);
    }

    private static abstract class MarketPlaceTask<T> extends SessionAsyncTask {

        protected T response;

        protected MarketPlaceTask(Context context, MarketPlaceListener<T> listener) {
            super(context, listener, true);
        }

        protected MarketPlaceTask(Context context, DeterminateChallengeListener<T> listener) {
            super(context, listener, true);
        }

        protected MarketPlaceTask(Context context, GenerateOtpListener<T> listener) {
            super(context, listener, true);
        }

        protected MarketPlaceTask(Context context, ValidateOtpListener<T> listener) {
            super(context, listener, true);
        }

        protected MarketPlaceTask(Context context, GetUrlUnica<UnicaUrl> listener) {
            super(context, listener, true);
        }


        @Override
        protected Integer doInBackground(Object... params) {
            try {
                response = doAsync();
                return RESULT_SUCCESS;
            } catch (final BankException e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (taskListener != null) {
                if (taskListener instanceof MarketPlaceListener) {
                    ((MarketPlaceListener<T>) taskListener).onMarketPlaceApiResponse(response);
                }

                if (taskListener instanceof DeterminateChallengeListener) {
                    ((DeterminateChallengeListener<T>) taskListener).onMarketChallengePlaceApiResponse(response);
                }

                if (taskListener instanceof GenerateOtpListener) {
                    ((GenerateOtpListener<T>) taskListener).onGenerateOtpApiResponse(response);
                }

                if (taskListener instanceof ValidateOtpListener) {
                    ((ValidateOtpListener<T>) taskListener).onValidateOptApiResponse(response);
                }

                if (taskListener instanceof GetUrlUnica) {
                    ((GetUrlUnica<T>) taskListener).onGetUrlUnica(response);
                }
            }
        }

        protected abstract T doAsync() throws BankException;
    }


    private static class MarketPlacePostAcceptTermsTask extends MarketPlaceTasks.MarketPlaceTask<MarketPlaceTermsResponse> {

        private String product;

        MarketPlacePostAcceptTermsTask(Context context, String product, MarketPlaceTasks.MarketPlaceListener<MarketPlaceTermsResponse> listener) {
            super(context, listener);
            this.product = product;
        }

        @Override
        protected MarketPlaceTermsResponse doAsync() throws BankException {
            response = App.getApplicationInstance().getApiClient().postAcceptTermsMarketplaceWithProductId(product);
            responderName = response.getResponderName();

            if (response != null && responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            } else if (response != null && "acceptMarketplaceTerms".equalsIgnoreCase(response.getResponderName()) && "accept_marketplace_terms".equalsIgnoreCase(response.getResponderMessage())) {
                return response;
            }
            return new MarketPlaceTermsResponse();
        }
    }

    public static void postAcceptTerms(Context context, String product, MarketPlaceTasks.MarketPlaceListener<MarketPlaceTermsResponse> listener) {
        new MarketPlaceTasks.MarketPlacePostAcceptTermsTask(context, product, listener).execute();
    }

    private static class DetermineChallengeTask extends MarketPlaceTasks.MarketPlaceTask<MarketplaceDeterminateChallenge> {

        DetermineChallengeTask(Context context, DeterminateChallengeListener<MarketplaceDeterminateChallenge> listener) {
            super(context, listener);
        }

        @Override
        protected MarketplaceDeterminateChallenge doAsync() throws BankException {
            response = App.getApplicationInstance().getApiClient().postDetermineAuthenticationChallenge();
            responderName = response.getResponderName();

            if (response != null && responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            } else if (response != null && "determineAuthenticationChallenge".equalsIgnoreCase(response.getResponderName()) && "determine_authentication_challenge".equalsIgnoreCase(response.getResponderMessage())) {
                return response;
            }
            return new MarketplaceDeterminateChallenge();
        }
    }

    public static void determineAuthenticationChallenge(Context context, DeterminateChallengeListener<MarketplaceDeterminateChallenge> listener) {
        new MarketPlaceTasks.DetermineChallengeTask(context, listener).execute();
    }

    private static class MarketPlaceGenerateOtpCode extends MarketPlaceTasks.MarketPlaceTask<GenerateOtpCode> {

        private String resendCode;

        MarketPlaceGenerateOtpCode(Context context, String resendCode, GenerateOtpListener<GenerateOtpCode> listener) {
            super(context, listener);
            this.resendCode = resendCode;
        }

        @Override
        protected GenerateOtpCode doAsync() throws BankException {
            response = App.getApplicationInstance().getApiClient().postGenerateOtpCode(resendCode);
            responderName = response.getResponderName();

            if (response != null && responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            } else if (response != null && "generateOtpCode".equalsIgnoreCase(response.getResponderName()) && "generate_otp_code".equalsIgnoreCase(response.getResponderMessage())) {
                return response;
            }
            return new GenerateOtpCode();
        }
    }

    public static void postGenerateOtpCode(Context context, String resendCode, MarketPlaceTasks.GenerateOtpListener<GenerateOtpCode> listener) {
        new MarketPlaceTasks.MarketPlaceGenerateOtpCode(context, resendCode, listener).execute();
    }

    private static class MarketPlaceValidateOtpCode extends MarketPlaceTasks.MarketPlaceTask<ValidateOtpCode> {

        private String code;

        MarketPlaceValidateOtpCode(Context context, String code, ValidateOtpListener<ValidateOtpCode> listener) {
            super(context, listener);
            this.code = code;
        }

        @Override
        protected ValidateOtpCode doAsync() throws BankException {
            response = App.getApplicationInstance().getApiClient().postValidateOtpCode(code);
            responderName = response.getResponderName();

            if (response != null && responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            } else if (response != null && "validateOtpCode".equalsIgnoreCase(response.getResponderName()) && "validate_otp_code".equalsIgnoreCase(response.getResponderMessage())) {
                return response;
            }
            return new ValidateOtpCode();
        }
    }

    public static void postValidateOtpCode(Context context, String code, MarketPlaceTasks.ValidateOtpListener<ValidateOtpCode> listener) {
        new MarketPlaceTasks.MarketPlaceValidateOtpCode(context, code, listener).execute();
    }

    private static class MarketPlaceGetUnicaUrl extends MarketPlaceTasks.MarketPlaceTask<UnicaUrl> {

        private Boolean isOobAuthenticated;

        MarketPlaceGetUnicaUrl(Context context, Boolean isOobAuthenticated, GetUrlUnica<UnicaUrl> listener) {
            super(context, listener);
            this.isOobAuthenticated = isOobAuthenticated;
        }

        @Override
        protected UnicaUrl doAsync() throws BankException {
            response = App.getApplicationInstance().getApiClient().postUnicaUrl(isOobAuthenticated);
            responderName = response.getResponderName();

            if (response != null && responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            } else if (response != null && "getUnicalUrl".equalsIgnoreCase(response.getResponderName()) && "get_unica_url".equalsIgnoreCase(response.getResponderMessage())) {
                return response;
            }
            return new UnicaUrl();
        }
    }

    public static void postUnicaUrl(Context context, Boolean isOobAuthenticated, GetUrlUnica<UnicaUrl> listener) {
        new MarketPlaceTasks.MarketPlaceGetUnicaUrl(context, isOobAuthenticated, listener).execute();
    }

}
