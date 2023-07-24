package com.popular.android.mibanco.object;

import android.view.View;

public class SettingsItem {

    private Class<?> intentClass;
    private final String title;
    private String subTitle;
    private String url;
    private String subItemTitle;
    private boolean isTitle = false;
    private boolean isFooter = false;
    private boolean isFooterSetting = false;
    private boolean displaySwitch = false;

    public boolean isFooterSetting() {
        return isFooterSetting;
    }

    public void setFooterSetting(boolean footerSetting) {
        isFooterSetting = footerSetting;
    }

    public boolean isShowDivider() {
        return showDivider;
    }

    public void setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
    }

    private boolean isAction = false;
    private boolean isPushSettings = false;
    private boolean isWebView=false;
    private View.OnClickListener alertsOnClickListener;
    private View.OnClickListener webViewOnClickListener;

    boolean showDivider = true;

    int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public SettingsItem(final String title) {
        this.title = title;
    }

    public SettingsItem(final String title, boolean displaySwitch) {
        this.title = title;
        this.displaySwitch = displaySwitch;
    }


    public SettingsItem(int image, final String title, final Class<?> intentClass) {
        this.title = title;
        this.intentClass = intentClass;
        this.image = image;
    }

    public SettingsItem(int image, final String title, final String url) {
        this.title = title;
        this.url = url;
        this.image = image;
    }

    public Class<?> getIntentClass() {
        return intentClass;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isDisplaySwitch() {
        return displaySwitch;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title, boolean showDivider) {
        isTitle = title;
        this.showDivider = showDivider;
    }

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public boolean isAction() {
        return isAction;
    }

    public void setIsAction(boolean action) {
        isAction = action;
    }

    public void setDescription(String description) {
        this.subTitle = description;
    }

    public boolean isDescription() {
        return subTitle != null;
    }

    public String getDescription() {
        return subTitle;
    }

    public boolean isPushSettings() {
        return isPushSettings;
    }

    public void setIsWebView(boolean webView) { isWebView= webView;}
    public boolean isWebViewItem() { return isWebView;}

    public void setPushSettings(boolean pushSettings) {
        isPushSettings = pushSettings;
    }

    public View.OnClickListener getAlertsOnClickListener() {
        return alertsOnClickListener;
    }

    public void setAlertsOnClickListener(View.OnClickListener alertsOnClickListener) {
        this.alertsOnClickListener = alertsOnClickListener;
    }

    public String getSubItemTitle() {
        return subItemTitle;
    }

    public void setSubItemTitle(String subItemTitle) {
        this.subItemTitle = subItemTitle;
    }

    public View.OnClickListener getWebViewOnClickListener() {return webViewOnClickListener; }
    public void setWebViewOnClickListener(View.OnClickListener webViewOnClickListener) { this.webViewOnClickListener = webViewOnClickListener;}
}
