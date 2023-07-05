package com.popular.android.mibanco.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//import com.popular.android.mibanco.activity.LocatorTabs;

public class BalanceWidgetProvider extends AppWidgetProvider {
	
	public static final String EXTRA_ITEM = "com.popular.android.mibanco.widget.EXTRA_ITEM";
	public static final String GET_BALANCES = "com.popular.android.mibanco.widget.GET_BALANCES";
	public static final String NAV_LOGIN = "com.popular.android.mibanco.widget.NAV_LOGIN";
	public static final String NAV_PAYMENTS = "com.popular.android.mibanco.widget.NAV_PAYMENTS";
	public static final String NAV_TRANSFERS = "com.popular.android.mibanco.widget.NAV_TRANSFERS";
	public static final String NAV_LOCATOR = "com.popular.android.mibanco.widget.NAV_LOCATOR";

	private static final int REFRESH_MINUTES = 10;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		final int N = appWidgetIds.length;
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		String lastUpdatedOn = "";
		
		// Set the language
		final Configuration config = context.getResources().getConfiguration();
		final String lang = sharedPreferences.getString("language", MiBancoConstants.ENGLISH_LANGUAGE_CODE);
		if (!config.locale.getLanguage().equals(lang)) {
			Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
		}
		
		// Necessary for binding widget items to actions
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			
			// Get the balance information - if there is none, we need to log in
			String balances = sharedPreferences.getString("widget_balances", "");
			String username = sharedPreferences.getString("widget_username", "");
			
