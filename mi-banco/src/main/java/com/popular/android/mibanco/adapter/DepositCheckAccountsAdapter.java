package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

/**
 * Adapter class to manage data for the remote deposit accounts
 */
public class DepositCheckAccountsAdapter extends BaseAdapter {

	private final ArrayList<CustomerAccount> accounts;

	private CustomerAccount selectedCustomerAccount;

	private final LayoutInflater inflater;

	private Context context;

	public DepositCheckAccountsAdapter(final Context context, final ArrayList<CustomerAccount> accounts, CustomerAccount selectedAccount) {
		this.accounts = accounts;
		this.selectedCustomerAccount = selectedAccount;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return accounts.size();
	}

	@Override
	public CustomerAccount getItem(final int position) {
		return accounts.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View myConvertView = convertView;
		if (myConvertView == null) {
			myConvertView = inflater.inflate(R.layout.list_item_payee_account, null);
		}

		final CustomerAccount customerAccountEntry = accounts.get(position);

		if (customerAccountEntry != null) {
			((TextView) myConvertView.findViewById(R.id.txt_name)).setText(customerAccountEntry.getNickname());
			TextView txtCardNumber = (TextView) myConvertView.findViewById(R.id.txt_number);
			txtCardNumber.setText(customerAccountEntry.getAccountLast4Num());
			txtCardNumber.setVisibility(View.VISIBLE);

			RadioButton radioAccount = (RadioButton) myConvertView.findViewById(R.id.radio_button);

			final ImageView cardImageView = (ImageView) myConvertView.findViewById(R.id.img_card);
			final String path = Utils.getAccountImagePath(customerAccountEntry, this.context);
	        if (path == null) {
	            cardImageView.setImageResource(customerAccountEntry.getImgResource());
	        } else {
	            Bitmap accountImage = BitmapFactory.decodeFile(path);
	            if (accountImage != null) {
	                cardImageView.setImageBitmap(accountImage);
	            } else {
	                cardImageView.setImageResource(customerAccountEntry.getImgResource());
	            }
	        }
	        
			CustomerAccount selectedAccount = null;
			if (selectedCustomerAccount != null) {
				selectedAccount = selectedCustomerAccount;
			}

			if (selectedAccount != null && customerAccountEntry.getApiAccountKey().equalsIgnoreCase(selectedAccount.getApiAccountKey())
					&& customerAccountEntry.getAccountNumberSuffix().equalsIgnoreCase(selectedAccount.getAccountNumberSuffix())
					&& customerAccountEntry.getAccountSection().equalsIgnoreCase(selectedAccount.getAccountSection())) {
				radioAccount.setChecked(true);
			} else {
				radioAccount.setChecked(false);
			}

			if (position == accounts.size() - 1) {
				myConvertView.findViewById(R.id.bottom_line).setVisibility(View.GONE);
			} else {
				myConvertView.findViewById(R.id.bottom_line).setVisibility(View.VISIBLE);
			}

			FontChanger.changeFonts(myConvertView);
		}

		return myConvertView;
	}
}
