package com.popular.android.mibanco.activity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;
import com.popular.android.mibanco.widget.BalanceWidgetProvider;

import java.util.ArrayList;

/**
 * Language change Activity class.
 */
public class LanguageSettings extends BaseActivity {

    /** The current position in the spinner. */
    private int currentSel;

    /** The available locales (abbreviation). */
    private final String[] myLocales = { MiBancoConstants.ENGLISH_LANGUAGE_CODE, MiBancoConstants.SPANISH_LANGUAGE_CODE };

    /** The spinner control for selecting language. */
    private Spinner spinner;

    /** The button for saving selected language. */
    private Button buttonSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.language_settings);

        spinner = (Spinner) findViewById(R.id.spinLanguages);
        final ArrayList<String> values = new ArrayList<String>(myLocales.length);

        int[] myLocaleLabels = { R.string.english, R.string.espanol };
        for (int i = 0; i < myLocales.length; i++) {
            values.add(getString(myLocaleLabels[i]));
        }

        final String[] spinnerValues = values.toArray(new String[values.size()]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, spinnerValues);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                currentSel = position;
                setButtonSaveState();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0) {

            }
        });

        buttonSave = (Button) findViewById(R.id.btnSaveSettings);
        setButtonSaveState();
        buttonSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                final DialogHolo dialog = new DialogHolo(LanguageSettings.this);
                dialog.setCancelable(true);
                dialog.setMessage(getString(R.string.ask_restart));
                dialog.setNoTitleMode();
                dialog.setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Utils.dismissDialog(dialog);
                        Utils.saveLanguage(application, myLocales[currentSel]);
                        application.setCurrentLanguage(application.getLanguage());
                        application.reLogin(LanguageSettings.this);
                        
                        // Send the update intent to the widget
                        Intent intent = new Intent(LanguageSettings.this, BalanceWidgetProvider.class);
                     	intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                     	int[] ids = {1};
                     	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                     	LanguageSettings.this.sendBroadcast(intent);
                    }
                });
                dialog.setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        spinner.setSelection(application.getLanguage().equals(MiBancoConstants.ENGLISH_LANGUAGE_CODE) ? 0 : 1);
                        Utils.dismissDialog(dialog);
                    }
                });

                Utils.showDialog(dialog, LanguageSettings.this);
            }
        });

        spinner.setSelection(application.getLanguage().equals(MiBancoConstants.ENGLISH_LANGUAGE_CODE) ? 0 : 1);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }

    /** Sets button Save enabled state depending on current and selected language. */
    private void setButtonSaveState() {
        if (application.getLanguage().equals(myLocales[currentSel])) {
            buttonSave.setEnabled(false);
        } else {
            buttonSave.setEnabled(true);
        }
    }
}
