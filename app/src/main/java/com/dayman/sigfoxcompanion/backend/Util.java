package com.dayman.sigfoxcompanion.backend;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.dayman.sigfoxcompanion.R;

/**
 * Created by 25143j on 01/08/2017.
 */

public class Util {

    private static TypedValue typedValue = new TypedValue();

    public static int fetchAccentColor(Context mContext) {

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static int fetchPrimaryColor(Context mContext) {
        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static int fetchPrimaryColorDark(Context mContext) {
        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimaryDark });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static void copyToClipboard(ClipboardManager clipboard, String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}
