package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.ws.response.Content;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.GlobalStatus;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.task.MobileCashTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.EasyCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrx;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;


@RunWith(PowerMockRunner.class)
@PrepareForTest({EasyCashStaging.class, ReportFragment.class,
        MiBancoConstants.class, R.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, ContextCompat.class, BPAnalytics.class,View.class,
        MobileCashTrx.class,MobileCashTasks.class,MobileCashTasks.MobileCashListener.class,MobileCashUtils.class,
        Double.class})
public class EasyCashStagingUT {
    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private App app;

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private Resources resources;

    @Mock
    private AsyncTasks asyncTasks;

    @Mock
    private CustomerAccount account;

    @Mock
    private Intent intent;

    @Mock
    private LinearLayout linearLayout;

    @Mock
    private TextView textView;

    @Mock
    private Button button;

    @Mock
    private EditText editText;

    @Mock
    private GlobalStatus globalStatus;

    @Mock
    private View view;

    @Mock
    MobileCashTrx mcTransaction;

    @Mock
    DialogInterface dialogInterface;

    @Mock
    MobileCashTasks.MobileCashListener<EasyCashTrx> mobileCashListener;

    @Mock
    MobileCashTasks mobileCashTasks;

    @Mock
    EasyCashTrx easyCashTrx;

    @Mock
    Content content;

    @InjectMocks
    private EasyCashStaging easyCashStaging;

    @Mock
    PhonebookContact contactTo;

    @Mock
    AccountCard selectedAccount;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(Double.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.mockStatic(MobileCashTasks.class);
        PowerMockito.mockStatic(MobileCashTasks.MobileCashListener.class);
        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(BPAnalytics.class);
        PowerMockito.mockStatic(MobileCashUtils.class);

        easyCashStaging = PowerMockito.spy(new EasyCashStaging());

        PowerMockito.doReturn(appDelegate).when(easyCashStaging).getDelegate();

        when(easyCashStaging.getIntent()).thenReturn(intent);
        when(app.getGlobalStatus()).thenReturn(globalStatus);

    }

