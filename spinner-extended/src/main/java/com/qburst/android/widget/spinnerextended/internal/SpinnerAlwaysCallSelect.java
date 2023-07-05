package com.qburst.android.widget.spinnerextended.internal;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSpinner;

/**
 * Calls onItemSelected even if the same option was selected again.
 */
public class SpinnerAlwaysCallSelect extends AppCompatSpinner {

    public SpinnerAlwaysCallSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected && getOnItemSelectedListener() != null) {
            getOnItemSelectedListener().onItemSelected(null, null, position, 0);
        }
    }
}
