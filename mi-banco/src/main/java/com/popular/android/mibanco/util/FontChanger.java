package com.popular.android.mibanco.util;

import android.app.Application;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Provides static methods for changing type face in the application.
 */
public final class FontChanger {

    private static Application application;
    private static String applicationFont = "fonts/Roboto-Regular.ttf";
    private static Typeface tf;

    private static void changeFontOnView(final View v) {
        if (v instanceof TextView) {
            if (((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().getStyle() != Typeface.NORMAL) {
                ((TextView) v).setTypeface(tf, ((TextView) v).getTypeface().getStyle());
            } else {
                ((TextView) v).setTypeface(tf);
            }
        } else if (v instanceof Button) {
            ((Button) v).setTypeface(tf);
        } else if (v instanceof EditText) {
            ((EditText) v).setTypeface(tf);
        } else if (v instanceof ViewGroup) {
            changeFonts(v);
        }
    }

    public static void changeFonts(final View view) {
        if (tf == null) {
            tf = Typeface.createFromAsset(application.getAssets(), applicationFont);
        }

        if (view != null) {
            if (view instanceof ViewGroup) {
                final ViewGroup root = (ViewGroup) view;
                for (int i = 0; i < root.getChildCount(); i++) {
                    final View v = root.getChildAt(i);
                    changeFontOnView(v);
                }
            } else {
                changeFontOnView(view);
            }
        }
    }

    public static String getApplicationFont() {
        return applicationFont;
    }

    public static Typeface getApplicationTypeface() {
        return Typeface.createFromAsset(application.getAssets(), applicationFont);
    }

    public static void setApplication(final Application app) {
        application = app;
    }

    public static void setApplicationFont(final String applicationFont) {
        FontChanger.applicationFont = applicationFont;
    }

    private FontChanger() {
    }
}
