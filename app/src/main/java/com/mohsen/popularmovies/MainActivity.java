package com.mohsen.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, RecyclerViewAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<String>
{

    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapter mAdapter;
    RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mLoadingIndicator;

    private final static int SEARCH_LOADER_ID = 77;
    private final static String SEARCH_QUERY_URL_EXTRA = "SEARCH_QUERY_URL_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorTextView = findViewById(R.id.tv_error_msg_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // Set the preferences to default for each cold start of app.
        PreferenceManager.setDefaultValues(this, R.xml.pref_popular_movies, true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if there is internet connection.
        if (!Utils.isOnline(this)) {
            showHideErrorMassage(getString(R.string.no_internet), true);
        }

        // Query data from MovieDB
        String urlString = Utils.getQueryURL();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, urlString);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(SEARCH_LOADER_ID);
        if (loader == null) loaderManager.initLoader(SEARCH_LOADER_ID, queryBundle, this);
        else loaderManager.restartLoader(SEARCH_LOADER_ID, queryBundle, this);

        mRecyclerView = findViewById(R.id.rv_posters);
        int columnsNo = Utils.numberOfRecyclerViewColumns(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, columnsNo));
        mAdapter = new RecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //TODO if changed re arrange the movies.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        //TODO Goto the detail activity.
    }

    // Show true, hide false
    void showHideErrorMassage(String msg, boolean show) {
        if (show) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(msg);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.INVISIBLE);
        }
    }

    //TODO fill the loader
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
