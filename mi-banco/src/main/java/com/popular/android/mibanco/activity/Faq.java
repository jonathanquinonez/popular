package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * FAQ screen Activity class.
 */
public class Faq extends BaseActivity {

    private String[] faqItemContents;
    private String[] faqItemTitles;


    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.faq);

        faqItemTitles = new String[2];
        faqItemContents = new String[2];

        faqItemTitles[0] = getResources().getString(R.string.faq1_title);
        faqItemTitles[1] = getResources().getString(R.string.faq2_title);

        faqItemContents[0] = Utils.loadRawTextResource(R.raw.faqa1);
        faqItemContents[1] = Utils.loadRawTextResource(R.raw.faqa2);

        /*
         * groupData describes the first-level entries, Layout for the first-level entries, Key in the groupData maps to display, Data under
         * "faqItemTitle" key goes into this TextView, childData describes second-level entries, Layout for second-level entries, Keys in childData
         * maps to display, Data under the keys above go into these TextViews
         */
        SimpleExpandableListAdapter listAdapter = new SimpleExpandableListAdapter(this, createGroupList(), R.layout.title_row, new String[]{"faq_item_title"}, new int[]{R.id.faq_content}, createChildList(),
                R.layout.child_row, new String[]{"faq_item_content"}, new int[]{R.id.faq_content});
        ExpandableListView listFaq = (ExpandableListView) findViewById(R.id.listFaq);
        listFaq.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FontChanger.changeFonts(getWindow().getDecorView().getRootView());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        Utils.setupLanguage(this);

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Creates the child list out of the faqItemContents[] array according to the structure required by SimpleExpandableListAdapter.
     *
     * @return The resulting List containing one list for each group. Each such second-level group contains Maps. Each such Map contains one key
     * "faq_item_content" and value of an entry in the faqItemContents[] array.
     */
    private List<ArrayList<HashMap<String, String>>> createChildList() {
        final ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        for (int i = 0; i < faqItemContents.length; ++i) {
            // Second-level lists
            final ArrayList<HashMap<String, String>> secList = new ArrayList<HashMap<String, String>>();
            final HashMap<String, String> child = new HashMap<String, String>();
            child.put("faq_item_content", faqItemContents[i]);
            secList.add(child);
            result.add(secList);
        }
        return result;
    }

    /**
     * Creates the group list out of the faqItemTitles[] array according to the structure required by SimpleExpandableListAdapter.
     *
     * @return The resulting List containing Maps. Each Map contains one entry with key "faq_item_title" and value of an entry in the faqItemTitles[]
     * array.
     */
    private List<HashMap<String, String>> createGroupList() {
        final ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < faqItemTitles.length; ++i) {
            final HashMap<String, String> m = new HashMap<String, String>();
            m.put("faq_item_title", faqItemTitles[i]);
            result.add(m);
        }
        return result;
    }
}
