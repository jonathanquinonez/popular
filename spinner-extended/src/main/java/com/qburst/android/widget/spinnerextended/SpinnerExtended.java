package com.qburst.android.widget.spinnerextended;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import androidx.core.content.ContextCompat;

import com.devspark.robototextview.RobotoTypefaces;
import com.devspark.robototextview.widget.RobotoTextView;
import com.qburst.android.widget.spinnerextended.internal.SpinnerAlwaysCallSelect;

import java.util.concurrent.atomic.AtomicInteger;

public class SpinnerExtended extends LinearLayout {

    public static final int NOTHING_SELECTED_POSITION = -1;
    public static final int NO_RESOURCE_MATCH = 0;
    private static final AtomicInteger nextGeneratedId = new AtomicInteger(1);
    private static final int selectedItemLayoutResourceId = R.layout.spinner_extended_selected_item;
    private SpinnerAlwaysCallSelect spinner;
    private RobotoTextView textView;
    private OnItemSelectedListener onItemSelectedListener;
    private final OnItemSelectedListener spinnerOnItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
            textView.setText(spinner.getItemAtPosition(position).toString());
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(parent, SpinnerExtended.this, position, id);
            }
        }

        @Override
        public void onNothingSelected(final AdapterView<?> parent) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onNothingSelected(parent);
            }
        }
    };
    private OnClickListener onClickListener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpinnerExtended(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SpinnerExtended(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SpinnerExtended(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpinnerExtended(Context context) {
        super(context);
    }

    public void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        removeAllViews();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerExtended);
        String hint = typedArray.getString(R.styleable.SpinnerExtended_spinnerHint);
        int hintColor = typedArray.getColor(R.styleable.SpinnerExtended_spinnerHintColor, ContextCompat.getColor(context, R.color.black_54));
        int selectedItemColor = typedArray.getColor(R.styleable.SpinnerExtended_spinnerSelectedItemColor, ContextCompat.getColor(context, R.color.black_87));
        int selectedItemTextStyle = typedArray.getResourceId(R.styleable.SpinnerExtended_spinnerSelectedItemTextStyle, NO_RESOURCE_MATCH);
        int selectedItemLayout = typedArray.getResourceId(R.styleable.SpinnerExtended_spinnerSelectedItemLayout, NO_RESOURCE_MATCH);
        boolean spinnerDialogMode = typedArray.getBoolean(R.styleable.SpinnerExtended_spinnerDialogMode, false);
        typedArray.recycle();

        final View rootView = LayoutInflater.from(context).inflate(R.layout.spinner_extended, this, false);
        if (spinnerDialogMode) {
            spinner = (SpinnerAlwaysCallSelect) rootView.findViewById(R.id.spinnerDialog);
        } else {
            spinner = (SpinnerAlwaysCallSelect) rootView.findViewById(R.id.spinner);
        }
        spinner.setVisibility(View.INVISIBLE);
        LinearLayout textViewLayout = (LinearLayout) rootView.findViewById(R.id.textViewLayout);
        final View selectedSpinnerView;
        if (selectedItemLayout != NO_RESOURCE_MATCH) {
            selectedSpinnerView = LayoutInflater.from(context).inflate(selectedItemLayout, this, false);
        } else {
            selectedSpinnerView = LayoutInflater.from(context).inflate(selectedItemLayoutResourceId, this, false);
        }
        textView = (RobotoTextView) selectedSpinnerView.findViewById(android.R.id.text1);
        textViewLayout.addView(selectedSpinnerView);

        spinner.setId(generateIdForView());
        textViewLayout.setId(generateIdForView());
        textView.setId(generateIdForView());
        selectedSpinnerView.setId(generateIdForView());

        if (selectedItemTextStyle != NO_RESOURCE_MATCH) {
            textView.setTextAppearance(context, selectedItemTextStyle);
            int[] robotoAttrs = {R.attr.robotoTypeface};
            final TypedArray robotoTypedArray = context.obtainStyledAttributes(selectedItemTextStyle, robotoAttrs);
            int typefaceValue = robotoTypedArray.getInt(0, RobotoTypefaces.TYPEFACE_ROBOTO_REGULAR);
            robotoTypedArray.recycle();
            final Typeface robotoTypeface = RobotoTypefaces.obtainTypeface(context, typefaceValue);
            RobotoTypefaces.setUpTypeface(textView, robotoTypeface);
        }

        textView.setTextColor(selectedItemColor);
        textView.setText(null);
        textView.setHintTextColor(hintColor);
        textView.setHint(hint);

        textViewLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(SpinnerExtended.this);
                } else {
                    spinner.performClick();
                }
            }
        });

        addView(rootView);
    }

    public void setOnItemSelectedListener(final OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    public Object getSelectedItem() {
        return TextUtils.isEmpty(textView.getText()) ? null : spinner.getSelectedItem();
    }

    public int getSelectedItemPosition() {
        return TextUtils.isEmpty(textView.getText()) ? NOTHING_SELECTED_POSITION : spinner.getSelectedItemPosition();
    }

    public SpinnerAdapter getAdapter() {
        return spinner.getAdapter();
    }

    public void setAdapter(final SpinnerAdapter adapter) {
        spinner.setOnItemSelectedListener(null);
        spinner.setAdapter(adapter);
        textView.setText(null);
        spinner.setSelection(0, false); // prevents onItemSelectedListener from calling
        spinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);
    }

    public void setSelection(final int position) {
        if (position == NOTHING_SELECTED_POSITION) {
            textView.setText(null);
        } else {
            spinner.setSelection(position);
            textView.setText(spinner.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void setOnClickListener(final OnClickListener listener) {
        onClickListener = listener;
    }

    private int generateIdForView() {
        for (; ; ) {
            final int result = nextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (nextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}

