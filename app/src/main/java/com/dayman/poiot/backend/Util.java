package com.dayman.poiot.backend;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.dayman.poiot.R;

/**
 * Created by 25143j on 01/08/2017.
 */

public class Util {

    public static int fetchAccentColor(Context mContext) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static int fetchPrimaryColor(Context mContext) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static int fetchPrimaryColorDark(Context mContext) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimaryDark });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
