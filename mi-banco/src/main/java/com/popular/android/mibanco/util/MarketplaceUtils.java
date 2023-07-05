package com.popular.android.mibanco.util;
import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.AsyncTaskListener;
import com.popular.android.mibanco.model.MarketPlaceEnum;
import com.popular.android.mibanco.task.MarketPlaceTasks;
import com.popular.android.mibanco.ws.response.GenerateOtpCode;
import com.popular.android.mibanco.ws.response.MarketPlaceTermsResponse;
import com.popular.android.mibanco.ws.response.MarketplaceDeterminateChallenge;
import com.popular.android.mibanco.ws.response.UnicaUrl;
import com.popular.android.mibanco.ws.response.ValidateOtpCode;

import java.io.File;

/**
 * MarketplaceUtils class for Marketplace related logic
 */

public class MarketplaceUtils {

    final static private String imageCacheCreditCardPr = File.separator
            + Utils.sha256(MiBancoConstants.MARKETPLACE_CC_PR);
    final static private String imageCacheCreditCardVi = File.separator
            + Utils.sha256(MiBancoConstants.MARKETPLACE_CC_VI);
    final static private String imageCacheEAccountPr = File.separator
            + Utils.sha256(MiBancoConstants.MARKETPLACE_EA_PR);
    final static private String imageCacheEAccountVi = File.separator
            + Utils.sha256(MiBancoConstants.MARKETPLACE_EA_VI);

    public static String getIdImageCache(String identifier) {
        String idImageCache = "";

        if(MarketPlaceEnum.Products.marketplace_credit_card_pr.name().equals(identifier)){
            idImageCache = imageCacheCreditCardPr;
        } else if (MarketPlaceEnum.Products.marketplace_credit_card_usvi.name().equals(identifier)) {
            idImageCache = imageCacheCreditCardVi;
        } else if (MarketPlaceEnum.Products.marketplace_eaccount_pr.name().equals(identifier)) {
            idImageCache = imageCacheEAccountPr;
        } else if (MarketPlaceEnum.Products.marketplace_eaccount_usvi.name().equals(identifier)) {
            idImageCache = imageCacheEAccountVi;
        }
        return idImageCache;
    }

    public interface ActionGetUnicaUrlCallback {
        void getUnicaUrlAction(String url,String authCode);
    }

    static boolean isHiddenButtonResend = false;

    public static void sendAcceptTermsMarketplaceWithProduct(final Context context, final String product) {

        MarketPlaceTasks.postAcceptTerms(context, product, new MarketPlaceTasks.MarketPlaceListener<MarketPlaceTermsResponse>() {
            @Override
            public MarketPlaceTermsResponse onMarketPlaceApiResponse(MarketPlaceTermsResponse result) {
                if (result != null) {
                    if (result.getSuccess()) {
                        determineAuthenticationChallenge(context);
                    }
                }
                return result;
            }
        });
    }

    public static void determineAuthenticationChallenge(final Context context) {
        MarketPlaceTasks.determineAuthenticationChallenge(context, new MarketPlaceTasks.DeterminateChallengeListener<MarketplaceDeterminateChallenge>() {
            @Override
            public MarketplaceDeterminateChallenge onMarketChallengePlaceApiResponse(MarketplaceDeterminateChallenge result) {

                if (result != null) {

                    if (MarketPlaceEnum.MarketeplaceAction.NO_CHALLENGE_PRESENTED.name().equals(result.getAction())){
                        getUnicaUrl(context,false);
                    }
                    else if (MarketPlaceEnum.MarketeplaceAction.OOB_CHALLENGE.name().equals(result.getAction())) {

                        //TODO This variable may not be required
                        final AsyncTaskListener oobAsyncTaskListener = new AsyncTaskListener() {
                            @Override
                            public void onSuccess(Object result) {
                                getUnicaUrl(context,true);
                            }

                            @Override
                            public boolean onError(Throwable error) {
                                return false;
                            }

                            @Override
                            public void onCancelled() {
                                if (context instanceof AsyncTaskListener) {
                                    final AsyncTaskListener listener = (AsyncTaskListener) context;
                                    listener.onCancelled();
                                }
                            }
                        };

                        RSAUtils.challengeRSAStatus(context, oobAsyncTaskListener);
                    }
                    else if (MarketPlaceEnum.MarketeplaceAction.EMAIL_CHALLENGE.toString().equals(result.getAction())) {

                        MarketPlaceEnum.setTimerSeconds(MarketPlaceEnum.RESEND_SECONDS);
                        isHiddenButtonResend = false;
                        generateOtpCode( context,MarketPlaceEnum.resendCode.RESEND_CODE_FALSE.toString());
                    }
                    else {
                        Utils.showAlert(context,context.getResources().getString(R.string.otp_generic_error));
                    }
                }
                return result;
            }
        });
    }

