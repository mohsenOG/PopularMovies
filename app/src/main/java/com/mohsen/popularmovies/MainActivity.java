package com.mohsen.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mohsen.popularmovies.common.Utils;
import com.mohsen.popularmovies.model.MovieApi;
import com.mohsen.popularmovies.model.MovieQueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, RecyclerViewAdapter.ItemClickListener
{

    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapter mAdapter;
    RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mLoadingIndicator;
    private List<String> mPosterRelativePath;

    private String mQueryType;
    private Map<String, String> mQueryParams = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorTextView = findViewById(R.id.tv_error_msg_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_posters);

        mPosterRelativePath = new ArrayList<>();

        // Set the preferences to default for each cold start of app.
        //PreferenceManager.setDefaultValues(this, R.xml.pref_popular_movies, true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mQueryType = mSharedPreferences.getString(getString(R.string.pref_key_sorting), getString(R.string.pref_value_popularity));
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Check if there is internet connection.
        if (!Utils.isOnline(this)) {
            showHideErrorMassage(getString(R.string.no_internet), true);
        }

        // Qeury data from MovieDB
        queryData();
        // Initiate the recycler view.
        initRecyclerView();

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
        if (key.equals(getString(R.string.pref_key_sorting)))
        {
            mQueryType = sharedPreferences.getString(key, getString(R.string.pref_value_popularity));
            queryData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    void queryData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        if (mQueryParams == null)
            mQueryParams = new HashMap<>();
        mQueryParams.put(getString(R.string.api_key_title), getString(R.string.moviedb_api_key));
        mQueryParams.put(getString(R.string.api_param_language), getString(R.string.api_param_language_value));
        MovieApi movieApi = MovieApi.retrofit.create(MovieApi.class);
        Call<MovieQueryResult> call = movieApi.getMovies(mQueryType, mQueryParams);
        call.enqueue(new Callback<MovieQueryResult>() {
            @Override
            public void onResponse(@NonNull Call<MovieQueryResult> call, @NonNull Response<MovieQueryResult> response) {
                MovieQueryResult result = response.body();
                if (result == null) return;
                mPosterRelativePath = result.getPosterRelativePaths();
                mAdapter.swapData(mPosterRelativePath);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<MovieQueryResult> call, @NonNull Throwable t) {
                showHideErrorMassage(t.getMessage(), true);
            }
        });
    }

    void initRecyclerView() {
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(this, 250);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(this, mPosterRelativePath);
        mRecyclerView.setAdapter(mAdapter);
    }


}
