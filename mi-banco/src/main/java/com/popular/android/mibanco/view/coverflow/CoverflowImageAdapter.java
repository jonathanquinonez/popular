package com.popular.android.mibanco.view.coverflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;

import java.util.ArrayList;

public class CoverflowImageAdapter extends BaseAdapter {

    private final static int CARD_BACK_BASE_ID = 2000;

    private final static int CARD_FRONT_BASE_ID = 1000;

    private final ArrayList<CustomerAccount> accounts;

    private int scaledHeight;

    private int scaledWidth;

    private ManagedBitmapBuffer bitmapBuffer;

    private Context context;

    /**
     * Creates the adapter with default set of resource images.
     * 
     * @param context context
     * @param customerAccounts the customer's accounts
     * @param scaledWidth scaled width of the cover flow
     * @param scaledHeight scaled height of the cover flow
     * @param position the position of the selected element
     */
    public CoverflowImageAdapter(final Context context, final ArrayList<CustomerAccount> customerAccounts, int scaledWidth, int scaledHeight, int position) {
        accounts = customerAccounts;
        this.context = context;
        bitmapBuffer = new ManagedBitmapBuffer(context, customerAccounts, scaledWidth, scaledHeight, position);
    }

    public ManagedBitmapBuffer getBitmapBuffer() {
        return bitmapBuffer;
    }

    @Override
    public synchronized int getCount() {
        return accounts.size();
    }

    public CustomerAccount getCustomerAccount(final int position) {
        return accounts.get(position);
    }

    public ArrayList<CustomerAccount> getData() {
        return accounts;
    }

    public float getHeight() {
        return scaledHeight;
    }

    public void recycle() {
        bitmapBuffer.close();
    }

    public void refreshCacheAt(int position) {
        bitmapBuffer.refreshCacheAt(position);
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        bitmapBuffer.setCurrentPosition(position);
    }

    public Bitmap getItemBitmap(int position) {
        return bitmapBuffer.getBitmap(position, false);
    }

    @Override
    public long getItemId(final int id) {
        return id;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        ViewFlipper viewFlipper;
        ViewHolder holder;

        if (convertView == null) {
            viewFlipper = new ViewFlipper(context);
            viewFlipper.setLayoutParams(new CoverFlow.LayoutParams(scaledWidth, scaledHeight));

            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();
            final View frontCardView = inflater.inflate(R.layout.coverflow_image, viewFlipper, false);

            holder.frontImage = (ImageView) frontCardView.findViewById(R.id.coverflow_front_image);
            holder.frontImage.setImageBitmap(bitmapBuffer.getBitmap(position, false));

            final View backCardView = inflater.inflate(R.layout.coverflow_image_back, viewFlipper, false);

            // add views to flipper
            viewFlipper.addView(frontCardView);
            viewFlipper.addView(backCardView);
            viewFlipper.getChildAt(0).setId(CARD_FRONT_BASE_ID + position);
            viewFlipper.getChildAt(1).setId(CARD_BACK_BASE_ID + position);

            viewFlipper.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            viewFlipper = (ViewFlipper) convertView;

            holder.frontImage.setImageBitmap(bitmapBuffer.getBitmap(position, false));
        }

        return viewFlipper;
    }

    static class ViewHolder {
        ImageView frontImage;
        TextView accountName;
        TextView accountNumber;
    }

    public float getWidth() {
        return scaledWidth;
    }

    public synchronized void setHeight(final int height) {
        scaledHeight = height;
    }

    public void setScaledHeight(final int height) {
        scaledHeight = height;
    }

    public void setScaledWidth(final int width) {
        scaledWidth = width;
    }

    public synchronized void setWidth(final int width) {
        scaledWidth = width;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}
