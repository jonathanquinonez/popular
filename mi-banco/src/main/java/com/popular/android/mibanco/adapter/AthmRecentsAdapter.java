package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseListAdapter;
import com.popular.android.mibanco.model.PhonebookContact;

/**
 * Adapter for the ATH Movil recent contacts list
 */
public class AthmRecentsAdapter extends BaseListAdapter<PhonebookContact> {

    private int[] contactColors;

    public AthmRecentsAdapter(Context context) {
        super(context);
        contactColors = getContext().getResources().getIntArray(R.array.athm_contact_colors);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_athm_recent_contact, parent, false);
        }

        TextView tvContactName = ViewHolder.get(convertView, R.id.tvContactName);
        TextView tvContactPhoneNumber = ViewHolder.get(convertView, R.id.tvContactPhoneNumber);
        final TextView tvContactPhotoSection = ViewHolder.get(convertView, R.id.tvContactPhotoSection);
        final ImageView imgContactPhoto = ViewHolder.get(convertView, R.id.imgContactPhoto);

        final PhonebookContact contactItem = getItem(position);
        tvContactName.setText(contactItem.getName());
        tvContactPhoneNumber.setText(contactItem.getFormattedPhoneNumber(getContext(), false));
        tvContactPhotoSection.setText("");

        final String contactPhotoSection = getItem(position).getName().substring(0, 1);
        ImageLoader.getInstance().displayImage(contactItem.getContactPhotoUri(), imgContactPhoto, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                contactItem.setDefaultPhotoColor(contactColors[position % contactColors.length]);
                Drawable contactPhotoDrawable = ContextCompat.getDrawable(getContext(),R.drawable.contact_photo_placeholder);
                contactPhotoDrawable.setColorFilter(contactItem.getDefaultPhotoColor(), PorterDuff.Mode.SRC_ATOP);
                imgContactPhoto.setImageDrawable(contactPhotoDrawable);
                tvContactPhotoSection.setText(contactPhotoSection);
                tvContactPhotoSection.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                tvContactPhotoSection.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });

        return convertView;
    }
}
