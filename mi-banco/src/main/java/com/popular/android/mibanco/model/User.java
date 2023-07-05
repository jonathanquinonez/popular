package com.popular.android.mibanco.model;

import java.io.Serializable;

/**
 * Class that represents a User
 * Created by ET55498 on 9/15/16.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 4633158007569930156L;
    private String username;
    private String encryptedPassword;
    private String fingerprintBindDate;
    private boolean isSavedUsername = false;

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSavedUsername() {
        return isSavedUsername;
    }

    public void setSavedUsername(boolean savedUsername) {
        isSavedUsername = savedUsername;
    }

    public String getFingerprintBindDate() {
        return fingerprintBindDate;
    }

    public void setFingerprintBindDate(String fingerprintBindDate) {
        this.fingerprintBindDate = fingerprintBindDate;
    }


}