    public static void generateOtpCode(final Context context, final String resendCode) {
        MarketPlaceTasks.postGenerateOtpCode(context, resendCode , new MarketPlaceTasks.GenerateOtpListener<GenerateOtpCode>() {
            @Override
            public GenerateOtpCode onGenerateOtpApiResponse(GenerateOtpCode result) {

                if (result != null) {

                    if (MarketPlaceEnum.OtpChallengeStatus.OTP_SERVICE_SUCCESS.name().equals(result.getStatus())) {

                        MarketPlaceEnum.setTimerSeconds(MarketPlaceEnum.RESEND_SECONDS);
                        if (Boolean.parseBoolean(resendCode)){
                            String titleDialog = context.getString(R.string.otp_resend_code_msj);
                            String textDialog = context.getString(R.string.otp_code_sent_message) + " <b>"+ Utils.maskEmail(App.getApplicationInstance().getLoggedInUser().getCustomerEmail(), MiBancoConstants.MASK_PATTERN_LENGTH) + "</b>" + ".";
                            OtpUtils.challengeOtpStatus(context,titleDialog,textDialog,isHiddenButtonResend,false);
                        } else {
                            String titleDialog = context.getString(R.string.otp_verification_code);
                            String textDialog = context.getString(R.string.otp_code_sent_message) + " <b>"+ Utils.maskEmail(App.getApplicationInstance().getLoggedInUser().getCustomerEmail(), MiBancoConstants.MASK_PATTERN_LENGTH) + "</b>" + ".";
                            OtpUtils.challengeOtpStatus(context,titleDialog,textDialog,isHiddenButtonResend,false);
                        }
                    }
                    else if (MarketPlaceEnum.OtpChallengeStatus.RESEND_LIMIT_REACHED.name().equals(result.getStatus())) {

                        isHiddenButtonResend = true;
                        String titleDialog = context.getString(R.string.otp_resend_code_msj);
                        String textDialog = context.getString(R.string.otp_code_sent_message) + " <b>"+ Utils.maskEmail(App.getApplicationInstance().getLoggedInUser().getCustomerEmail(), MiBancoConstants.MASK_PATTERN_LENGTH) + "</b>" + ".";
                        OtpUtils.challengeOtpStatus(context,titleDialog,textDialog,isHiddenButtonResend,false);
                    }
                    else {
                        Utils.showAlert(context,context.getResources().getString(R.string.otp_generic_error));
                    }
                }
                return result;
            }
        });
    }

    public static void validateOtpCode(final Context context, final String code) {

        MarketPlaceTasks.postValidateOtpCode(context, code, new MarketPlaceTasks.ValidateOtpListener<ValidateOtpCode>() {
            @Override
            public ValidateOtpCode onValidateOptApiResponse(ValidateOtpCode result) {
                if (result != null) {

                    if (MarketPlaceEnum.OtpChallengeStatus.VALIDATION_SUCCESS.name().equals(result.getStatus())) {
                        getUnicaUrl(context,false);
                    }
                    else if (MarketPlaceEnum.OtpChallengeStatus.VALIDATION_LIMIT.name().equals(result.getStatus())) {
                        getUnicaUrl(context,false);
                    }
                    else if (MarketPlaceEnum.OtpChallengeStatus.VALIDATION_FAILED.name().equals(result.getStatus())) {

                        String titleDialog = context.getResources().getString(R.string.otp_incorrect_code);
                        String textDialog = context.getResources().getString(R.string.otp_incorrect_code_text);
                        OtpUtils.challengeOtpStatus(context,titleDialog,textDialog,isHiddenButtonResend,false);
                    }
                    else if (MarketPlaceEnum.OtpChallengeStatus.CODE_EXPIRED.name().equals(result.getStatus())) {

                        String titleDialog = context.getResources().getString(R.string.otp_expired_code);
                        String textDialog = context.getResources().getString(R.string.otp_expired_code_text);
                        OtpUtils.challengeOtpStatus(context,titleDialog,textDialog,isHiddenButtonResend,true);
                    }
                    else {

                        Utils.showAlert(context,context.getResources().getString(R.string.otp_generic_error));
                    }
                }
                return result;
            }

        });
    }

    public static void getUnicaUrl(final Context context, final Boolean isOobAuthenticated) {

        MarketPlaceTasks.postUnicaUrl(context, isOobAuthenticated, new MarketPlaceTasks.GetUrlUnica<UnicaUrl>() {

            @Override
            public UnicaUrl onGetUrlUnica(UnicaUrl result) {

                if (result != null && result.getAuthCode() != null && !result.getAuthCode().isEmpty()
                        && result.getUrl() != null && !result.getUrl().isEmpty()) {

                    String authCode = result.getAuthCode();
                    String unicaUrl = result.getUrl();

                    ActionGetUnicaUrlCallback actionGetUnicaUrlCallback;
                    if (context instanceof ActionGetUnicaUrlCallback) {
                        actionGetUnicaUrlCallback = (ActionGetUnicaUrlCallback) context;
                        actionGetUnicaUrlCallback.getUnicaUrlAction(unicaUrl, authCode);
                    }
                }
                else {
                    Utils.showAlert(context,context.getResources().getString(R.string.otp_generic_error));
                }
                return result;
            }
        });
    }
}
