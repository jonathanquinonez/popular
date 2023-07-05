package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseListAdapter;
import com.popular.android.mibanco.model.PhonebookContact;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Adapter for the ATH Movil contacts adapter
 */
public class AthmContactsAdapter extends BaseListAdapter<PhonebookContact> implements StickyListHeadersAdapter, Filterable {

    private int[] contactColors;
    private List<PhonebookContact> originalData;

    public AthmContactsAdapter(Context context, List<PhonebookContact> originalData) {
        super(context);
        contactColors = getContext().getResources().getIntArray(R.array.athm_contact_colors);
        this.originalData = new ArrayList<>(originalData);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_athm_contact, parent, false);
        }

        TextView tvContactGroup = ViewHolder.get(convertView, R.id.tvContactGroup);
        TextView tvContactName = ViewHolder.get(convertView, R.id.tvContactName);
        TextView tvContactPhoneNumber = ViewHolder.get(convertView, R.id.tvContactPhoneNumber);
        final TextView tvContactPhotoSection = ViewHolder.get(convertView, R.id.tvContactPhotoSection);
        final ImageView imgContactPhoto = ViewHolder.get(convertView, R.id.imgContactPhoto);

        final PhonebookContact contactItem = getItem(position);
        tvContactName.setText(contactItem.getName());
        tvContactPhoneNumber.setText(contactItem.getFormattedPhoneNumber(getContext(), false));
        tvContactPhotoSection.setText("");

        String previousSectionCharacter = null;
        if (position > 0) {
            PhonebookContact previousContact = getItem(position - 1);
            if ((previousSectionCharacter = previousContact.getSectionCharacter()) == null) {
                previousSectionCharacter = previousContact.getName().substring(0, 1);
                if (!StringUtils.isAlpha(previousSectionCharacter)) {
                    previousSectionCharacter = "#";
                }
            }
        }

        String currentSectionCharacter;
        if ((currentSectionCharacter = contactItem.getSectionCharacter()) == null) {
            currentSectionCharacter = getItem(position).getName().substring(0, 1);
            if (!StringUtils.isAlpha(currentSectionCharacter)) {
                currentSectionCharacter = "#";
            }
            contactItem.setSectionCharacter(currentSectionCharacter);
        }

        if (previousSectionCharacter != null && previousSectionCharacter.equals(currentSectionCharacter)) {
            tvContactGroup.setText("");
        } else {
            tvContactGroup.setText(currentSectionCharacter);
        }

        final String contactPhotoSection = currentSectionCharacter;
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
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.athm_contacts_list_header, parent, false);
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                final List<PhonebookContact> valuesToShow;
                if (constraint != null && constraint.length() > 0 && originalData != null) {
                    valuesToShow = new ArrayList<>();
                    for (final PhonebookContact contact : originalData) {
                        final String toCompare = contact.getRawPhoneNumber() + " " + contact.getName();
                        if (toCompare.toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                            valuesToShow.add(contact);
                        }
                    }
                } else {
                    valuesToShow = originalData;
                }

                final FilterResults filterResults = new FilterResults();
                if (valuesToShow != null) {
                    filterResults.values = valuesToShow;
                    filterResults.count = valuesToShow.size();
                }

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(final CharSequence constraint, final FilterResults results) {
                getData().clear();
                if (results != null && results.values != null) {
                    getData().addAll((List<PhonebookContact>) results.values);
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(final Object resultValue) {
                return super.convertResultToString(resultValue);
            }
        };
    }
}
