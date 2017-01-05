package com.example.phompang.thermalfeedback.app;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by phompang on 12/23/2016 AD.
 */

public class DpiUtils {
    public static int toPixels(int dp, DisplayMetrics metrics) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
