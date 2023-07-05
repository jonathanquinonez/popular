package com.popular.android.mibanco.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.FontChanger;

public class DialogCoverup extends Dialog {

    private final Context context;

    public DialogCoverup(final Context context) {
        super(context, R.style.DialogCoverup);
        this.context = context;
        View customView = null;
        customView = View.inflate(context, R.layout.dialog_coverup, null);
        setContentView(customView);
        setCancelable(false);

        FontChanger.changeFonts(findViewById(R.id.parentPanel));
    }

    public void setProgressCaption(final int resourceId) {
        setProgressCaption(context.getString(resourceId));
    }

    public void setProgressCaption(final String caption) {
        ((TextView) findViewById(R.id.progressCaption)).setText(caption);
    }
}
