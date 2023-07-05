package com.popular.android.mibanco.ws;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface MiBancoServices {

    @GET("actions/globalStatus")
    Call<String> globalStatus();

    @FormUrlEncoded
    @POST("actions/login")
    Call<String> login(@FieldMap HashMap<String, Object> params);

    @GET("actions/login")
    Call<String> loginGet();

    @GET("actions/sessionping")
    Call<String> sessionPing();

    @FormUrlEncoded
    @POST("actions/question")
    Call<String> question(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/password")
    Call<String> password(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/logout")
    Call<String> logout(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/loginoob")
    Call<String> loginObb(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/oobchallenge")
    Call<String> oobChallenge(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileCheckRsaChallenge")
    Call<String> getMobileCheckRsaChallenge(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileCheckRsaChallenge")
    Call<String> postMobileCheckRsaChallenge(@FieldMap HashMap<String, Object> params);

    @POST("actions/portal")
    Call<String> portal();

    @GET("actions/notificationCenter")
    Call<String> notificationCenter();

    @FormUrlEncoded
    @POST("https://apps.popular.com/mobile/api/mi-banco/alerts-splash/")
    Call<String> pushWelcomeParameters(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/transaction")
    Call<String> transaction(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/paymenthistory")
    Call<String> paymentHistory(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/transferhistory")
    Call<String> transferHistory(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/makeTransfer")
    Call<String> transfer(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/quickpayment")
    Call<String> quickPayment(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/CCAinProcessTransactions")
    Call<String> ccaInProcessTransactions(@FieldMap HashMap<String, Object> params);

    @GET("actions/ebppPortalInbox")
    Call<String> ebppPortalInbox();

    @GET("actions/remotedeposithistory")
    Call<String> remoteDepositHistory();

    @FormUrlEncoded
    @POST("actions/remotedeposithistoryimages")
    Call<String> remoteDepositHistoryImages(@FieldMap HashMap<String, Object> params);

    @POST("actions/remotedepositrdcenroll")
    Call<String> remoteDepositDCenRoll();

    @POST("actions/getRemoteDepositEasyAcceptTerms")
    Call<String> getRemoteDepositEasyAcceptTerms();

    @POST("actions/remoteDepositEasyAcceptTerms")
    Call<String> remoteDepositEasyAcceptTerms();

    @FormUrlEncoded
    @POST("actions/remotedepositsubmitdeposit")
    Call<String> remoteDepositSubmitDeposit(@FieldMap HashMap<String, Object> params);

    @POST("actions/removeMessage")
    Call<String> removeMessage();

    @FormUrlEncoded
    @POST("actions/mobileGetBalanceInfo")
    Call<String> mobileGetBalanceInfo(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileTokenization")
    Call<String> mobileTokenization(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/liteEnrollmentVerification")
    Call<String> liteEnrollmentVerification(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/isCustomerLiteEnrolled")
    Call<String> getCustomerLiteEnrolled(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/liteCustomerEnrollmentInfoSubmit")
    Call<String> liteCustomerEnrollmentInfoSubmit(@FieldMap HashMap<String, Object> params);

    @GET("actions/customerEntitlements")
    Call<String> customerEntitlements();

    @FormUrlEncoded
    @POST("actions/addCustomerDevice")
    Call<String> addCustomerDevice(@FieldMap HashMap<String, Object> params);

    @GET("actions/createLiteProfile")
    Call<String> createLiteProfile();

    @FormUrlEncoded
    @POST("actions/generateSmsCode")
    Call<String> generateSmsCode(@FieldMap HashMap<String, Object> params);

    @GET("actions/generateSmsCode")
    Call<String> getGenerateSmsCode();

    @FormUrlEncoded
    @POST("actions/validateSmsCode")
    Call<String> validateSmsCode(@FieldMap HashMap<String, Object> params);

    @GET("actions/hasMobilePhoneAlerts")
    Call<String> hasMobilePhoneAlerts();

    @GET("actions/mobilePhoneProviders")
    Call<String> mobilePhoneProviders();

    @FormUrlEncoded
    @POST("actions/mobileAlertsSaveSplashDecision")
    Call<String> mobileAlertsSaveSplashDecision(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/tokenOpac")
    Call<String> tokenOpac(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/renewPermID")
    Call<String> renewPermID(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileCheckPushTokenStatus")
    Call<String> mobileCheckPushTokenStatus(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileSavePushToken")
    Call<String> mobileSavePushToken(@FieldMap HashMap<String, Object> params);

    @GET("actions/makeTransfer")
    Call<String> getTransfer();

    @GET("actions/quickpayment")
    Call<String> getQuickPayment();

    @FormUrlEncoded
    @POST("actions/mobileAthOnOff")
    Call<String> mobileAthOnOff(@FieldMap HashMap<String, Object> params);

    @GET("actions/getOnOffCardPlastics")
    Call<String> getOnOffCardPlastics();

    @GET("actions/athmsendmoney")
    Call<String> getAthMSendMoney();

    @FormUrlEncoded
    @POST("actions/athmsendmoney")
    Call<String> athMSendMoney(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/athmenroll")
    Call<String> athMEnroll(@FieldMap HashMap<String, Object> params);

    @GET("actions/athmenroll")
    Call<String> getAthMEnroll();

    @FormUrlEncoded
    @POST("actions/athmssotoken")
    Call<String> athMSsoToken(@FieldMap HashMap<String, Object> params);

    @GET("actions/athmssounbind")
    Call<String> athMsSoUnbind();

    @FormUrlEncoded
    @POST("actions/acceptMarketplaceTerms")
    Call<String> acceptMarketplaceTerms(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/determineAuthenticationChallenge")
    Call<String> determineAuthenticationChallenge(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/getUnicaUrl")
    Call<String> getUnicaUrl(@FieldMap HashMap<String, Object> params);

    @POST("actions/rsaCheckStatus")
    Call<String> rsaCheckStatus();

    @FormUrlEncoded
    @POST("actions/generateOtpCode")
    Call<String> generateOtpCode(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/validateOtpCode")
    Call<String> validateOtpCode(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/tsysLoyaltyRewardsBalanceInformation")
    Call<String> getTsysLoyaltyRewardsInfo(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/rewardsRedemption")
    Call<String> postTsysLoyaltyRewardsRedemption(@Query ("frontEndId") String frontendId,@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/tsysLoyaltyRewardsAutomaticRedemption")
    Call<String> getTsysAutomaticRedemption(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/tsysLoyaltyRewardsRedirection")
    Call<String> getTsysLoyaltyRewardsRedirectUrl(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/fisRetirementPlanInformation")
    Call<String> getRetirementPlanInfoUrl(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/tsysLoyaltyRewardsAccountInfo")
    Call<String> getLoyaltyRewardsBalanceUrl(@FieldMap HashMap<String, Object> params);

    @GET("actions/mobileCashTrxSubmitV1")
    Call<String> mobileCashPendingTrxPath();

    @GET("actions/mobileCashAccountsV1")
    Call<String> mobileCashAccountPath();

    @FormUrlEncoded
    @POST("actions/mobileCashTrxAssignAtmV1")
    Call<String> mobileCashSubmitTrxCode(@FieldMap HashMap<String, Object> params);

    @GET("actions/mobileCashTrxSubmit")
    Call<String> getEasyCashPendingTrx(@QueryMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileCashTrxSubmit")
    Call<String> postEasyCashPendingTrx(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileCashAccounts")
    Call<String> easyCashAccounts(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("actions/mobileCashTrxAssignAtm")
    Call<String> easyCashTrxCode(@FieldMap HashMap<String, Object> params);
}