    @Ignore
    @Test
    public void whenOnCreate_GivenConfParameters_forme_forother_thenCheckValues_maxAmount_maxAmountCashDrop_noEmpty() throws Exception {
        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.viewAthCard)).thenReturn(linearLayout);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        when(easyCashStaging.findViewById(R.id.tvAccountHint)).thenReturn(textView);
        when(easyCashStaging.findViewById(R.id.tvRecipientHint)).thenReturn(textView);
        when(easyCashStaging.findViewById(R.id.tvTransferAmount)).thenReturn(textView);
        when(easyCashStaging.findViewById(R.id.btnForMe)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.btnForOther)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.btnConfirmForOther)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.txt_optional_msg)).thenReturn(editText);
        when(globalStatus.isMobileCashForOthers()).thenReturn(Boolean.TRUE);
        PowerMockito.doNothing().when(easyCashStaging,"getEasyCashAccounts", any(Boolean.class), any(View.OnClickListener.class));
        easyCashStaging.onCreate(null);
        PowerMockito.verifyPrivate(easyCashStaging).invoke("viewChangesManagement",any(int.class));
    }


    @Test
    public void whenOnClicFormeWith() throws Exception {
        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.btnForMe)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "transferAmount", 1);
        Whitebox.setInternalState(easyCashStaging, "selectedAccount", cccountCard);
        when(easyCashStaging.getResources()).thenReturn(resources);
        when(resources.getString(R.string.mc_confirm_stage_title)).thenReturn("String");
        when(Utils.getFormattedDollarAmount(String.valueOf(1))).thenReturn("$ 1.00");
        when(resources.getString(R.string.mc_transfer_confirm, Utils.getFormattedDollarAmount(String.valueOf(1)),
                cccountCard.getNickname()+" "+ cccountCard.getAccountLast4Num())).thenReturn("Withdraw: Nick \\nFrom: $ 1.00");
        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");

        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.btnForMe);
        easyCashStaging.onClick(view);
    }


    @Test
    public void whenOnClickFormeWithDialogConfirm() throws Exception {

        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");
        cccountCard.setFrontEndId("1");
        AccountCard selectedCardFromAccount =  new AccountCard();
        selectedCardFromAccount.setAccountLast4Num("1234");
        selectedCardFromAccount.setAtmType("TypeATM");
        cccountCard.setSelectedCardFromAccount(selectedCardFromAccount);

        when(textView.getText()).thenReturn("$ 1.00");

        mobileCashTasks = PowerMockito.spy(new MobileCashTasks());

        when(mcTransaction.getAccountFrontEndId()).thenReturn(cccountCard.getAccountFrontEndId());

        Whitebox.setInternalState(easyCashStaging, "mcTransaction", mcTransaction);
        Whitebox.setInternalState(easyCashStaging, "tvTransferAmount", textView);
        Whitebox.setInternalState(easyCashStaging, "selectedAccount", cccountCard);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "mobileCashTasks", mobileCashTasks);
        when(textView.getText()).thenReturn("$ 1.00");

        mobileCashTasks = PowerMockito.spy(new MobileCashTasks());
        easyCashStaging.onClick(dialogInterface,-1);
    }

    @Test
    public void whenOnClickFormeMobileCashApiResponseBlackList() throws Exception {
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        when(easyCashTrx.getContent()).thenReturn(content);
        when(content.getBlackListStatus()).thenReturn("PHONE_IN_BLACKLIST");
        easyCashStaging.onMobileCashApiResponse(easyCashTrx);
    }


    @Test
    public void whenOnClickFormeMobileCashApiResponseError() throws Exception {
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        when(easyCashTrx.getContent()).thenReturn(content);
        easyCashStaging.onMobileCashApiResponse(easyCashTrx);
    }

    @Test
    public void whenOnClickFormeMobileCashApiResponse() throws Exception {

        Double sentAmount = 2.00;
        MobileCashTrx trx1 = new MobileCashTrx();
        trx1.setAccountLast4Num("1234");
        trx1.setReceiverPhone("123456789");
        trx1.setAmount("2.00");
        List<MobileCashTrx> trxList =  new ArrayList<MobileCashTrx>();
        trxList.add(trx1);

        EasyCashTrx result =  new EasyCashTrx();
        Content content2 =  new Content();
        content2.setStatus(MiBancoConstants.MOBILE_CASH_SUCCESS);
        content2.setTransactions(trxList);
        result.setContent(content2);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "mcTransaction", mcTransaction);
        when(easyCashTrx.getContent()).thenReturn(content);
        when(content.getStatus()).thenReturn("MOBILE_CASH_SUCCESS");
        when(mcTransaction.getAmount()).thenReturn("2.00");
        when(mcTransaction.getAccountLast4Num()).thenReturn("1234");
        when(Double.valueOf(any(String.class))).thenReturn(sentAmount);
        when(Utils.isBlankOrNull(any(String.class))).thenReturn(true);
        when(Utils.isBlankOrNull(mcTransaction.getTrxReceiptId())).thenReturn(false);

        easyCashStaging.onMobileCashApiResponse(result);
    }


    @Test
    public void whenOnClicSelectAccountMinBalance() throws Exception {
        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");

        ArrayList<AccountCard>  accountList =  new ArrayList<AccountCard>();
        accountList.add(cccountCard);

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.viewSelectAccount)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "transferAmount", 1);
        Whitebox.setInternalState(easyCashStaging, "accounts", accountList);
        when(easyCashStaging.getResources()).thenReturn(resources);
        when(resources.getString(R.string.mc_confirm_stage_title)).thenReturn("String");


        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.viewSelectAccount);
        easyCashStaging.onClick(view);
    }


    @Test
    public void whenOnClicSelectAccountBalance() throws Exception {
        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");

        ArrayList<AccountCard>  accountList =  new ArrayList<AccountCard>();
        accountList.add(cccountCard);

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.viewSelectAccount)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "transferAmount", 1);
        Whitebox.setInternalState(easyCashStaging, "accounts", accountList);
        when(easyCashStaging.getResources()).thenReturn(resources);
        when(resources.getString(R.string.mc_confirm_stage_title)).thenReturn("String");
        when(Utils.getAmountIntValue(any(String.class))).thenReturn(30);

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.viewSelectAccount);
        easyCashStaging.onClick(view);
    }

    @Test
    public void whenOnClicSelectAccountNull() throws Exception {
        ArrayList<AccountCard>  accountList =  new ArrayList<AccountCard>();

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.viewSelectAccount)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "transferAmount", 1);
        Whitebox.setInternalState(easyCashStaging, "accounts", accountList);
        when(easyCashStaging.getResources()).thenReturn(resources);
        when(resources.getString(R.string.mc_confirm_stage_title)).thenReturn("String");

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.viewSelectAccount);
        easyCashStaging.onClick(view);
    }



    @Test
    public void whenOnClicSelectContact() throws Exception {

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        when(easyCashStaging.getResources()).thenReturn(resources);
        when(resources.getString(R.string.mc_confirm_stage_title)).thenReturn("String");

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.selectContact);
        easyCashStaging.onClick(view);
    }


    @Test
    public void whenOnClicForOther() throws Exception {

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.btnForOther)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        when(easyCashStaging.getResources()).thenReturn(resources);

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.doNothing().when(easyCashStaging,"sendMoneyToOthersAction");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.btnForOther);
        easyCashStaging.onClick(view);
    }



    @Test
    public void whenOnClicConfirmForOtherValidNumberRaw() throws Exception {

        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.btnConfirmForOther)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "selectedAccount", cccountCard);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "contactTo", contactTo);
        when(contactTo.getRawPhoneNumber()).thenReturn("12345678910");

        when(easyCashStaging.getResources()).thenReturn(resources);


        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.btnConfirmForOther);
        easyCashStaging.onClick(view);
    }


    @Test
    public void whenOnClicConfirmForOther() throws Exception {

        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.btnConfirmForOther)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "selectedAccount", cccountCard);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "contactTo", contactTo);
        when(contactTo.getRawPhoneNumber()).thenReturn("123456789");

        when(easyCashStaging.getResources()).thenReturn(resources);

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.btnConfirmForOther);
        easyCashStaging.onClick(view);
    }


    @Test
    public void whenOnClicTransferAmountWithoutBalance() throws Exception {

        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");
        cccountCard.setBalance("");

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.tvTransferAmount)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "selectedAccount", cccountCard);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "maximumAmountForme", 30);
        Whitebox.setInternalState(easyCashStaging, "maximumAmountForOther", 30);

        when(Utils.isBlankOrNull(Matchers.<String>any())).thenReturn(true);
        when(Utils.getAmountIntValue(Matchers.<String>any())).thenReturn(20);
        when(easyCashStaging.getResources()).thenReturn(resources);

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.doNothing().when(easyCashStaging,"amountPicker");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.tvTransferAmount);
        easyCashStaging.onClick(view);
    }


    @Test
    public void whenOnClicTransferAmountMC_MIN_BALANCE_AMOUNT() throws Exception {

        AccountCard cccountCard =  new AccountCard();
        cccountCard.setNickname("nick");
        cccountCard.setAccountLast4Num("1234");
        cccountCard.setBalance("$20.00");

        doNothing().when(easyCashStaging).setContentView(R.layout.easycash_staging);
        when(easyCashStaging.findViewById(R.id.tvTransferAmount)).thenReturn(button);
        when(easyCashStaging.findViewById(R.id.selectContact)).thenReturn(linearLayout);
        Whitebox.setInternalState(easyCashStaging, "selectContact", linearLayout);
        Whitebox.setInternalState(easyCashStaging, "optionalMessage", editText);
        Whitebox.setInternalState(easyCashStaging, "btnConfirmSendToOther", button);
        Whitebox.setInternalState(easyCashStaging, "selectedAccount", cccountCard);
        Whitebox.setInternalState(easyCashStaging, "mContext", context);
        Whitebox.setInternalState(easyCashStaging, "maximumAmountForme", 20);

        when(easyCashStaging.getResources()).thenReturn(resources);

        PowerMockito.doNothing().when(easyCashStaging,"validateTransfer");
        PowerMockito.mockStatic(View.class);
        when(view.getId()).thenReturn(R.id.tvTransferAmount);
        easyCashStaging.onClick(view);
    }
}
