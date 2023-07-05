package com.popular.android.mibanco.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ET55498 on 4/18/16.
 */
public class AlertDialogParameters {

    private String title;
    private int message;
    private String strMessage;
    private Context context;
    private String positiveButtonText;
    private String negativeButtonText;
    private String neutralButtonText;
    private EditText inputEditText;
    private TextView textView;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnCancelListener onCancelListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private String[] items;
    private Button positiveButton;
    private Button negativeButton;
    private Button neutralButton;
    private boolean isHtmlFormat;
    private View customView;

    public AlertDialogParameters(Context context)
    {
        this.context = context;
    }

    public AlertDialogParameters(Context context, DialogInterface.OnClickListener onClickListener)
    {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    public AlertDialogParameters(Context context, int message, DialogInterface.OnClickListener onClickListener)
    {
        this.context = context;
        this.message = message;
        this.onClickListener = onClickListener;
    }

    public AlertDialogParameters(Context context, String message, DialogInterface.OnClickListener onClickListener)
    {
        this.context = context;
        this.strMessage = message;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMessage() {
        return message;
    }

    public Context getContext() {
        return context;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public String getNeutralButtonText() {
        return neutralButtonText;
    }

    public void setNeutralButtonText(String neutralButtonText) {
        this.neutralButtonText = neutralButtonText;
    }

    public DialogInterface.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public DialogInterface.OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public EditText getInputEditText() {
        return this.inputEditText;
    }

    public void setInputEditText(EditText inputEditText) {
        this.inputEditText = inputEditText;
    }

    public String[] getItems() {
        return this.items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public Button getPositiveButton() {
        return this.positiveButton;
    }

    public void setPositiveButton(Button button) {
        this.positiveButton = button;
    }

    public Button getNegativeButton() {
        return this.negativeButton;
    }

    public void setNegativeButton(Button button) {
        this.negativeButton = button;
    }

    public Button getNeutralButton() {
        return this.neutralButton;
    }

    public void setNeutralButton(Button button) {
        this.neutralButton = button;
    }

    public boolean getIsHtmlFormat() {
        return this.isHtmlFormat;
    }

    public void setIsHtmlFormat(boolean isHtmlFormat) {
        this.isHtmlFormat = isHtmlFormat;
    }

    public View getCustomView() {
        return this.customView;
    }

    public void setCustomView(View linearLayout) {
        this.customView = linearLayout;
    }
}
