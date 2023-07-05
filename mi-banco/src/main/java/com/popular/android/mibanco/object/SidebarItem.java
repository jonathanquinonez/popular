package com.popular.android.mibanco.object;

import android.content.Intent;

/**
 * Class that represents a sidebar item
 * Created by ET55498 on 9/30/15.
 */
public class SidebarItem {
	
	private int mIcon;
    private String mTitle;
    private Intent mIntent;

    public SidebarItem(String title, int icon, Intent intent) {
        mTitle = title;
		mIcon = icon;
		mIntent = intent;
    }

	/**
	 * @return the mIcon
	 */
	public int getIcon() {
		return mIcon;
	}

	/**
	 * @param mIcon the mIcon to set
	 */
	public void setIcon(int mIcon) {
		this.mIcon = mIcon;
	}

	/**
	 * @return the mTitle
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param mTitle the mTitle to set
	 */
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	/**
	 * @return the mIntent
	 */
	public Intent getIntent() {
		return mIntent;
	}

	/**
	 * @param mIntent the mIntent to set
	 */
	public void setIntent(Intent mIntent) {
		this.mIntent = mIntent;
	}
}