			if(username != null && !username.equals("")
					&& balances != null && !balances.equals("")) {
				displayContent(context, views, ContentType.BALANCES);
				
				// Get the last updated on date
				lastUpdatedOn = sharedPreferences.getString("widget_lastupdatedon", "");
				if(lastUpdatedOn != null && !lastUpdatedOn.equals("")) {
					
					// Parse the date format
					SimpleDateFormat englishFormatter = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.US);
					SimpleDateFormat spanishFormatter = new SimpleDateFormat("d 'de' MMMM 'de' yyyy h:mm a", new Locale("es", "ES"));
					try {
						Date todayDate = new Date();
						Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(lastUpdatedOn);
						
						String formattedDate = context.getString(R.string.last_updated_on);
						if(Utils.dateResetTime(date).equals(Utils.dateResetTime(todayDate))){
							if(lang.equals("es")){
								formattedDate +=" hoy a las "+(new SimpleDateFormat("h:mm a", new Locale("es", "ES"))).format(date);
							}else{
								formattedDate +=" Today at "+(new SimpleDateFormat("h:mm a", Locale.US)).format(date);
							}
						}else{
							if(lang.equals("es"))
								formattedDate += " " + spanishFormatter.format(date);
							else
								formattedDate += " " + englishFormatter.format(date);
								
						}

						views.setTextViewText(R.id.lastUpdatedOnText, formattedDate);
						
						editor.putString("widget_get_balance", "false");
						editor.commit();
						appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
						appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
					} catch (ParseException e) {
						// Do not show the information if there was a parse exception
						views.setTextViewText(R.id.lastUpdatedOnText, "");
						appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
					}
				}
			}
			else {
				displayContent(context, views, ContentType.LOGIN);
				
				// Clear out the account list
				editor.putString("widget_balances", "");
				editor.putString("widget_lastupdatedon", "");
				editor.commit();
				appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
			}
			
			// Set up a pending intent for the refresh icon
			Intent refreshIntent = new Intent(context, BalanceWidgetProvider.class);
			refreshIntent.setAction(BalanceWidgetProvider.GET_BALANCES);
			refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent refreshPendingIntent = createPendingIntentGetBroadCast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.refreshIcon, refreshPendingIntent);
			views.setOnClickPendingIntent(R.id.lastUpdatedOnText, refreshPendingIntent);
			views.setOnClickPendingIntent(R.id.refreshSection, refreshPendingIntent);
			
			// Set the pending intents for all the buttons
			Intent loginIntent = new Intent(context, BalanceWidgetProvider.class);
			loginIntent.setAction(BalanceWidgetProvider.NAV_LOGIN);
			loginIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent loginPendingIntent = createPendingIntentGetBroadCast(context, 0, loginIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.loginImg, loginPendingIntent);
			views.setOnClickPendingIntent(R.id.textMessageAction, loginPendingIntent);
			
			Intent paymentsIntent = new Intent(context, BalanceWidgetProvider.class);
			paymentsIntent.setAction(BalanceWidgetProvider.NAV_PAYMENTS);
			paymentsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent paymentsPendingIntent = createPendingIntentGetBroadCast(context, 0, paymentsIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.paymentsImg, paymentsPendingIntent);
			
			Intent transfersIntent = new Intent(context, BalanceWidgetProvider.class);
			transfersIntent.setAction(BalanceWidgetProvider.NAV_TRANSFERS);
			transfersIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent transfersPendingIntent = createPendingIntentGetBroadCast(context, 0, transfersIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.transfersImg, transfersPendingIntent);
			
			Intent locatorIntent = new Intent(context, BalanceWidgetProvider.class);
			locatorIntent.setAction(BalanceWidgetProvider.NAV_LOCATOR);
			locatorIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent locatorPendingIntent = createPendingIntentGetBroadCast(context, 0, locatorIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.locatorImg, locatorPendingIntent);
            
			// Bind the list
			Intent intent = new Intent(context, ListWidgetService.class);
	        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
	        views.setRemoteAdapter(appWidgetId, R.id.listView, intent);
	        appWidgetManager.updateAppWidget(new ComponentName(context, BalanceWidgetProvider.class), views);		
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}


	private static PendingIntent createPendingIntentGetBroadCast(Context context, int id, Intent intent, int flag){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE | flag);
		} else {
			return PendingIntent.getBroadcast(context, id, intent, flag);
		}
	}

	private void displayContent(Context context, RemoteViews views, ContentType contentType){
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
	public void onReceive(final Context context, Intent intent) {

		App application = App.getApplicationInstance();
		if (intent.getAction().equals(GET_BALANCES)) {
			SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(context);
			String lastUpdatedOn = sharedPreferences.getString("widget_lastupdatedon", "");
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			if(lastUpdatedOn != null && !lastUpdatedOn.equals("")) {
				this.displayContent(context, views, ContentType.BALANCES);
				Date todaysDate = new Date();
				Date lastUpdate;
				try {
					lastUpdate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(lastUpdatedOn);
				
	
					long t=lastUpdate.getTime();
					Date afterAddingTenMins= new Date(t + (REFRESH_MINUTES * 60000));
					
					// 10 minutes verification
					if(!(afterAddingTenMins.before(todaysDate))){
						long duration  = afterAddingTenMins.getTime() - todaysDate.getTime();
						long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
						
						String refreshMessage = context.getString(R.string.refresh_message).replace("(?)", String.valueOf(diffInMinutes));
						if(diffInMinutes == 0)
							refreshMessage = context.getString(R.string.refresh_message2);
						
						Toast toast = Toast.makeText(context, refreshMessage, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}else{
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("widget_get_balance", "true");
						editor.commit();
						
						AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
						int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, BalanceWidgetProvider.class));
						appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
		
			}
		}
		else if(intent.getAction().equals(NAV_LOGIN)) {
			if(application != null) {
				application.clearSessionDataFromWidget();
				restartApplication(context, application);
			}
		}
		else if(intent.getAction().equals(NAV_PAYMENTS)) {
			if(application != null) {
				application.setWidgetCalledPayments(true);
				application.setWidgetCalledTransfers(false);
				application.clearSessionDataFromWidget();
				restartApplication(context, application);
			}
		}
		else if(intent.getAction().equals(NAV_TRANSFERS)) {
			if(application != null) {
				application.setWidgetCalledTransfers(true);
				application.setWidgetCalledPayments(false);
				application.clearSessionDataFromWidget();
				restartApplication(context, application);
			}
		}
		else if(intent.getAction().equals(NAV_LOCATOR)) {
			Utils.openExternalUrl(context, context.getString(R.string.locator_url));
		}
		super.onReceive(context, intent);
	}
	
	private void restartApplication(Context context, App application) {
		final Intent kIntent = new Intent(MiBancoConstants.KILL_ACTION);
        kIntent.setType(MiBancoConstants.KILL_TYPE);
        context.sendBroadcast(kIntent);

		final Intent introScreenIntent = new Intent(context, IntroScreen.class);
		introScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		application.startActivity(introScreenIntent);
	}
}
