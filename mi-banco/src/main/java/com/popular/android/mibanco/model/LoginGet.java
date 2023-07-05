package com.popular.android.mibanco.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents the login response
 */
public class LoginGet extends BaseFormResponse {

    private LoginGetContent content;

    public String getBackgroundImgUrl() {
        return content.backgroundImageUrl;
    }

    public String getUsername() {
        try {
            return (String) form.fields.get("username").value;
        } catch (final Exception ex) {
            Log.w("LoginGet", ex);
            return null;
        }
    }
}

/**
 * Class that represents the login response content
 */
class LoginGetContent {

    protected String backgroundImageUrl;
}

/**
 * Class that represents flags content
 */
class LoginGetFlags {

    @SerializedName("login.username.label")
    protected boolean loginUsername;
}

/**
 * Class that represents labels content
 */
class LoginGetLabels {

    @SerializedName("login.button.login")
    protected String loginButton;

    @SerializedName("login.username.label")
    protected String loginUsername;
}
