package com.mohsen.popularmovies.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mohsen on 20.02.2018.
 *
 */

public class Utils {

    private Utils() {}
    /**
     *
     * @return true if online otherwise false.
     * @see https://stackoverflow.com/a/4009133/6072457
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     *
     * @param time time with yy-mm-dd format
     * @return time in dd mm yy format.
     * @see https://stackoverflow.com/a/17324150/6072457
     */
    public static String timeConverter(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        Date date;
        try {
            date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String timeConverterToOriginal(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date;
        try {
            date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
