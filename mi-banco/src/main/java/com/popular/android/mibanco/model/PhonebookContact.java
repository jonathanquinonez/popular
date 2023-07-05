package com.popular.android.mibanco.model;

import android.content.Context;

import com.popular.android.mibanco.exception.InvalidPhoneNumberFormatException;
import com.popular.android.mibanco.util.ContactsManagementUtils;

import java.io.Serializable;

/**
 * Class that represents a phonebook contact
 */
public class PhonebookContact implements Serializable {

    @Override
    public boolean equals(final Object o) {
        if ((o==null)||!(o instanceof PhonebookContact)) {
            return false;
        }
        return getRawPhoneNumber().equalsIgnoreCase(((PhonebookContact) o).getRawPhoneNumber());
    }

    private static final long serialVersionUID = 4839816328070118692L;

    private String name;
    private String number;
    private String formattedPhoneNumber;
    private String contactPhotoUri;
    private boolean phoneNumberFormatted;
    private String sectionCharacter;
    private int defaultPhotoColor;
    private boolean isPhoneContact = true;

    public PhonebookContact(final String name, final String phoneNumber) {
        this.name = name;
        number = phoneNumber;
    }

    public PhonebookContact(final String name, final String phoneNumber, final String contactPhotoUri) {
        this.name = name;
        number = phoneNumber;
        this.contactPhotoUri = contactPhotoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFormattedPhoneNumber(final Context context, final boolean forceFormatting) {
        if (!phoneNumberFormatted || forceFormatting) {
            try {
                formattedPhoneNumber = ContactsManagementUtils.formatPhoneNumber(context, number, false);
                phoneNumberFormatted = true;
                return formattedPhoneNumber;
            } catch (final InvalidPhoneNumberFormatException e) {
                return number;
            }
        }

        return formattedPhoneNumber;
    }

    public String getRawPhoneNumber() {
        if (number == null)
            return null;

        String temp = number.replaceAll("[^0-9]", "").trim();
        if (temp.length() == 11) {
            if (temp.charAt(0) == '1') {
                temp = temp.substring(1);
            }
        }
        return temp;
    }

    public static String getRawPhoneNumber(String formattedPhoneNumber) {
        if (formattedPhoneNumber == null)
            return null;

        String temp = formattedPhoneNumber.replaceAll("[^0-9]", "").trim();
        if (temp.length() == 11) {
            if (temp.charAt(0) == '1') {
                temp = temp.substring(1);
            }
        }
        return temp;
    }

    public String getPhoneNumber() {
        return number;
    }

    public void setPhoneNumber(final String phoneNumber) {
        number = phoneNumber;
    }

    public String getContactPhotoUri() {
        return contactPhotoUri;
    }

    public void setContactPhotoUri(final String contactPhotoUri) {
        this.contactPhotoUri = contactPhotoUri;
    }

    public String getSectionCharacter() {
        return sectionCharacter;
    }

    public void setSectionCharacter(String sectionCharacter) {
        this.sectionCharacter = sectionCharacter;
    }

    public int getDefaultPhotoColor() {
        return defaultPhotoColor;
    }

    public void setDefaultPhotoColor(int defaultPhotoColor) {
        this.defaultPhotoColor = defaultPhotoColor;
    }

    public boolean isPhoneContact() {
        return isPhoneContact;
    }

    public void setIsPhoneContact(boolean isPhoneContact) {
        this.isPhoneContact = isPhoneContact;
    }
}
