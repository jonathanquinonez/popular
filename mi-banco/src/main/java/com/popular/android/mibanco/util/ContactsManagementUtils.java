package com.popular.android.mibanco.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.exception.InvalidPhoneNumberFormatException;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.view.AlertDialogFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by et55498
 * Date: 6/14/2017.
 */

public class ContactsManagementUtils {

    public static void setContactToView(final Context mContext, final PhonebookContact contactTo, TextView recipientHint, LinearLayout selectContact)
    {
        if (contactTo != null) {
            if(recipientHint != null) {
                recipientHint.setVisibility(View.GONE);
            }
            selectContact.removeAllViews();

            View recipientView = LayoutInflater.from(mContext).inflate(R.layout.athm_view_recipient, selectContact, false);
            TextView tvContactName = (TextView) recipientView.findViewById(R.id.tvContactName);
            TextView tvContactPhoneNumber = (TextView) recipientView.findViewById(R.id.tvContactPhoneNumber);
            final TextView tvContactPhotoSection = (TextView) recipientView.findViewById(R.id.tvContactPhotoSection);
            final ImageView imgContactPhoto = (ImageView) recipientView.findViewById(R.id.imgContactPhoto);

            tvContactName.setText(contactTo.getName());
            tvContactPhoneNumber.setText(contactTo.getFormattedPhoneNumber(mContext, false));
            tvContactPhotoSection.setText("");

            final String contactPhotoSection = contactTo.getSectionCharacter();

            if(contactTo!= null && !contactTo.isPhoneContact()){
                Drawable contactPhotoDrawable = ContextCompat.getDrawable(mContext, R.drawable.contact_photo_placeholder);
                contactPhotoDrawable.setColorFilter(contactTo.getDefaultPhotoColor(), PorterDuff.Mode.SRC_ATOP);
                imgContactPhoto.setImageDrawable(contactPhotoDrawable);
                tvContactPhotoSection.setText(contactPhotoSection);
            }else {
                ImageLoader.getInstance().displayImage(contactTo.getContactPhotoUri(), imgContactPhoto, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (contactTo != null) {
                            Drawable contactPhotoDrawable = ContextCompat.getDrawable(mContext,R.drawable.contact_photo_placeholder);
                            contactPhotoDrawable.setColorFilter(contactTo.getDefaultPhotoColor(), PorterDuff.Mode.SRC_ATOP);
                            imgContactPhoto.setImageDrawable(contactPhotoDrawable);
                            tvContactPhotoSection.setText(contactPhotoSection);
                        }

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            }
            selectContact.addView(recipientView);
            selectContact.setVisibility(View.VISIBLE);
        } else {
            selectContact.setVisibility(View.GONE);
            recipientHint.setVisibility(View.VISIBLE);
        }
    }



    public static LinkedHashMap<String, PhonebookContact> getContactsWithPhones(final Context context) {
        final LinkedHashMap<String, PhonebookContact> contacts = new LinkedHashMap<>();
        final LinkedHashMap<String, PhonebookContact> contactsSpecialChars = new LinkedHashMap<>();
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Data.CONTACT_ID};
        final String selection = ContactsContract.CommonDataKinds.Phone.NUMBER.concat(" IS NOT NULL AND ").concat(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME).concat(" IS NOT NULL");
        final String sortOrder = Utils.concatenateStrings(new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME," ASC"});
        Cursor people = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, sortOrder);

        if(people != null) {
            final int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            final int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int indexContactId = people.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            final HashSet<Pair<String, String>> contactsSet = new HashSet<>();

            while (people.moveToNext()) {
                final String number = people.getString(indexNumber);
                final String rawNumber = getRawPhoneNumber(people.getString(indexNumber), context);
                final String name = people.getString(indexName);
                final long contactId = people.getLong(indexContactId);
                final PhonebookContact contact = new PhonebookContact(name, number);
                final Uri personUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                final Uri photoUri = Uri.withAppendedPath(personUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                contact.setContactPhotoUri(photoUri.toString());

                final Pair<String, String> pair = new Pair<>(name, rawNumber);
                if (!contactsSet.contains(pair)) {
                    contactsSet.add(pair);
                    if (StringUtils.isAlpha(contact.getName().substring(0, 1))) {
                        contacts.put(rawNumber, contact);
                    } else {
                        contactsSpecialChars.put(rawNumber, contact);
                    }
                }
            }
            people.close();
            contacts.putAll(contactsSpecialChars);

        }
        return contacts;
    }

    public static String getRawPhoneNumber(final String phoneNumber, final Context context) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return context.getString(R.string.not_available);
        }

        String edPhoneNumber = phoneNumber.replace("-","").replace("(","").replace(")","").replaceAll(" ","").trim();
        return edPhoneNumber.replaceAll("[^0-9]", "");
    }

    public static String formatPhoneNumber(final Context context, final String unformattedPhoneNumber, final boolean showDialogWarning) throws InvalidPhoneNumberFormatException {
        if (TextUtils.isEmpty(unformattedPhoneNumber)) {
            return context.getString(R.string.not_available);
        }
        String temp = unformattedPhoneNumber.replaceAll("[^0-9]", "").trim();
        if (temp.length() < 10 || temp.length() > 11) {
            if (showDialogWarning) {
                AlertDialogFragment.showAlertDialog((BaseActivity) context, context.getString(R.string.app_name), context.getString(R.string.athm_invalid_phone_number), context.getString(R.string.ok),
                        null, MiBancoConstants.MiBancoDialogId.INVALID_PHONE_NUMBER, null, false);
            }
            throw new InvalidPhoneNumberFormatException(unformattedPhoneNumber);
        } else {
            if (temp.length() == 11) {
                if (temp.charAt(0) == '1') {
                    temp = temp.substring(1);
                } else {
                    if (showDialogWarning) {
                        AlertDialogFragment.showAlertDialog((BaseActivity) context, context.getString(R.string.app_name), context.getString(R.string.athm_invalid_phone_number),
                                context.getString(R.string.ok), null, MiBancoConstants.MiBancoDialogId.INVALID_PHONE_NUMBER, null, false);
                    }
                    throw new InvalidPhoneNumberFormatException(unformattedPhoneNumber);
                }
            }
            temp = String.format("(%s) %s-%s", temp.substring(0, 3), temp.substring(3, 6), temp.substring(6, 10));
            return temp;
        }
    }


    public static String getContactName(Context mContext, String phone, HashMap<String, PhonebookContact> contacts)
    {
        //List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(mContext, Arrays.asList(MiBancoConstants.CONTACTS_PERMISSIONS));
//        if(missingPermissions.size() == 0){
        try {
            String contactName = formatPhoneNumber(mContext, phone, false);
            String phoneNumber = phone;
            String[] phoneNumberCombinations = {"1","1 ","+1","+1 "};
            if(contacts != null){
                if(!contacts.containsKey(phoneNumber)){
                    for(String prefix: phoneNumberCombinations){
                        phoneNumber = prefix+phone;
                        if(contacts.containsKey(phoneNumber)){
                            PhonebookContact contact = contacts.get(phoneNumber);
                            contactName = contact.getName() +" "+contactName;
                            break;
                        }
                    }
                }else{
                    PhonebookContact contact = contacts.get(phone);
                    contactName = contact.getName() +" "+contactName;
                }
            }
            return contactName;
        }catch (Exception e){
            return phone;
        }

    }
}
