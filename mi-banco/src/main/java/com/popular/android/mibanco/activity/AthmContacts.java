package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.AthmContactsAdapter;
import com.popular.android.mibanco.adapter.AthmRecentsAdapter;
import com.popular.android.mibanco.base.BasePermissionsSessionActivity;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Activity that manages the ATHM contacts
 */
public class AthmContacts extends BasePermissionsSessionActivity implements TextWatcher, View.OnClickListener  {

    private GridView gridViewRecents;
    private LinearLayout viewListHeader;
    private StickyListHeadersListView listViewContacts;

    private AthmRecentsAdapter gridAdapter;
    private AthmContactsAdapter listAdapter;

    private EditText etSearch;
    private ImageButton selectOtherContact;
    private String recentsType;
    private Cursor cursor;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_contacts);

        recentsType = getIntent().getStringExtra(MiBancoConstants.RECENT_CONTACTS_KEY);
        if(Utils.isBlankOrNull(recentsType)){
            recentsType = MiBancoConstants.RECENT_CONTACTS_KEY_ATHM;
        }

        etSearch = (EditText) findViewById(R.id.etSearch);
        if(etSearch != null) {
            etSearch.addTextChangedListener(this);
        }

        selectOtherContact = (ImageButton)findViewById(R.id.selectContact);
        if(selectOtherContact != null) {
            selectOtherContact.setVisibility(View.INVISIBLE);
            selectOtherContact.setOnClickListener(this);
        }

        listViewContacts = (StickyListHeadersListView) findViewById(R.id.listViewContacts);
        if(listViewContacts != null){
            listViewContacts.setVisibility(View.GONE);
        }
    }


    public void onPermissionResult(boolean permissionGranted)
    {
        if(permissionGranted){
            initializePhoneContacts();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    private void initializePhoneContacts(){

        if(listViewContacts == null) {
            listViewContacts = (StickyListHeadersListView) findViewById(R.id.listViewContacts);
        }

        if(listViewContacts != null && listViewContacts.getVisibility() == View.GONE) {
            listViewContacts.setVisibility(View.VISIBLE);
            final ViewGroup nullParent = null;
            viewListHeader = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.athm_contacts_header, nullParent, false);
            gridViewRecents = (GridView) viewListHeader.findViewById(R.id.gridViewRecents);
            gridViewRecents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (gridAdapter != null) {
                        returnSelectedContact(gridAdapter.getItem(position));
                    }
                }
            });

            listViewContacts.addHeaderView(viewListHeader);
            listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (listAdapter != null) {
                        returnSelectedContact(listAdapter.getItem(position - 1));
                    }
                }
            });

            AthmTasks.getPhoneContacts(this, new AthmTasks.AthmListener<LinkedHashMap<String, PhonebookContact>>() {
                @Override
                public void onAthmApiResponse(LinkedHashMap<String, PhonebookContact> result) {
                    List<String> savedRecentsList = new ArrayList<String>();
                    if(recentsType != null && !Utils.isBlankOrNull(MiBancoPreferences.getLoggedInUsername())){
                        if(recentsType.equalsIgnoreCase(MiBancoConstants.RECENT_CONTACTS_KEY_ATHM)){
                            savedRecentsList = MiBancoPreferences.getAthmRecentContacts(MiBancoPreferences.getLoggedInUsername());
                        }else{
                            savedRecentsList = MiBancoPreferences.getEasyCashRecentContacts(MiBancoPreferences.getLoggedInUsername());
                        }
                    }

                    if (savedRecentsList.size() > 0) {
                        final ArrayList<PhonebookContact> recentExistingContacts = new ArrayList<>();
                        ArrayList<String> recentExistingContactsNumbers = new ArrayList<>();
                        for (String rawPhoneNumber : savedRecentsList) {
                            if (result.containsKey(rawPhoneNumber)) {
                                recentExistingContacts.add(result.get(rawPhoneNumber));
                                recentExistingContactsNumbers.add(rawPhoneNumber);
                            }
                        }

                        if(recentsType != null&& !Utils.isBlankOrNull(MiBancoPreferences.getLoggedInUsername())){
                            if(recentsType.equalsIgnoreCase(MiBancoConstants.RECENT_CONTACTS_KEY_ATHM)){
                                MiBancoPreferences.setAthmRecentContacts(MiBancoPreferences.getLoggedInUsername(), recentExistingContactsNumbers);
                            }else{
                                MiBancoPreferences.setEasyCashRecentContacts(MiBancoPreferences.getLoggedInUsername(), recentExistingContactsNumbers);
                            }
                        }

                        if (recentExistingContacts.size() > 0) {
                            viewListHeader.findViewById(R.id.viewRecents).setVisibility(View.VISIBLE);
                            gridAdapter = new AthmRecentsAdapter(AthmContacts.this);
                            gridViewRecents.setAdapter(gridAdapter);
                            gridAdapter.updateDataset(recentExistingContacts);
                            gridViewRecents.post(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) gridViewRecents.getLayoutParams();
                                    layoutParams.height = recentExistingContacts.size() > 3 ? gridViewRecents.getChildAt(0).getHeight() * 2 : gridViewRecents.getChildAt(0).getHeight();
                                    gridViewRecents.setLayoutParams(layoutParams);
                                }
                            });
                        }
                    }

                    List<PhonebookContact> contactsList = new ArrayList<>(result.values());
                    listAdapter = new AthmContactsAdapter(AthmContacts.this, contactsList);
                    listViewContacts.setAdapter(listAdapter);
                    listAdapter.updateDataset(contactsList);
                }
            });
        }
    }


    private String getContactName(String phoneNumber)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        cursor= getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }
        cursor = null;

        return contactName;
    }

    private void returnSelectedContact(PhonebookContact selectedContact) {
        if (selectedContact != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MiBancoConstants.ATHM_CONTACT_KEY, selectedContact);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (listAdapter != null) {
            listAdapter.getFilter().filter(s);
        }

        if(etSearch.getText().length() == 10 && etSearch.getText().toString().matches(MiBancoConstants.REGEX_ALL_DIGITS)){
            selectOtherContact.setVisibility(View.VISIBLE);
        }else{
            selectOtherContact.setVisibility(View.INVISIBLE);
        }
    }


    //invoke selectOtherContact OnClickListener
    @Override
    public void onClick(View v) {
        String getContactNameSearch = getContactName(etSearch.getText().toString());
        String a = getResources().getString(R.string.athm_contacts_other);
        String contactName = (!Utils.isBlankOrNull(getContactNameSearch)) ? getContactNameSearch : getResources().getString(R.string.athm_contacts_other);
        PhonebookContact otherContact = new PhonebookContact(contactName, etSearch.getText().toString());
        otherContact.setSectionCharacter(String.valueOf(otherContact.getName().charAt(0)));
        otherContact.setDefaultPhotoColor(getResources().getColor(R.color.blue));
        otherContact.setIsPhoneContact(false);
        returnSelectedContact(otherContact);
    }
}