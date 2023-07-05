package com.popular.android.mibanco.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;

import java.util.Locale;

/**
 * Class to manage Alert Dialog fragment
 */
public class AlertDialogFragment extends DialogFragment {

    public static final String ALERT_DIALOG_TAG_PREFIX = "ALERT_DIALOG_FRAGMENT_";
    protected int dialogId;
    protected Bundle data;

    public static AlertDialogFragment newInstance(final String title, final CharSequence message, final String positiveButtonTitle, final String negativeButtonTitle, final int dialogId,
                                                  final Bundle data, final boolean cancelable) {
        final AlertDialogFragment frag = new AlertDialogFragment();
        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putCharSequence("message", message);
        args.putString("positiveButtonTitle", positiveButtonTitle);
        args.putString("negativeButtonTitle", negativeButtonTitle);
        args.putInt("dialogId", dialogId);
        args.putBundle("data", data);
        args.putBoolean("cancelable", cancelable);
        frag.setArguments(args);
        return frag;
    }

    public static void showAlertDialog(final BaseActivity activity, final String title, final CharSequence message, final String positiveButtonTitle, final String negativeButtonTitle,
                                       final int dialogId, final Bundle data, final boolean cancelable, final boolean showOneInstanceAtTime) {
        if (showOneInstanceAtTime && activity.getSupportFragmentManager().findFragmentByTag(ALERT_DIALOG_TAG_PREFIX + dialogId) != null) {
            return;
        }
        final DialogFragment newFragment = AlertDialogFragment.newInstance(title, message, positiveButtonTitle, negativeButtonTitle, dialogId, data, cancelable);
        newFragment.show(activity.getSupportFragmentManager(), ALERT_DIALOG_TAG_PREFIX + dialogId);
    }

    public static void showAlertDialog(final BaseActivity activity, final String title, final CharSequence message, final String positiveButtonTitle, final String negativeButtonTitle,
                                       final int dialogId, final Bundle data, final boolean cancelable) {
        final DialogFragment newFragment = AlertDialogFragment.newInstance(title, message, positiveButtonTitle, negativeButtonTitle, dialogId, data, cancelable);
        newFragment.show(activity.getSupportFragmentManager(), ALERT_DIALOG_TAG_PREFIX + dialogId);
    }

    public static void showAlertDialog(final BaseActivity activity, final Integer titleResourceId, final int messageResourceId, final Integer positiveButtonTitleResourceId,
                                       final Integer negativeButtonTitleResourceId, final int dialogId, final Bundle data, final boolean cancelable) {
        final DialogFragment newFragment = AlertDialogFragment.newInstance(
                titleResourceId == null ? null : activity.getString(titleResourceId),
                activity.getString(messageResourceId),
                positiveButtonTitleResourceId == null ? null : activity.getString(positiveButtonTitleResourceId),
                negativeButtonTitleResourceId == null ? null : activity.getString(negativeButtonTitleResourceId),
                dialogId,
                data,
                cancelable);
        newFragment.show(activity.getSupportFragmentManager(), ALERT_DIALOG_TAG_PREFIX + dialogId);
    }

    @Override
    public void show(final FragmentManager manager, final String tag) {
        try {
            super.show(manager, tag);
        } catch (final IllegalStateException e) {
            App.submitException(e);
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismissAllowingStateLoss();
        } catch (final IllegalStateException e) {
            App.submitException(e);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final CharSequence message = getArguments().getCharSequence("message");
        final String positiveButtonTitle = getArguments().getString("positiveButtonTitle");
        final String negativeButtonTitle = getArguments().getString("negativeButtonTitle");
        dialogId = getArguments().getInt("dialogId");
        data = getArguments().getBundle("data");
        final boolean cancelable = getArguments().getBoolean("cancelable");

        final Dialog dialog = new AppCompatDialog(getActivity(), R.style.Dialog);
        dialog.setContentView(R.layout.alert_dialog);
        final TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        if (title != null) {
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        if (message != null) {
            tvMessage.setText(message);
        } else {
            tvMessage.setVisibility(View.GONE);
        }
        if (positiveButtonTitle != null) {
            btnOk.setText(positiveButtonTitle.toUpperCase(Locale.getDefault()));
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null && getActivity() instanceof AlertDialogListener) {
                        ((AlertDialogListener) getActivity()).onPositiveClick(AlertDialogFragment.this, dialogId, data);
                    } else {
                        dismiss();
                    }
                }
            });
        } else {
            btnOk.setVisibility(View.GONE);
        }
        if (negativeButtonTitle != null) {
            btnCancel.setText(negativeButtonTitle.toUpperCase(Locale.getDefault()));
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null && getActivity() instanceof AlertDialogListener) {
                        ((AlertDialogListener) getActivity()).onNegativeClick(AlertDialogFragment.this, dialogId, data);
                    }
                    dismiss();
                }
            });
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        if (!cancelable) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(final DialogInterface dialog, final int keyCode, final KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }

        return dialog;
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        super.onCancel(dialog);
        if (getActivity() != null && getActivity() instanceof AlertDialogListener) {
            ((AlertDialogListener) getActivity()).onDialogCancel(AlertDialogFragment.this, dialogId, data);
        }
    }

    /**
     * Interface to be implemented to manage responses in the Alert Dialog Fragment
     */
    public interface AlertDialogListener {

        void onPositiveClick(DialogFragment dialog, int dialogId, Bundle dataBundle);

        void onNegativeClick(DialogFragment dialog, int dialogId, Bundle dataBundle);

        void onDialogCancel(DialogFragment dialog, int dialogId, Bundle dataBundle);
    }
}
