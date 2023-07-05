package com.popular.android.mibanco.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.ViewUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Fragment class that manages a general object picker
 */
public class ObjectPickerFragment extends DialogFragment {

    public static final String OBJECT_PICKER_TAG = "FRAGMENT_OBJECT_PICKER";
    private Bundle data;

    public static ObjectPickerFragment newInstance(ArrayList<Object> objectsList1, ArrayList<Object> objectsList2, String setLabel, String cancelLabel, final Bundle data) {
        final ObjectPickerFragment frag = new ObjectPickerFragment();
        final Bundle args = new Bundle();
        args.putSerializable("objectsList1", objectsList1);
        args.putSerializable("objectsList2", objectsList2);
        args.putString("setLabel", setLabel);
        args.putString("cancelLabel", cancelLabel);
        args.putBundle("data", data);
        frag.setArguments(args);
        return frag;
    }

    public static void showObjectPicker(final BaseActivity activity, ArrayList objectsList1, ArrayList objectsList2, String setLabel, String cancelLabel, final Bundle data) {
        final ObjectPickerFragment datePickerFragment = ObjectPickerFragment.newInstance(objectsList1, objectsList2, setLabel, cancelLabel, data);
        try {
            datePickerFragment.show(activity.getSupportFragmentManager(), OBJECT_PICKER_TAG);
        } catch (IllegalStateException e) {
            App.submitException(e);
        }
    }

    @SuppressLint("NewApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final ArrayList<Object> objectsList1 = (ArrayList<Object>) getArguments().getSerializable("objectsList1");
        final ArrayList<Object> objectsList2 = (ArrayList<Object>) getArguments().getSerializable("objectsList2");
        final String setLabel = getArguments().getString("setLabel");
        final String cancelLabel = getArguments().getString("cancelLabel");
        data = getArguments().getBundle("data");

        final Dialog dialog = new AppCompatDialog(getActivity(), R.style.Dialog);
        dialog.setContentView(R.layout.object_picker_dialog);

        final NumberPicker picker1 = (NumberPicker) dialog.findViewById(R.id.picker1);
        picker1.setMinValue(0);
        picker1.setMaxValue(objectsList1.size() - 1);
        String[] displayValues1 = new String[objectsList1.size()];
        for (int i = 0; i < objectsList1.size(); ++i) {
            Object object = objectsList1.get(i);
            displayValues1[i] = android.text.Html.fromHtml(object.toString()).toString();
        }
        picker1.setDisplayedValues(displayValues1);
        picker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final NumberPicker picker2 = (NumberPicker) dialog.findViewById(R.id.picker2);
        picker2.setMinValue(0);
        picker2.setMaxValue(objectsList2.size() - 1);
        String[] displayValues2 = new String[objectsList2.size()];
        for (int i = 0; i < objectsList2.size(); ++i) {
            Object object = objectsList2.get(i);
            displayValues2[i] = android.text.Html.fromHtml(object.toString()).toString();
        }
        picker2.setDisplayedValues(displayValues2);
        picker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        final Button btnSet = (Button) dialog.findViewById(R.id.btnSet);

        btnCancel.setText(StringUtils.upperCase(cancelLabel));
        btnSet.setText(StringUtils.upperCase(setLabel));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ViewUtils.styleNumberPicker(getActivity(), picker1);
            ViewUtils.styleNumberPicker(getActivity(), picker2);
        }

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof OnObjectsSetListener) {
                    ((OnObjectsSetListener) getActivity()).onObjectsSet(objectsList1.get(picker1.getValue()), objectsList2.get(picker2.getValue()), data);
                    dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof OnObjectsSetListener) {
                    dismiss();
                }
            }
        });

        return dialog;
    }

    @Override
    public void dismiss() {
        try {
            super.dismissAllowingStateLoss();
        } catch (final IllegalStateException e) {
            App.submitException(e);
        }
    }

    /**
     * Interface to be implemented to manage what happens when an object is set
     */
    public interface OnObjectsSetListener {

        void onObjectsSet(Object object1, Object object2, Bundle data);
    }
}
