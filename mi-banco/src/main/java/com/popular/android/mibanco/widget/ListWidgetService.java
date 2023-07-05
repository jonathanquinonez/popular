package com.popular.android.mibanco.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListWidgetFactory(this.getApplicationContext(), intent);
    }
}

/**
 * Provides the app widget with the data for the items in its collection
 *
 */
class ListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    
	private App application;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private List<AccountNameAndBalanceItem> mWidgetItems = new ArrayList<>();

    public ListWidgetFactory(Context context, Intent intent) {
        mContext = context;
        mSharedPreferences = Utils.getSecuredSharedPreferences(context);
    }

    public void onCreate() {
		String accountsAndBalances = mSharedPreferences.getString("widget_balances", "");
		createAccountList(accountsAndBalances);
		application = App.getApplicationInstance();
		application.initDeviceIdParams();
    }

	@Override
	public int getCount() {
		return mWidgetItems.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
		String accountName = mWidgetItems.get(position).getAccountName();
		String accountSuffix = " ("+mWidgetItems.get(position).getAccountSuffix()+") ";
		
		if((accountName+accountSuffix).length()<26){
			rv.setViewVisibility(R.id.weightedWidgetAcctName, View.GONE);
			rv.setViewVisibility(R.id.widgetAcctName, View.VISIBLE);
		}else{
			rv.setViewVisibility(R.id.weightedWidgetAcctName, View.VISIBLE);
			rv.setViewVisibility(R.id.widgetAcctName, View.GONE);
		}
		rv.setTextViewText(R.id.weightedWidgetAcctName, accountName);
        rv.setTextViewText(R.id.widgetAcctName, accountName);
        rv.setTextViewText(R.id.widgetAcctSuffix, accountSuffix);
        rv.setTextViewText(R.id.widgetAcctBalance, " "+mWidgetItems.get(position).getBalance());
        rv.setTextColor(R.id.widgetAcctBalance, mWidgetItems.get(position).isRedBalance()?mContext.getResources().getColor(R.color.red):mContext.getResources().getColor(R.color.black));
        return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}


	@Override
	public void onDataSetChanged() {

		// Get the customer object from a mobile balance info call
		try {
			if("".equals(mSharedPreferences.getString("widget_username", ""))){
				updateWidget("", ContentType.LOGIN);
			}else{
				Customer customer = null;
				String isGetBalance = mSharedPreferences.getString("widget_get_balance", "");
				if("true".equals(isGetBalance)){
					String customerToken = mSharedPreferences.getString("widget_customer_token", "");
					String deviceId = mSharedPreferences.getString("widget_device_identifier", "");
					customer = application.getApiClient().getBalances(customerToken+deviceId);
				}
				
				if(customer != null && 
						((customer.getCreditCards() != null && customer.getCreditCards().size() > 0) 
								||(customer.getDepositAccounts() != null && customer.getDepositAccounts().size() > 0))) {
					
					LinkedList<CustomerAccount> accounts = new LinkedList<>();
					if(customer.getDepositAccounts() != null && customer.getDepositAccounts().size() > 0)
						accounts.addAll(customer.getDepositAccounts());
					
					if(customer.getCreditCards() != null && customer.getCreditCards().size() > 0)
						accounts.addAll(customer.getCreditCards());
					
					
					String accountsAndBalances = "";
					for(CustomerAccount account : accounts) {
						accountsAndBalances += account.getNickname() + "><"; 
						accountsAndBalances += account.getAccountLast4Num() + (!account.getAccountNumberSuffix().equals("") ? " " + account.getAccountNumberSuffix() : "") + "><";
						accountsAndBalances += account.getPortalBalance() + "><";
						accountsAndBalances += account.isBalanceColorRed()?"R;":"B;";
					}
					if(mSharedPreferences != null)
						mSharedPreferences = Utils.getSecuredSharedPreferences(mContext);
					
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putString("widget_lastupdatedon", Calendar.getInstance().getTime().toString());
					editor.commit();
					updateWidget(accountsAndBalances, ContentType.BALANCES);
					
				}
				else{
					String accountsAndBalances = mSharedPreferences.getString("widget_balances", "");
					updateWidget(accountsAndBalances, ContentType.BALANCES);
				}
			}
		} catch (Exception e) {
			// Do not crash if an error occurs
			String accountsAndBalances = mSharedPreferences.getString("widget_balances", "");
			updateWidget(accountsAndBalances, ContentType.BALANCES);
		}
	}
	
	private void updateWidget(String accountsAndBalances, ContentType content) {
		displayContent(mContext, content);
		
		if(content ==  ContentType.BALANCES)
			createAccountList(accountsAndBalances);

		// Send the update intent to the widget
		Intent intent = new Intent(mContext, BalanceWidgetProvider.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		int[] ids = {1};
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		mContext.sendBroadcast(intent);
	}
	
	private void createAccountList(String accountInfoString){
		int indexAccountName= 0;
		int indexAccountSuffix = 1;
		int indexAccountBalance = 2;
		int indexBalanceColor = 3;

		if(mSharedPreferences == null)
			mSharedPreferences = Utils.getSecuredSharedPreferences(mContext);
		
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString("widget_balances", accountInfoString);
		editor.commit();
	
		String[] unparsedAccountInformation = accountInfoString.split(";");
		mWidgetItems.clear();
		for(String information: unparsedAccountInformation) {
			String[] parsedAccountInformation = information.split("><");
			if(parsedAccountInformation.length == 4){
				
				AccountNameAndBalanceItem acctInfo = new AccountNameAndBalanceItem();
				acctInfo.setAccountName(parsedAccountInformation[indexAccountName].trim());
				acctInfo.setAccountSuffix(parsedAccountInformation[indexAccountSuffix].trim());
				acctInfo.setBalance(parsedAccountInformation[indexAccountBalance].trim());
				acctInfo.setRedBalance("R".equalsIgnoreCase(parsedAccountInformation[indexBalanceColor]));
				mWidgetItems.add(acctInfo);
			}
		}
	}
	
	
	
	private void displayContent(Context context, ContentType contentType){
		RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_layout);
		switch(contentType){
		case LOGIN:
			views.setViewVisibility(R.id.balanceWidgetContent, View.GONE);
			views.setViewVisibility(R.id.messageWidgetContent, View.VISIBLE);
			
			views.setTextViewText(R.id.messageTitle, context.getText(R.string.welcome_back));
			views.setTextViewText(R.id.textMessageAction, context.getText(R.string.please_sign_in));
			views.setTextViewText(R.id.messageInstructions, context.getText(R.string.see_balance_instructions));
			break;
		case BALANCES:
			views.setViewVisibility(R.id.balanceWidgetContent, View.VISIBLE);
			views.setViewVisibility(R.id.messageWidgetContent, View.GONE);
			break;
		case HIDDEN_ACCOUNTS:
			break;
		}
	}

	@Override
	public void onDestroy() {
		mWidgetItems.clear();
	}
}
