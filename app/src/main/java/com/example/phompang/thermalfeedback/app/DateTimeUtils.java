package com.example.phompang.thermalfeedback.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phompang on 1/26/2017 AD.
 */

public class DateTimeUtils {
    public static String toDateString(long date) {
        return new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date(date));
    }
}
