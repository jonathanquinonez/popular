package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.ContactListAdapter;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.object.ContactItem;
import com.popular.android.mibanco.object.ContactItem.ContactType;
import com.popular.android.mibanco.util.BPAnalytics;

/**
 * Contact Us Activity class.
 */
public class Contact extends BaseActivity {

    private ListView contactList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact);

        contactList = (ListView) findViewById(R.id.contact_list);
        final ContactListAdapter contactListAdapter = new ContactListAdapter(this, R.layout.list_item_contact, R.layout.list_item_holo_header);

        final Resources resources = getResources();
        final TypedArray typedArray = resources.obtainTypedArray(R.array.contacts);
        final String[][] contacts = new String[typedArray.length()][];
        for (int i = 0; i < typedArray.length(); i++) {
            final int id = typedArray.getResourceId(i, 0);
            if (id > 0) {
                contacts[i] = resources.getStringArray(id);
            }
        }
        typedArray.recycle();

        for (final String[] contact : contacts) {
            for (int j = 0; j < contact.length; j++) {


                if (j == 0) {
                    Log.d("###***", "###*** header=["+contact[j]+"]");
                    contactListAdapter.addItem(new ContactItem("header", contact[j], ContactType.CONTACT_TYPE_HEADER));
                    continue;
                }

                if (j == 1) {
                    if (contact[j] != null && contact[j].length() > 0) {
                        Log.d("###***", "###*** Email=["+contact[j]+"]");
                        contactListAdapter.addItem(new ContactItem("Email", contact[j], ContactType.CONTACT_TYPE_EMAIL));
                    }
                    continue;
                }

                Log.d("###***", "###*** row=["+contact[j]+"]");
                if (contact[j] != null &&  contact[j].equals("Web")) {
                    contactListAdapter.addItem(new ContactItem("Web", contact[j+1], ContactType.CONTACT_TYPE_WEB));
                    continue;
                }

                if (j + 1 < contact.length) {
                    contactListAdapter.addItem(new ContactItem(contact[j], contact[j + 1], ContactType.CONTACT_TYPE_PHONE));
                    j++;
                }

            }
        }

        contactList.setAdapter(contactListAdapter);
        setListeners();
    }


    @SuppressLint("NewApi")
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_contact).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    /**
     * Sets the listeners.
     */
    private void setListeners() {
        final OnItemClickListener listItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
                final ContactListAdapter adapter = (ContactListAdapter) ((ListView) parent).getAdapter();
                final ContactItem item = (ContactItem) adapter.getItem(position);

                try {
                    if (item != null) {
                        if (item.getType() == ContactType.CONTACT_TYPE_EMAIL) {
                            final String uriText = "mailto:" + item.getContent();
                            final Uri uri = Uri.parse(uriText);
                            final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(uri);
                            startActivity(emailIntent);
                        } else if (item.getType() == ContactType.CONTACT_TYPE_PHONE) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_CONTACTUS_CALL_INITIATED);
                            final Uri uriTel = Uri.parse("tel:" + item.getContent());
                            final Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(uriTel);
                            startActivity(callIntent);
                        } else if (item.getType() == ContactType.CONTACT_TYPE_WEB) {
                                //BPAnalytics.logEvent(BPAnalytics.EVENT_CONTACTUS_CALL_INITIATED);
                                final Uri uriWeb = Uri.parse("http://" + item.getContent());
                                final Intent callIntent = new Intent(Intent.ACTION_VIEW);
                                callIntent.setData(uriWeb);
                                startActivity(callIntent);
                        }
                    }
                } catch (final ActivityNotFoundException ex) {
                    Log.w("Contact", ex);
                    Toast.makeText(Contact.this, getString(R.string.no_application_found), Toast.LENGTH_SHORT).show();
                }
            }
        };
        contactList.setOnItemClickListener(listItemClickListener);
    }
}
