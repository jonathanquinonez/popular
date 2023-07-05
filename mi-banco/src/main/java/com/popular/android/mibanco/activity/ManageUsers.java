package com.popular.android.mibanco.activity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.ListUsersAdapter;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the UI for saved users management.
 */
public class ManageUsers extends BaseActivity {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_users);

        List<User> savedUsers = Utils.getUsernames(getApplicationContext());
        ArrayList<String> users = new ArrayList<>(savedUsers.size());

        for (User user : savedUsers) {
            users.add(user.getUsername());
        }

        final ListView list = (ListView) findViewById(R.id.listUsers);
        if (list != null) {
            list.setAdapter(new ListUsersAdapter(this, R.layout.list_item_users, users));
        }

        FontChanger.changeFonts(getWindow().getDecorView().getRootView());
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

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
}
