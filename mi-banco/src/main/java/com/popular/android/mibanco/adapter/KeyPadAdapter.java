package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.FontChanger;

/**
 * Adapter class to manage data in numeric keypad
 */
public class KeyPadAdapter extends BaseAdapter {

    private final String[] numbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "00", "0", "x" };
    private final LayoutInflater inflater;

    public KeyPadAdapter(final Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return numbers.length;
    }

    @Override
    public Object getItem(final int position) {
        return position;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        int buttonPosition = 11;
        int buttonDoubleZeroPosition = 9;
        String[] letters = { "", "ABC", "DEF", "GHI", "JKL", "MNO", "PQRS", "TUV", "WXYZ", "", "", "" };


        View myConvertView = inflater.inflate(R.layout.amount_keypad_key, null);

        if (position == buttonDoubleZeroPosition) {
            myConvertView.findViewById(R.id.normal_key).setVisibility(View.GONE);
            myConvertView.findViewById(R.id.double_zero).setVisibility(View.VISIBLE);
        } else if (position == buttonPosition) {
            myConvertView.findViewById(R.id.normal_key).setVisibility(View.GONE);
            myConvertView.findViewById(R.id.del_key).setVisibility(View.VISIBLE);
        } else {
            ((TextView) myConvertView.findViewById(R.id.key_number)).setText(numbers[position]);
            ((TextView) myConvertView.findViewById(R.id.key_letters)).setText(letters[position]);
        }

        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }
}
