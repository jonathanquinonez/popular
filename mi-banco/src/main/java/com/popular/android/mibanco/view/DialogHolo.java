package com.popular.android.mibanco.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.FontChanger;

public class DialogHolo extends Dialog {

    private final Button confirmButton;

    private final LinearLayout confirmButtonLayout;

    private final Context context;

    private final FrameLayout customContent;

    private android.view.View.OnClickListener listenerConfirm;

    private android.view.View.OnClickListener listenerNegative;

    private android.view.View.OnClickListener listenerPositive;

    private final TextView message;

    private final Button negativeButton;

    private final Button positiveButton;

    private final LinearLayout positiveNegativeButtonsLayout;

    private final TextView title;

    public DialogHolo(final Context context) {
        super(context, R.style.DialogHoloLight);
        this.context = context;
        final View customView = View.inflate(context, R.layout.dialog_holo_light_match, null);
        setContentView(customView);
        title = (TextView) customView.findViewById(R.id.alertTitle);
        message = (TextView) findViewById(R.id.message);
        customContent = (FrameLayout) findViewById(R.id.content);
        positiveNegativeButtonsLayout = (LinearLayout) findViewById(R.id.positiveNegativeButtons);
        confirmButtonLayout = (LinearLayout) findViewById(R.id.confirmationButton);
        positiveButton = (Button) findViewById(R.id.buttonPositive);
        negativeButton = (Button) findViewById(R.id.buttonNegative);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);

        positiveNegativeButtonsLayout.setVisibility(View.GONE);
        confirmButtonLayout.setVisibility(View.GONE);
        setListeners();

        FontChanger.changeFonts(findViewById(R.id.parentPanel));
    }

    public DialogHolo(final Context context, final boolean wrapContent) {
        super(context, R.style.DialogHoloLight);
        this.context = context;
        View customView;
        if (wrapContent) {
            customView = View.inflate(context, R.layout.dialog_holo_light_wrap, null);
        } else {
            customView = View.inflate(context, R.layout.dialog_holo_light_match, null);
        }
        setContentView(customView);
        title = (TextView) customView.findViewById(R.id.alertTitle);
        message = (TextView) findViewById(R.id.message);
        customContent = (FrameLayout) findViewById(R.id.content);
        positiveNegativeButtonsLayout = (LinearLayout) findViewById(R.id.positiveNegativeButtons);
        confirmButtonLayout = (LinearLayout) findViewById(R.id.confirmationButton);
        positiveButton = (Button) findViewById(R.id.buttonPositive);
        negativeButton = (Button) findViewById(R.id.buttonNegative);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);

        positiveNegativeButtonsLayout.setVisibility(View.GONE);
        confirmButtonLayout.setVisibility(View.GONE);
        setListeners();

        FontChanger.changeFonts(findViewById(R.id.parentPanel));
    }

    public void setConfirmationButton(final String buttonLabel, final View.OnClickListener clickListener) {
        confirmButton.setText(buttonLabel);
        listenerConfirm = clickListener;
        setConfirmationButtonMode();
    }

    private void setConfirmationButtonMode() {
        positiveNegativeButtonsLayout.setVisibility(View.GONE);
        confirmButtonLayout.setVisibility(View.VISIBLE);
    }

    private void setCustomContentMode() {
        message.setVisibility(View.GONE);
        customContent.setVisibility(View.VISIBLE);
    }

    public View setCustomContentView(final int customViewLayoutId) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customView = inflater.inflate(customViewLayoutId, null);
        ((FrameLayout) customContent.findViewById(R.id.content)).addView(customView);
        FontChanger.changeFonts(customView);
        setCustomContentMode();

        return customView;
    }

    private void setListeners() {
        positiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listenerPositive != null) {
                    listenerPositive.onClick(positiveButton);
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listenerNegative != null) {
                    listenerNegative.onClick(negativeButton);
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listenerConfirm != null) {
                    listenerConfirm.onClick(confirmButton);
                }
            }
        });
    }

    public void setMessage(final int messageResId) {
        message.setText(messageResId);
        setMessageMode();
    }

    public void setMessage(final String aMessage) {
        message.setText(aMessage);
        setMessageMode();
    }

    private void setMessageMode() {
        message.setVisibility(View.VISIBLE);
        customContent.setVisibility(View.GONE);
    }

    public void setNoContentMode() {
        message.setVisibility(View.GONE);
        customContent.setVisibility(View.GONE);
    }

    public void setNegativeButton(final String buttonLabel, final View.OnClickListener clickListener) {
        negativeButton.setText(buttonLabel);
        listenerNegative = clickListener;
        setPositiveNegativeButtonsMode();
    }

    public void setNoButtonsMode() {
        positiveNegativeButtonsLayout.setVisibility(View.GONE);
        confirmButtonLayout.setVisibility(View.GONE);
    }

    public void setNoTitleMode() {
        findViewById(R.id.topPanel).setVisibility(View.GONE);
    }

    public void setPositiveButton(final String buttonLabel, final View.OnClickListener clickListener) {
        positiveButton.setText(buttonLabel);
        listenerPositive = clickListener;
        setPositiveNegativeButtonsMode();
    }

    private void setPositiveNegativeButtonsMode() {
        positiveNegativeButtonsLayout.setVisibility(View.VISIBLE);
        confirmButtonLayout.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(final CharSequence text) {
        if (text == null) {
            setNoTitleMode();
        } else {
            title.setText(text.toString().toUpperCase());
        }
    }

    @Override
    public void setTitle(final int textResId) {
        title.setText(context.getString(textResId).toUpperCase());
    }

    public void setTitleEnabled(final boolean enable) {
        if (enable) {
            findViewById(R.id.topPanel).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.topPanel).setVisibility(View.GONE);
        }
    }

    public void setMessageCenter(){
        message.setGravity(Gravity.CENTER);

    }
}
