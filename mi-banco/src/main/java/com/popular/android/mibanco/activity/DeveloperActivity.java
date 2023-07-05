package com.popular.android.mibanco.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.SwitchCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

public class DeveloperActivity extends BaseActivity {

    private Context mContext;

    private String selectedPort;
    private TextView tvPortsOptions;
    private Button btnConfirm;
    private ListView savedUsersList;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        mContext = this;


        tvPortsOptions = (TextView) findViewById(R.id.tvPortsOptions);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        findViewById(R.id.custom_port).setOnClickListener(customPortOnClick);
        btnConfirm.setOnClickListener(confirmOnClick);
        tvPortsOptions.setOnClickListener(portsOnClick);

        selectedPort = Utils.getParsedPort();
        tvPortsOptions.setText(selectedPort);

        savedUsersList = (ListView) findViewById(R.id.listUsers);

        final ArrayList<String> values = FeatureFlags.getInstance().getFlagsKeys();

        final String[] listValues = values.toArray(new String[values.size()]);
        final DeveloperArrayAdapter adapter = new DeveloperArrayAdapter(this, R.layout.list_item_settings_switch, listValues);
        savedUsersList.setAdapter(adapter);
    }

    private String urlBase ()
    {
        switch (BuildConfig.BUILD_TYPE) {
            case "qa":
                return "https://cert.bancopopular.com:%s/cibp-web/";
            default:
                if(FeatureFlags.SDG_WIFI()) {
                    return this.getResources().getString(R.string.final_host) + "%s/cibp-web/";
                } else {
                    return this.getResources().getString(R.string.origin_host) + "%s/cibp-web/";
                }
        }

    }

    private String[] portsList()
    {
        String[] qaPorts = {"6443", "7443", "8443", "9443"};
        String[] testPorts = {"1212", "2020", "3030", "3131", "4040", "4141", "5050", "5151", "6060", "8080", "8181","9090"};
        switch (BuildConfig.BUILD_TYPE) {
            case "qa":
                return qaPorts;
            default:
                return testPorts;
        }
    }

    protected void saveNewURL(String url)
    {
        final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(mContext).edit();
        editor.putString(MiBancoConstants.CUSTOM_URL_KEY, url);
        editor.apply();

        App.getApplicationInstance().setApiUrl(url);

    }

    OnClickListener customPortOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Set up the input
            final EditText input = new EditText(mContext);
            int maxLength = 4;
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            input.setFilters(FilterArray);

            AlertDialogParameters params = new AlertDialogParameters(mContext,"Enter a port",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            String port = input.getText().toString();
                            if (port != null && port.length() > 0) {
                                tvPortsOptions.setText(port);
                                selectedPort = port;
                            }
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            });

            params.setInputEditText(input);

            params.setPositiveButtonText(getResources().getString(R.string.ok));
            params.setNegativeButtonText(getResources().getString(R.string.cancel).toUpperCase());
            Utils.showAlertDialog(params);

        }
    };

    OnClickListener portsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final Dialog dialog = new AppCompatDialog(mContext, R.style.Dialog);
            dialog.setCancelable(true);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.single_list_view, null);
            dialog.setContentView(convertView);
            final ListView lv = (ListView) convertView.findViewById(R.id.list_view_elements);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, portsList());
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item =  adapter.getItem(position);

                    if(item != null) {
                        tvPortsOptions.setText(item);
                        selectedPort = item;
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        }
    };

    OnClickListener confirmOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            saveNewURL(String.format(urlBase(), selectedPort));

            setResult(Activity.RESULT_OK);
            application.reLogin(mContext);
        }
    };

    class DeveloperArrayAdapter extends ArrayAdapter {
        private final int mResource;

        public DeveloperArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            mResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View myConvertView = convertView;

            if (myConvertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myConvertView = inflater.inflate(mResource, parent, false);
                myConvertView.setEnabled(false);
                myConvertView.setClickable(false);
                myConvertView.setVerticalScrollBarEnabled(true);

            }

            final SwitchCompat swt = (SwitchCompat) myConvertView.findViewById(R.id.switchOption);

            final String item = String.valueOf(getItem(position));
            ((TextView) myConvertView.findViewById(R.id.title_text)).setText(FeatureFlags.getInstance().getDescForFlag(item));

            swt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FeatureFlags.getInstance().saveValueForFlag(item, swt.isChecked());
                }
            });
            swt.setChecked(FeatureFlags.getInstance().getValueForFlag(item));

            FontChanger.changeFonts(myConvertView);
            return myConvertView;
        }
    }
}
