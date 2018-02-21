package com.mohsen.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

/**
 * Created by Mohsen on 20.02.2018.
 *
 */

public class Utils {

    private Utils() {}

    /**
     *
     * @param context
     * @return number of needed columns for the recycler view based on device display.
     * @see https://stackoverflow.com/a/38472370/6072457
     */
    public static int numberOfRecyclerViewColumns(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpWidth = metrics.widthPixels / metrics.density;
        return (int)(dpWidth / 180);
    }

    /**
     *
     * @return true if online otherwise false.
     * @see https://stackoverflow.com/a/4009133/6072457
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static String getQueryURL() {
        //TODO make the URL here.
        return null;
    }

}
