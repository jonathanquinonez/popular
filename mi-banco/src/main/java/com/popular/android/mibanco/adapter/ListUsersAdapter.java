package com.popular.android.mibanco.adapter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.object.ListItemSelectable;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;
import com.popular.android.mibanco.widget.BalanceWidgetProvider;

import java.util.ArrayList;

/**
 * Adapter class to manage users in the manage user's list
 */
public class ListUsersAdapter extends BaseAdapter implements OnClickListener {

    private final Context context;

    private LayoutInflater inflater;

    private final ArrayList<String> items;

    private final int mLayoutResourceId;



    public ListUsersAdapter(final Context context, final int layoutResourceId, final ArrayList<String> aItems) {
        items = aItems;
        this.context = context;
        mLayoutResourceId = layoutResourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void addAll(final ArrayList<String> items) {
        this.items.addAll(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Class that represents data in the manage username row in list
     */
    public static class ViewHolder {

        public Button button;

        public TextView username;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(mLayoutResourceId, null);
            ViewHolder holder = new ViewHolder();
            holder.username = (TextView) myConvertView.findViewById(R.id.item_username);
            holder.button = (Button) myConvertView.findViewById(R.id.button);
            myConvertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) myConvertView.getTag();
        holder.button.setOnClickListener(this);
        holder.button.setTag(position);
        boolean maskUser = FeatureFlags.MASK_USERNAME() && App.getApplicationInstance().getCurrentUser() == null;
        String username = maskUser? Utils.maskUsername(items.get(position), MiBancoConstants.MASK_PATTERN_LENGTH) : items.get(position);
        holder.username.setText(username);
        return myConvertView;
    }

    public void remove(final int position) {
        items.remove(position);
        notifyDataSetChanged();
    }

    public void remove(final ListItemSelectable item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        final int position = (Integer) v.getTag();
        final DialogHolo dialog = new DialogHolo(context);
        dialog.setCancelable(true);
        dialog.setMessage(context.getString(R.string.confirm_delete_user));
        dialog.setNoTitleMode();
        dialog.setPositiveButton(context.getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final App app = (App) ((Activity) context).getApplication();

                String usernameToRemove = items.get(position);

                Utils.removeUsername(app.getApplicationContext(), usernameToRemove);
                remove(position);

                // Remove widget data
                SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(context);
                String widgetUsername = sharedPreferences.getString("widget_username", "");
                if(widgetUsername != null && usernameToRemove.equals(widgetUsername)){
                	SharedPreferences.Editor editor = sharedPreferences.edit();
                	editor.putString("widget_username", "");
                	editor.putString("widget_balances", "");
                	editor.putString("widget_customer_token", "");
                	editor.putString("widget_get_balance", "");
                	editor.putString("widget_lastupdatedon", "");
                	editor.commit();
                	
                	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    				int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, BalanceWidgetProvider.class));
    				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
                }
                BPAnalytics.logEvent(BPAnalytics.EVENT_EDITED_SAVED_USERNAMES);

                Utils.dismissDialog(dialog);
                if (items.size() == 0) {
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.no_more_users), Toast.LENGTH_LONG).show();
                    ((Activity) context).finish();
                }
            }
        });
        dialog.setNegativeButton(context.getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });
        Utils.showDialog(dialog, context);
    }
}
