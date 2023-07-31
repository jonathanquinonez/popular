package com.popular.android.mibanco.fragment;

import static com.popular.android.mibanco.MiBancoConstants.FOOTER_FULL_TEXT_KEY;
import static com.popular.android.mibanco.util.BPAnalytics.EVENT_RET_PLAN_TOUCHED;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.AccountDetails;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.activity.CoverFlowActivity;
import com.popular.android.mibanco.activity.DepositCheck;
import com.popular.android.mibanco.activity.EnrollmentLiteWelcomeActivity;
import com.popular.android.mibanco.activity.ImageAdapter;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.NotificationCenter;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsInfo;
import com.popular.android.mibanco.object.ViewHolder;
import com.popular.android.mibanco.task.PremiaTasks;
import com.popular.android.mibanco.task.RetirementPlanTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ImageCarouselListener;
import com.popular.android.mibanco.util.KiuwanUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogCoverup;
import com.popular.android.mibanco.view.fragment.BtmSheetModalFrgmnt;
import com.popular.android.mibanco.viewModel.SharedViewModel;
import com.popular.android.mibanco.ws.response.BannerResponse;
import com.popular.android.mibanco.ws.response.RetirementPlanDetail;
import com.popular.android.mibanco.ws.response.RetirementPlanInfoResponse;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountsFragment extends Fragment implements ImageCarouselListener {

    private View rootView;
    private ImageView imageViewNotificationClose;
    private RecyclerView recyclerView;
    private SharedViewModel viewModel;
    protected Button retPlanNewBadge;
    protected ImageView retPlanLoading;

    private LinearLayout marketplaceBanner;

    private RelativeLayout relativeLayoutNotificationCenter;
    /**
     * MBSE-2480 not show error toast in mla accounts
     */
    private static final String MLA = "MLA"; // mla string var
    /**
     * ON/OFF ANIMATION FLAG
     **/
    private boolean isOnOffCounterAnimationEnabled = false;
    /**
     * Account images Bitmaps.
     */
    private final HashMap<String, WeakReference<Bitmap>> accountsImages = new HashMap<String, WeakReference<Bitmap>>();
    /**
     * Max number of successful logins to which accounts personalization notice should be displayed.
     */
    private static final int MAX_LOGINS_TO_SHOW_NOTICE = 2;

    private ImageCarouselListener imageCarouselListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        setHasOptionsMenu(true);
        Utils.setupLanguage(getContext());
        viewModel.getBannerResponse().observe(getViewLifecycleOwner(), new Observer<BannerResponse>() {
            @Override
            public void onChanged(BannerResponse response) {
                setImageCarousel(response);
            }
        });
        viewModel.getNotificationCenterData().observe(getViewLifecycleOwner(), new Observer<NotificationCenter>() {
            @Override
            public void onChanged(NotificationCenter notificationData) {
                notificationCenterSetup();
            }
        });

        viewModel.getPremiaCatalogRedirectURL().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String premiaCatalogRedirectURL) {
                if(premiaCatalogRedirectURL != null){
                    Utils.openExternalUrl(getContext(), premiaCatalogRedirectURL);
                }
            }
        });
        return rootView;
    }

    @Override
    public void setImageCarousel(BannerResponse response) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList mImageUrls = new ArrayList<>();
        ArrayList mIUrlsAction = new ArrayList<>();

        if (response.getCarousel() != null) {
            for (int i = 0; i < response.getCarousel().size(); i++) {
                mImageUrls.add(response.getCarousel().get(i).getImage_url());
                mIUrlsAction.add(response.getCarousel().get(i).getAction_url());
            }
        } else {
            mImageUrls.add(response.getImage_url());
        }

        ImageAdapter adapter = new ImageAdapter(mImageUrls, mIUrlsAction);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnMobileWithdrawal = view.findViewById(R.id.btn_mobile_withdrawal);
        recyclerView = view.findViewById(R.id.recycler_view);

        imageViewNotificationClose = view.findViewById(R.id.notification_center_close_button);
        relativeLayoutNotificationCenter = view.findViewById(R.id.notification_center_notice);

        final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(getContext());

        final boolean dismissNotice = sharedPreferences.getBoolean(MiBancoConstants.DISMISS_PERSONALIZATION_NOTICE_PREFS_KEY, false);

        if (Utils.getSuccessfulLoginsCount() > MAX_LOGINS_TO_SHOW_NOTICE || dismissNotice) {
            view.findViewById(R.id.accounts_notice_layout).setVisibility(View.GONE);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            final Intent intent = new Intent(getActivity(), CoverFlowActivity.class);
            startActivity(intent);
        }

        if (FeatureFlags.NOTIFICATION_CENTER()) {
            view.findViewById(R.id.notification_center_notice).setVisibility(View.VISIBLE);
            this.notificationCenterButtonExecution();
        } else {
            view.findViewById(R.id.notification_center_notice).setVisibility(View.GONE);
        }
        btnMobileWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnrollmentLiteWelcomeActivity.class);
                intent.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, true);
                startActivity(intent);
            }
        });

        Button btnEasyDeposit = view.findViewById(R.id.btn_easy_deposit);
        btnEasyDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepositCheck.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(getActivity());
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MiBancoConstants.DISMISS_PERSONALIZATION_NOTICE_PREFS_KEY, true);
                editor.commit();
                v.findViewById(R.id.accounts_notice_layout).setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(getActivity());
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MiBancoConstants.DISMISS_PERSONALIZATION_NOTICE_PREFS_KEY, true);
                editor.commit();
                v.findViewById(R.id.accounts_notice_layout).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateDataSource();
        //greylin.aruias MBSE-2175 App.getApplicationInstance()
        if (App.getApplicationInstance().isUpdatingBalances()) {
            if (App.getApplicationInstance().isReloadPayments()) {
                showUpdateBalancesDialog();
                fetchPayments(false, true);
            }
            if (App.getApplicationInstance().isReloadTransfers()) {
                showUpdateBalancesDialog();
                fetchTransfers(false, true);
            }
            updateDataSource();
            App.getApplicationInstance().setUpdatingBalances(false);
        } else {
            updateDataSource();
            this.addMarketplaceBannerIfNecessary();
        }
    }

    @Override
    public void onDestroy() {
        for (final String path : accountsImages.keySet()) {
            if (accountsImages.get(path) != null && accountsImages.get(path).get() != null) {
                accountsImages.get(path).get().recycle();
            }
        }
        super.onDestroy();
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem menuLogout = menu.findItem(R.id.menu_logout);
        if (FeatureFlags.NOTIFICATION_CENTER()) {
            int bellType;
            if (MiBancoPreferences.isNewNotificationsFlag()) {
                bellType = (rootView.findViewById(R.id.notification_center_notice).getVisibility() == View.VISIBLE) ? R.id.notification_center_ribbon : R.id.notification_center_active;
            } else {
                bellType = R.id.notification_center_normal;
                rootView.findViewById(R.id.notification_center_notice).setVisibility(View.GONE);
            }
            if(App.getApplicationInstance().isSessionNeeded()) {
                menuLogout.setVisible(true);
            }
            menu.findItem(bellType).setVisible(false);
        }
    }

    private void fetchPayments(final boolean showProgress, final boolean forceReload) {
        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            App.getApplicationInstance().getAsyncTasksManager().fetchPayments(getContext(), new ResponderListener() {
                @Override
                public void responder(final String responderName, final Object data) {

                    final Payment paymentInstance = KiuwanUtils.checkBeforeCast(Payment.class, data);//Payment instance

                    if (paymentInstance != null) {

                        App.getApplicationInstance().getAsyncTasksManager().updateBalances(getContext());

                        hideUpdateBalancesDialogPayment();
                    }

                }
                @Override
                public void sessionHasExpired() {
                    Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
                }

            }, showProgress, forceReload);
        }
    }

    private void hideUpdateBalancesDialogPayment() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDataSource();
                App.getApplicationInstance().setReloadPayments(false);
                Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
            }

        }, MiBancoConstants.DIALOG_DURATION);
    }

    /**
     * Fetch transfers.
     *
     * @param showProgress Show progress dialog?
     * @param forceReload  Should we force reload even when transfers are already loaded.
     */
    private void fetchTransfers(final boolean showProgress, final boolean forceReload) {
        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            App.getApplicationInstance().getAsyncTasksManager().fetchTransfers(getContext(), new ResponderListener() {
                @Override
                public void responder(final String responderName, final Object data) {
                    final Transfer transferInstance = KiuwanUtils.checkBeforeCast(Transfer.class, data);//Transfer instance
                    if (transferInstance != null) {
                        App.getApplicationInstance().getAsyncTasksManager().updateBalances(getContext());
                        hideUpdateBalancesDialogTransfer();
                    }
                }
                @Override
                public void sessionHasExpired() {
                    Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
                }

            }, showProgress, forceReload);
        }
    }

    /**
     * hide UpdateBalances Dialog Transfer
     */
    private void hideUpdateBalancesDialogTransfer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDataSource();
                App.getApplicationInstance().setReloadTransfers(false);
                Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
            }

        }, MiBancoConstants.DIALOG_DURATION);
    }

    private void addMarketplaceBannerIfNecessary() {
        if(getActivity() instanceof Accounts) {
            Accounts accounts = (Accounts) getActivity();
        if (accounts.hasMarketplaceProducts()) {
            marketplaceBanner = rootView.findViewById(R.id.marketplace_banner);
            marketplaceBanner.setVisibility(View.VISIBLE);
            marketplaceBanner.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    accounts.marketplaceActivity();
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MARKETPLACE_BANNER);
                }
            });
            }
        }
    }

    private void showUpdateBalancesDialog() {
        Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
        App.getApplicationInstance().setDialogCoverupUpdateBalances(new DialogCoverup(getContext()));
        App.getApplicationInstance().getDialogCoverupUpdateBalances().setProgressCaption(R.string.refreshing_balances);
        Utils.showDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances(), getActivity());
    }


    public void notificationCenterSetup() {

        if (FeatureFlags.NOTIFICATION_CENTER()) {
            if (MiBancoPreferences.isNewNotificationsFlag()) {
                relativeLayoutNotificationCenter.setVisibility(View.VISIBLE);
                this.notificationCenterButtonExecution();
            }
        } else {
            relativeLayoutNotificationCenter.setVisibility(View.GONE);
        }
    }

    private void notificationCenterButtonExecution() {
        imageViewNotificationClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(getContext());
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MiBancoConstants.DISMISS_NEW_NOTIFICATION_KEY, true);
                editor.commit();
                relativeLayoutNotificationCenter.setVisibility(View.GONE);
            }
        });
    }

    public void updateDataSource() {
        Customer loggedInCustomer = App.getApplicationInstance().getLoggedInUser();//Fix Kiuwan MBDP_2519 declare constant
        if (loggedInCustomer != null) {
            loadAccounts(loggedInCustomer.getCreditCards(), R.id.cards_view, R.id.cards_list);
            loadAccounts(loggedInCustomer.getDepositAccounts(), R.id.accounts_view, R.id.accounts_list);
            loadAccounts(loggedInCustomer.getLoans(), R.id.loans_view, R.id.loans_list);
            loadAccounts(loggedInCustomer.getMortgage(), R.id.mortgage_view, R.id.mortgage_list);
            loadAccounts(loggedInCustomer.getCdsIras(), R.id.cds_iras_view, R.id.cds_iras_list);
            loadAccounts(loggedInCustomer.getOtherAccounts(), R.id.other_accounts_view, R.id.other_accounts_list);
            loadAccounts(loggedInCustomer.getRewards(), R.id.rewards_view, R.id.rewards_list);
            loadAccounts(loggedInCustomer.getInsuranceAndSecurities(), R.id.secins_view, R.id.secins_list);
            showOrHideRetirementPlanAccount(loggedInCustomer);
        } else {
            App.getApplicationInstance().reLogin(getContext());
        }
        if (App.getApplicationInstance().isUpdateSidebarMenuOnResume()) {
//            setupDrawer();
            App.getApplicationInstance().setUpdateSidebarMenuOnResume(false);
        }
    }

    private void showOrHideRetirementPlanAccount(final Customer loggedInCustomer) {

        rootView.findViewById(R.id.retplan_view).setVisibility(View.GONE);

        retPlanNewBadge = rootView.findViewById(R.id.retplan_view_new_alert);

        if (loggedInCustomer.isRetPlanEnabled() && App.getApplicationInstance().getGlobalStatus().isReterimentPlanEnabled()) {

            retPlanLoading = rootView.findViewById(R.id.ret_plan_loading);

            final Animation loading_animation = AnimationUtils.loadAnimation(getContext(), R.anim.quick_spin_animation);//to add visual animations
            loading_animation.reset();
            loading_animation.setInterpolator(new LinearInterpolator());
            loading_animation.setDuration(1500);

            retPlanLoading.clearAnimation();
            retPlanLoading.setVisibility(View.VISIBLE);
            retPlanLoading.startAnimation(loading_animation);

            if (loggedInCustomer.getRetPlanCallback()) {
                retPlanLoading.clearAnimation();
                retPlanLoading.setVisibility(View.GONE);
                if (loggedInCustomer.customerHasRetPlan()) {

                    loadAccounts(loggedInCustomer.getRetirementPlanAccounts(), R.id.retplan_view, R.id.retplan_list);

                    rootView.findViewById(R.id.retplan_view).setVisibility(View.VISIBLE);

                    final int visibility = loggedInCustomer.hasRpBadge() ? View.VISIBLE : View.GONE; // Choose if visible or not depend on badge flag

                    Utils.hideOrShowView(visibility, retPlanNewBadge); // Show or hide UI component depend on visibility
                }
            } else {

                loggedInCustomer.setRetPlanCallback(true);

                RetirementPlanTasks.retirementPlanInfo(getActivity(), new ResponderListener() {
                    @Override
                    public void responder(String responderName, final Object data) {
                        //UI changes should run on ui thread
                        if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                retPlanLoading.clearAnimation();
                                retPlanLoading.setVisibility(View.GONE);

                                RetirementPlanInfoResponse newRetirementPlanInfoResponse = new RetirementPlanInfoResponse();

                                if (data instanceof RetirementPlanInfoResponse) {
                                    newRetirementPlanInfoResponse = (RetirementPlanInfoResponse) data;//object to manage a TsysLoyaltyRewardsInfo
                                }

                                loadRetirementPlanSection(newRetirementPlanInfoResponse, loggedInCustomer);

                            }
                        });
                    }
                    }

                    @Override
                    public void sessionHasExpired() {
                        App.getApplicationInstance().reLogin(getActivity());
                    }
                });
            }

        } else {
            loadUnavailableRetPlanSection(App.getApplicationInstance().getGlobalStatus().isReterimentPlanEnabled());
        }
    }

    /**
     * <p> Load Retirement Plan Section when is unavailable</p>
     *
     * @param globalEntitlementStatus boolean
     */
    private void loadUnavailableRetPlanSection(boolean globalEntitlementStatus) {
        if (App.getApplicationInstance().getCustomerEntitlements().hasRetirementPlan()) {
            rootView.findViewById(R.id.retplan_view).setVisibility(View.VISIBLE);

            final int[] show_downtime_text = new int[]{View.VISIBLE, R.id.retplan_view_csr_downtimes_text}; // Show text which indicate retplan not available (by csr-tool)
            final int[] hide_retplan_view = new int[]{View.GONE, R.id.retplan_view}; // Hide retirement plan section totally

            final int[] hide_or_show_retplan = (!globalEntitlementStatus) ? show_downtime_text : hide_retplan_view; // Determinate if show a text message or hide totally retirement plan account section

            Utils.hideOrShowView(hide_or_show_retplan[0], rootView.findViewById(hide_or_show_retplan[1])); // According global entitlement hide totally retirement plan section or show text message
        }
    }

    private void loadRetirementPlanSection(final RetirementPlanInfoResponse newRetirementPlanInfoResponse, final Customer loggedInCustomer ){

        if (newRetirementPlanInfoResponse.getHasRetPlan()) {

            loggedInCustomer.setHasRetPlan(newRetirementPlanInfoResponse.getHasRetPlan());

            loggedInCustomer.setRpBadge(newRetirementPlanInfoResponse.getRetirementPlanAccountNewBadge());

            LinkedList<RetirementPlanDetail> newRetirementPlanDetail = newRetirementPlanInfoResponse.getRetirementPlans();

            List<CustomerAccount> retirementPlanList = new ArrayList<>();

            for (final RetirementPlanDetail plan : newRetirementPlanDetail) {

                CustomerAccount newCustomerAccount = new CustomerAccount();

                final String rpSubType = "RTP"; //MBFIS-841
                final int imgRetPlanSrc = R.drawable.account_image_retirement_plan;// MBSE 2434

                newCustomerAccount.setShowStatement(false);
                newCustomerAccount.setBalanceColorRed(false);
                newCustomerAccount.setSubtype(rpSubType);  //MBFIS-841

                //MBFIS-461
                newCustomerAccount.setHref(MiBancoConstants.ACCS_PLATFRM_URL);
                newCustomerAccount.setAccountLast4Num(plan.getPlanLast4Num());
                newCustomerAccount.setImgResource(imgRetPlanSrc);

                Double amountDouble = Double.valueOf(plan.getPlanTotalBalance());

                newCustomerAccount.setPortalBalance(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(amountDouble));
                newCustomerAccount.setNickname(plan.getPlannam());

                retirementPlanList.add(newCustomerAccount);

                //MBFIS-588
                if (plan == newRetirementPlanDetail.getLast()) {
                    newCustomerAccount.setFooterPreviewTextId(R.string.retplan_preview_terms_and_conditions);
                    newCustomerAccount.setFooterFullTextId(R.string.retplan_full_terms_and_conditions);
                }

            }

            loggedInCustomer.setRetirementPlanAccounts(retirementPlanList);
            loadAccounts(loggedInCustomer.getRetirementPlanAccounts(), R.id.retplan_view, R.id.retplan_list);

            final int visibility = loggedInCustomer.hasRpBadge() ? View.VISIBLE : View.GONE; // Choose if visible or not depend on badge flag

            Utils.hideOrShowView(visibility, retPlanNewBadge);

            rootView.findViewById(R.id.retplan_view).setVisibility(View.VISIBLE);
        }
    }

    private void loadAccounts(final List<CustomerAccount> listItems, final int sectionResId, final int listResId) {
        final LinearLayout listLayout = (LinearLayout) rootView.findViewById(listResId);
        listLayout.removeAllViews();
        if (listItems.size() == 0) {
            rootView.findViewById(sectionResId).setVisibility(View.GONE);
        } else {
            for (final CustomerAccount acc : listItems) {
                listLayout.addView(getView(acc));
            }
        }
    }

    private View getView(final CustomerAccount account) {
        ViewHolder holder = null;
        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewItem = inflater.inflate(R.layout.list_item_accounts, null);
        viewItem.setTag(holder);
        holder = new ViewHolder(viewItem);

        holder.getMainLayout().setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                List<String> accountsActionDisabled = Arrays.asList(getActivity().getResources().getStringArray(R.array.accounts_action_disabled));

                if (!accountsActionDisabled.contains(account.getSubtype())) {

                    if (account.showStatement()) {
                        final Intent intent = new Intent(getActivity(), AccountDetails.class);//variable to invoke an specific component
                        intent.putExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY, account);
                        startActivity(intent);

                    } else if (App.getApplicationInstance().getPremiaInfo().isPremiaEnabled() && !Utils.isBlankOrNull(account.getSubtype())
                            && account.getSubtype().equalsIgnoreCase(getContext()
                            .getResources().getString(R.string.premia_account_subtype))) {
                        redirectToPremiaCatalog(account);

                    } else if (!Utils.isBlankOrNull(account.getHref()) && !account.getSubtype().equals(MLA)) {
                        BPAnalytics.logEvent(EVENT_RET_PLAN_TOUCHED);
                        Utils.openExternalUrl(getContext(), account.getHref());

                    }
                }
            }
        });

        setAccountFooter(account, holder);

        holder.getName().setText(account.getNickname());
        holder.getComment().setText(account.getAccountLast4Num());
        final TextView valueView = holder.getValue();
        valueView.setText(account.getPortalBalance());
        if (account.isBalanceColorRed()) {
            valueView.setTextColor(ContextCompat.getColor(getActivity(), R.color.accounts_debit_balance));
            valueView.setText(valueView.getText());
        }

        if (FeatureFlags.ONOFF() && account.getOnOffCount() > 0) {

            holder.getOnOffView().setVisibility(View.VISIBLE);
            if (isOnOffCounterAnimationEnabled) {
                holder.getOnOffView().setAlpha(0f);
                holder.getOnOffView().animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(null);
            }
            holder.getOnOffCounter().setText(Integer.toString(account.getOnOffCount()));

        } else {
            holder.getOnOffView().setVisibility(View.GONE);
        }

        if (FeatureFlags.CASH_REWARDS() && account.isCashRewardsEligible()) {
            holder.getTsysLoyaltyRewardsView().setVisibility(View.VISIBLE);
            TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo = account.getTsysLoyaltyRewardsInfo();

            if (tsysLoyaltyRewardsInfo == null) {
                loadTsysLoyaltyRewardsInfo(account, holder);
            } else {

                holder.getTsysLoyaltyRewardsBalanceTextView()
                        .setText(tsysLoyaltyRewardsInfo.getAvailableRewardsBalance());
            }
        }

        if (App.getApplicationInstance().getPremiaInfo().isPremiaEnabled() && !Utils.isBlankOrNull(account.getSubtype())
                && account.getSubtype().equalsIgnoreCase(getContext()
                .getResources().getString(R.string.premia_account_subtype))) {
            holder.getPremiaBalanceView().setVisibility(View.VISIBLE);
            String premiaBalance = account.getPremiaBalance();

            if (premiaBalance == null || premiaBalance.isEmpty()) {
                loadPremiaBalance(account, holder);
            } else {
                valueView.setText(premiaBalance);
            }
        }

        final String path = Utils.getAccountImagePath(account, getContext());//to get image path
        if (path == null) {
            holder.getImage().setImageResource(account.getImgResource());
        } else {
            final Bitmap tmp = getAccountBitmap(path);
            if (tmp != null) {
                holder.getImage().setImageBitmap(tmp);
            } else {
                holder.getImage().setImageResource(account.getImgResource());
            }
        }
        return viewItem;
    }

    /**
     * @param account Data with the text to fill the footer
     * @param holder  Abstraction layer for the ViewItem ListItemAccount
     */
    private void setAccountFooter(final CustomerAccount account, ViewHolder holder) {
        if (account.getFooterPreviewTextId() != null) {
            holder.getFooterLayout().setVisibility(View.VISIBLE);
            final SpannableString footerText = new SpannableString(this.getString(account.getFooterPreviewTextId())); //Footer text for account list item
            TextView footer = holder.getFooter();//Represents the text below the account info
            footer.setTextColor(ContextCompat.getColor(getContext(), R.color.accounts_item_footer));
            footer.setText(footerText);

            if (account.getFooterFullTextId() != null) {
                final String clickableText = this.getString(R.string.accounts_item_footer_more);//The clickable text of the footer
                SpannableString ss = new SpannableString(footerText + clickableText);//The concatenation of footer plus clickable text
                final ClickableSpan clickableSpan;//Clickable span
                clickableSpan = new ClickableSpan() {

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }

                    @Override
                    public void onClick(@NonNull View view) {
                        final BtmSheetModalFrgmnt modal;//Modal to show the full text
                        modal = new BtmSheetModalFrgmnt();
                        Bundle arguments = new Bundle();//Bundle to pass params to the modal
                        arguments.putCharSequence(FOOTER_FULL_TEXT_KEY, getActivity().getString(account.getFooterFullTextId()));
                        modal.setArguments(arguments);
                        modal.show(getActivity().getSupportFragmentManager(), "");
                    }
                };
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.blue)),
                        ss.length() - clickableText.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(clickableSpan, ss.length() - clickableText.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                footer.setText(ss);
                footer.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, getResources().getDisplayMetrics()), 1.0f);
            }
            footer.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }

    private void loadTsysLoyaltyRewardsInfo(final CustomerAccount account, final ViewHolder holder) {

        Animation loading_animation = AnimationUtils.loadAnimation(getContext(), R.anim.quick_spin_animation);
        loading_animation.reset();

        holder.getTsysLoyaltyRewardsLoading().clearAnimation();
        holder.getTsysLoyaltyRewardsLoading().setVisibility(View.VISIBLE);
        holder.getTsysLoyaltyRewardsLoading().startAnimation(loading_animation);

        App.getApplicationInstance().getAsyncTasksManager().getTsysLoyaltyRewardsInfoTask(getActivity(),
                account.getFrontEndId(), new ResponderListener() {
                    @Override
                    public void responder(String responderName, final Object data) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.getTsysLoyaltyRewardsLoading().clearAnimation();
                                    holder.getTsysLoyaltyRewardsLoading().setVisibility(View.GONE);

                                    if (data == null) {
                                        holder.getTsysLoyaltyRewardsBalanceTextView().setText(
                                                R.string.tsys_loyalty_rewards_no_balance_placeholder);
                                        return;
                                    }
                                    TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo = (TsysLoyaltyRewardsInfo) data;
                                    if (tsysLoyaltyRewardsInfo.getAvailableRewardsBalance() == null
                                            || tsysLoyaltyRewardsInfo.getAvailableRewardsBalance()
                                            .isEmpty()) {
                                        tsysLoyaltyRewardsInfo.setAvailableRewardsBalance(getString(
                                                R.string.tsys_loyalty_rewards_no_balance_placeholder));
                                    }
                                    account.setTsysLoyaltyRewardsInfo(tsysLoyaltyRewardsInfo);
                                    holder.getTsysLoyaltyRewardsBalanceTextView().setText(
                                            tsysLoyaltyRewardsInfo.getAvailableRewardsBalance());
                                }
                            });
                        }
                    }
                    @Override
                    public void sessionHasExpired() {
                        App.getApplicationInstance().reLogin(getActivity());
                    }
                });
    }

    private Bitmap getAccountBitmap(final String path) {
        if (accountsImages.get(path) != null && accountsImages.get(path).get() != null) {
            return accountsImages.get(path).get();
        }
        final Bitmap tmp = BitmapFactory.decodeFile(path);
        accountsImages.put(path, new WeakReference<Bitmap>(tmp));
        return tmp;
    }

    private void loadPremiaBalance(final CustomerAccount account, final ViewHolder holder) {
        final Animation loading_animation = AnimationUtils.loadAnimation(getContext(), R.anim.quick_spin_animation);//to add vissual animations
        loading_animation.reset();
        loading_animation.setInterpolator(new LinearInterpolator());
        loading_animation.setDuration(1500);
        final TextView valueView = holder.getValue();
        valueView.setVisibility(View.GONE);
        holder.getPremiaBalanceLoading().clearAnimation();
        holder.getPremiaBalanceLoading().setVisibility(View.VISIBLE);
        holder.getPremiaBalanceLoading().startAnimation(loading_animation);
        PremiaTasks.premiaAccountBalance(getActivity(), new ResponderListener() {
            @Override
            public void responder(String responderName, final Object data) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.getPremiaBalanceLoading().clearAnimation();
                            holder.getPremiaBalanceLoading().setVisibility(View.GONE);
                            if (data == null) {
                                valueView.setText(R.string.premia_no_balance_placeholder);
                                valueView.setVisibility(View.VISIBLE);
                                return;
                            }
                            String premiaBalance = getString(R.string.premia_no_balance_placeholder);
                            if (data instanceof String) {
                                premiaBalance = (String) data;
                            }
                            if (premiaBalance == null
                                    || premiaBalance.isEmpty()) {
                                premiaBalance = getString(
                                        R.string.premia_no_balance_placeholder);
                            }
                            account.setPremiaBalance(premiaBalance);

                            valueView.setText(premiaBalance);
                            valueView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            @Override
            public void sessionHasExpired() {
                App.getApplicationInstance().reLogin(getActivity());
            }
        });
    }

    /**
     * Redirect to Premia Catalog.
     *
     * @param account
     * @return
     */
    private void redirectToPremiaCatalog(CustomerAccount account) {
        PremiaTasks.premiaCatalogRedirect(getActivity(), account.getFrontEndId(), new ResponderListener() {
            @Override
            public void responder(String responderName, final Object data) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data == null) {
                                return;
                            } else if (data instanceof String) {
                                final String premiaCatalogRedirectURL = (String) data; // Premia URL.
                                Utils.openExternalUrl(getActivity(), premiaCatalogRedirectURL);
                            }
                        }
                    });
                }
            }
            @Override
            public void sessionHasExpired() {
                App.getApplicationInstance().reLogin(getActivity());
            }
        });

    }

    public AppCompatDelegate getDelegateCompat() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            return activity.getDelegate();
        }
        return null;
    }

    public View getViewFromFragment(int viewId) {
        if (getView() != null) {
            return getView().findViewById(viewId);
        }
        return null;
    }
}