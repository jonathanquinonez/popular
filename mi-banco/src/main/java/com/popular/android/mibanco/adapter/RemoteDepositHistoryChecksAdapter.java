package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foound.widget.AmazingAdapter;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.object.RemoteDepositHistoryCheckItem;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.List;

/**
 * Adapter class to manage data in remote deposit history list
 */
public class RemoteDepositHistoryChecksAdapter extends AmazingAdapter {

	private List<Pair<String, List<RemoteDepositHistoryCheckItem>>> items;
    private int itemsSize = 0;

	private List<CustomerAccount> rdcAccounts;
	
    private final LayoutInflater inflater;

    private final Context context;

    public RemoteDepositHistoryChecksAdapter(final Context context, final List<Pair<String, List<RemoteDepositHistoryCheckItem>>> data, final List<CustomerAccount> accounts) {
        this.items = data;
        this.itemsSize = items.size();
        this.context = context;
        this.rdcAccounts = accounts;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        int res = 0;
        for (int i = 0; i < itemsSize; i++) {
            res += items.get(i).second.size();
        }
        return res;
    }

    @Override
    public RemoteDepositHistoryCheckItem getItem(int position) {
        int c = 0;
        for (int i = 0; i < itemsSize; i++) {
            if (position >= c && position < c + items.get(i).second.size()) {
                return items.get(i).second.get(position - c);
            }
            c += items.get(i).second.size();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onNextPageRequested(int page) {
    }

    @Override
    protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        View headerBox = view.findViewById(R.id.header_box);
        TextView sectionTitle = (TextView) headerBox.findViewById(R.id.header_text);
        if (displaySectionHeader) {
            headerBox.setVisibility(View.VISIBLE);
            sectionTitle.setText(getSections()[getSectionForPosition(position)]);
        } else {
            headerBox.setVisibility(View.GONE);
        }
        FontChanger.changeFonts(view);
    }

    static class ViewHolderCheckReceipt {

        public TextView to;
        public TextView toNumber;
        public TextView amount;
        public TextView date;
        public ImageView logo;
    }

    @Override
    public View getAmazingView(int position, View convertView, ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
        	myConvertView = inflater.inflate(R.layout.list_item_receipt, null);
        	ViewHolderCheckReceipt viewHolder = new ViewHolderCheckReceipt();
        	viewHolder.to = (TextView) myConvertView.findViewById(R.id.txt_payee_to);
        	viewHolder.toNumber = (TextView) myConvertView.findViewById(R.id.txt_to_payee_number);
        	viewHolder.amount = (TextView) myConvertView.findViewById(R.id.txt_amount);
        	viewHolder.date = (TextView) myConvertView.findViewById(R.id.txt_date);
        	viewHolder.logo = (ImageView) myConvertView.findViewById(R.id.img_receipt_card);
        	myConvertView.setTag(viewHolder);
        }

        ViewHolderCheckReceipt holder = (ViewHolderCheckReceipt) myConvertView.getTag();
        final RemoteDepositHistoryCheckItem item = getItem(position);

        if (item != null) {
        	holder.to.setText(item.getTargetNickname());
        	holder.toNumber.setText(item.getTargetAccountLast4Num());
        	holder.amount.setText(item.getAmount());
        	holder.date.setText(item.getSubmittedDate());
                
        	for(CustomerAccount rdcAccount : rdcAccounts) {
        		if(rdcAccount.getFrontEndId().equals(item.getFrontendId())) {
        			CustomerAccount customerAccount = App.getApplicationInstance().getCustomerAccountsMap().get(rdcAccount.getApiAccountKey() + rdcAccount.getAccountNumberSuffix());
            		final String path = Utils.getAccountImagePath(customerAccount, this.context);
        			if (path == null) {
        				holder.logo.setImageResource(customerAccount.getImgResource());
        			} else {
        				Bitmap accountImage = BitmapFactory.decodeFile(path);
        				if (accountImage != null) {
        					holder.logo.setImageBitmap(accountImage);
        				} else {
        					holder.logo.setImageResource(customerAccount.getImgResource());
        				}
        			}
        			break;
                }
            }
        }

        return myConvertView;
    }

    @Override
    public void configurePinnedHeader(View headerLayout, int position, int alpha) {
        int sectionIndex = getSectionForPosition(position);
        if (sectionIndex < 0 || sectionIndex >= getSections().length) {
            return;
        }
        TextView sectionHeader = (TextView) headerLayout.findViewById(R.id.header_text);
        sectionHeader.setText(getSections()[sectionIndex]);
        sectionHeader.setBackgroundColor(alpha << 24 | ContextCompat.getColor(context, R.color.white));
        sectionHeader.setTextColor(alpha << 24 | ContextCompat.getColor(context, R.color.account_details_header));
        FontChanger.changeFonts(sectionHeader);
    }

    @Override
    public int getPositionForSection(int section) {
        if (section < 0) {
            section = 0;
        }
        if (section >= itemsSize) {
            section = itemsSize - 1;
        }
        int c = 0;
        for (int i = 0; i < itemsSize; i++) {
            if (section == i) {
                return c;
            }
            c += items.get(i).second.size();
        }

        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        int c = 0;
        for (int i = 0; i < itemsSize; i++) {
            if (position >= c && position < c + items.get(i).second.size()) {
                return i;
            }
            c += items.get(i).second.size();
        }
        return -1;
    }

    @Override
    public String[] getSections() {
        String[] res = new String[itemsSize];
        for (int i = 0; i < itemsSize; i++) {
            res[i] = items.get(i).first;
        }
        return res;
    }
}
