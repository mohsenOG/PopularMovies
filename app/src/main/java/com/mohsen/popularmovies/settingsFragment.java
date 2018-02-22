package com.mohsen.popularmovies;

/*
 *
 * Created by Mohsen on 19.02.2018.
 */

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


public class settingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_popular_movies);
    }
}
