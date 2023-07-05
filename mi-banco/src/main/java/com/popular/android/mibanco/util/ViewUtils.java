package com.popular.android.mibanco.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;

import java.lang.reflect.Field;

/**
 * Utilities class for View common methods
 */
public final class ViewUtils {


    @TargetApi(11)
    public static void styleDatePicker(Context context, DatePicker datePicker) {
        try {
            LinearLayout firstViewGroup = (LinearLayout) datePicker.getChildAt(0);
            LinearLayout secondViewGroup = (LinearLayout) firstViewGroup.getChildAt(0);
            for (int i = 0; i < secondViewGroup.getChildCount(); i++) {
                NumberPicker numberPicker = (NumberPicker) secondViewGroup.getChildAt(i);
                Field[] numberPickerFields = NumberPicker.class.getDeclaredFields();
                for (Field field : numberPickerFields) {
                    if (field.getName().equals("mSelectionDivider")) {
                        field.setAccessible(true);
                        field.set(numberPicker, ContextCompat.getDrawable(context, R.drawable.divider_datepicker));
                    } else if (field.getName().equals("mInputText")) {
                        field.setAccessible(true);
                        EditText editText = (EditText) field.get(numberPicker);
                        editText.setTextColor(ContextCompat.getColor(context, R.color.black_54));
                    } else if (field.getName().equals("mSelectorWheelPaint")) {
                        field.setAccessible(true);
                        Paint paint = (Paint) field.get(numberPicker);
                        paint.setColor(ContextCompat.getColor(context, R.color.black_54));
                    }
                }
            }
        } catch (Exception e) {
            App.submitException(e);
        }
    }

    @TargetApi(11)
    public static void styleNumberPicker(Context context, NumberPicker numberPicker) {
        try {
            Field[] numberPickerFields = NumberPicker.class.getDeclaredFields();
            for (Field field : numberPickerFields) {
                switch (field.getName()) {
                    case "mSelectionDivider":
                        field.setAccessible(true);
                        field.set(numberPicker, ContextCompat.getDrawable(context, R.drawable.divider_datepicker));
                        break;
                    case "mInputText":
                        field.setAccessible(true);
                        EditText editText = (EditText) field.get(numberPicker);
                        editText.setTextColor(ContextCompat.getColor(context, R.color.black_54));
                        break;
                    case "mSelectorWheelPaint":
                        field.setAccessible(true);
                        Paint paint = (Paint) field.get(numberPicker);
                        paint.setColor(ContextCompat.getColor(context, R.color.black_54));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            App.submitException(e);
        }
    }
}
