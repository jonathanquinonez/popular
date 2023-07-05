package com.popular.android.mibanco.activity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.R;
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


import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReportFragment.class, R.class, MiBancoConstants.class, App.class, Utils.class,
        FeatureFlags.class, ContextCompat.class, AppCompatActivity.class, BuildConfig.class, BPAnalytics.class,
        Intent.class, Activity.class, Contacts.class})
public class AthmContactsUT {

    @InjectMocks
    private AthmContacts athmContacts;

    @Mock
    private Context mockContext = mock(Context.class);

    @Mock
    private View.OnClickListener viewClickListener;

    @Mock
    private Bundle instanceBundle;

    @Mock
    private App app;

    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private Intent intent;

    @Mock
    private EditText editText;

    @Mock
    private ImageButton imageButton;

    @Mock
    private StickyListHeadersListView stickyListHeadersListView;

    @Mock
    private ContentResolver contentResolver;

    @Mock
    private Cursor cursor;

    @Mock
    private View view;

    @Mock
    private Editable editable;


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(Intent.class);
        PowerMockito.mockStatic(Activity.class);
        PowerMockito.mockStatic(Contacts.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.mockStatic(BPAnalytics.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        athmContacts = spy(new AthmContacts());
    }


    @Test
    public void whenOnCreate_giveBundleInstanceAndRecentContactKey_thenInitializarView() {

        doNothing().when(athmContacts).setContentView(R.layout.athm_contacts);
        PowerMockito.doReturn(appDelegate).when(athmContacts).getDelegate();
        when(App.getApplicationInstance()).thenReturn(app);
        when(athmContacts.getIntent()).thenReturn(intent);
        when(intent.getStringExtra(MiBancoConstants.RECENT_CONTACTS_KEY)).thenReturn(MiBancoConstants.RECENT_CONTACTS_KEY);
        when(athmContacts.findViewById(R.id.etSearch)).thenReturn(editText);
        when(athmContacts.findViewById(R.id.selectContact)).thenReturn(imageButton);
        when(athmContacts.findViewById(R.id.listViewContacts)).thenReturn(stickyListHeadersListView);

        athmContacts.onCreate(instanceBundle);
    }

    @Ignore
    @Test
    public void whenOnCreate_giveBundleInstanceAndRecentContactKeyNull_thenInitializarView() {

        doNothing().when(athmContacts).setContentView(R.layout.athm_contacts);
        PowerMockito.doReturn(appDelegate).when(athmContacts).getDelegate();
        when(App.getApplicationInstance()).thenReturn(app);
        when(athmContacts.getIntent()).thenReturn(intent);
        when(athmContacts.findViewById(R.id.etSearch)).thenReturn(editText);
        when(athmContacts.findViewById(R.id.selectContact)).thenReturn(imageButton);
        when(athmContacts.findViewById(R.id.listViewContacts)).thenReturn(stickyListHeadersListView);
        when(Utils.isBlankOrNull(Matchers.<String>any())).thenReturn(true);

        athmContacts.onCreate(instanceBundle);
    }

    @Test
    public void whenGetContactName_givenNumberPhone_thenReturnContactName() throws Exception {
        // Set up the mock objects
        String[] projection = new String[] { "display_name" };
        String phoneNumber = "555-1234";
        Uri uri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(phoneNumber));
        PowerMockito.doReturn(contentResolver).when(athmContacts).getContentResolver();
        when(contentResolver.query(uri, projection, null, null, null)).thenReturn(cursor);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getString(0)).thenReturn("John Doe");

        // Call the method
        String contactName = Whitebox.invokeMethod(athmContacts, "getContactName", phoneNumber);

        // Verify the results
        verify(contentResolver).query(uri, projection, null, null, null);
        verify(cursor).getString(0);
        assertEquals("John Doe", contactName);
    }




    @Test
    public void whenOnClick_givenViewAndNumberPhone_thenSelectOtherNumber() throws Exception {

        // Set up the mock objects
        String[] projection = new String[] { "display_name" };
        String phoneNumber = "555-1234";
        Uri uri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(phoneNumber));
        PowerMockito.doReturn(contentResolver).when(athmContacts).getContentResolver();
        when(contentResolver.query(uri, projection, null, null, null)).thenReturn(cursor);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getString(0)).thenReturn("John Doe");
        Whitebox.setInternalState(athmContacts, "etSearch", editText);
        when(athmContacts.getResources()).thenReturn(resources);


        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn(phoneNumber);
        String contactName = Whitebox.invokeMethod(athmContacts, "getContactName", phoneNumber);

        athmContacts.onClick(view);
    }

    @Test
    public void whenOnClick_givenViewAndNumberPhoneBlank_thenSelectOtherNumber() throws Exception {

        // Set up the mock objects
        String[] projection = new String[] { "display_name" };
        String phoneNumber = "555-1234";
        Uri uri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(phoneNumber));
        PowerMockito.doReturn(contentResolver).when(athmContacts).getContentResolver();
        when(contentResolver.query(uri, projection, null, null, null)).thenReturn(cursor);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getString(0)).thenReturn("");
        Whitebox.setInternalState(athmContacts, "etSearch", editText);
        when(athmContacts.getResources()).thenReturn(resources);
        when(resources.getString(R.string.athm_contacts_other)).thenReturn("Other phone");
        when(Utils.isBlankOrNull(Matchers.<String>any())).thenReturn(true);

        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn(phoneNumber);
        String contactName = Whitebox.invokeMethod(athmContacts, "getContactName", phoneNumber);

        athmContacts.onClick(view);
    }


}
