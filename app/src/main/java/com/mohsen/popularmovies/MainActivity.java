package com.mohsen.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mohsen.popularmovies.common.Utils;
import com.mohsen.popularmovies.database.DatabaseWrapper;
import com.mohsen.popularmovies.database.MovieDbHelper;
import com.mohsen.popularmovies.model.MovieApi;
import com.mohsen.popularmovies.model.MovieInfo;
import com.mohsen.popularmovies.model.MovieInfoQueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, RecyclerViewAdapter.ItemClickListener
{

    public static final String MOVIE_INFO_EXTRA = "MOVIE_INFO_EXTRA";

    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapter mAdapter;
    @BindView(R.id.rv_posters) RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_msg_display) TextView mErrorTextView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.btn_search_again) Button mRetryButton;
    private MovieInfoQueryResult mResult = null;
    private List<String> mPosterRelativePath;
    private String mQueryType;
    private DatabaseWrapper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // init database.
        mDb = new DatabaseWrapper(this, false);

        mPosterRelativePath = new ArrayList<>();
        // Set the preferences to default for each cold start of app.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mQueryType = mSharedPreferences.getString(getString(R.string.pref_key_sorting), getString(R.string.pref_value_popularity));
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Check if there is internet connection.
        if (!Utils.isOnline(this))
            showHideErrorMassage(getString(R.string.no_internet), true);
        // Query data from MovieDB
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
        } else if (key.equals(getString(R.string.pref_value_favorites))) {
            mQueryType = sharedPreferences.getString(key, getString(R.string.pref_value_favorites));
            queryData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onItemClick(String posterRelPath) {
        //Find the MovieInfo object based on poster relative path.
        MovieInfo info = mResult.getMovieInfo(posterRelPath);
        if (info == null) return;
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MOVIE_INFO_EXTRA, info);
        startActivity(intent);
    }

    // Show true, hide false
    private void showHideErrorMassage(String msg, boolean show) {
        if (show) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRetryButton.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(msg);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRetryButton.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void queryData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);
        if (mQueryType.equals(getString(R.string.pref_value_favorites))) {
            queryDataFromDb();
        } else {
            queryDataFromWeb();
        }
    }

    private void queryDataFromWeb() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(getString(R.string.api_key_title), BuildConfig.API_KEY);
        queryParams.put(getString(R.string.api_param_language), getString(R.string.api_param_language_value));
        MovieApi movieApi = MovieApi.retrofit.create(MovieApi.class);
        Call<MovieInfoQueryResult> call = movieApi.getMovies(mQueryType, queryParams);
        call.enqueue(new Callback<MovieInfoQueryResult>() {
            @Override
            public void onResponse(@NonNull Call<MovieInfoQueryResult> call, @NonNull Response<MovieInfoQueryResult> response) {
                mResult = response.body();
                if (mResult == null) return;
                mPosterRelativePath = mResult.getPosterRelativePaths();
                mAdapter.swapData(mPosterRelativePath);
                showHideErrorMassage(null, false);
            }

            @Override
            public void onFailure(@NonNull Call<MovieInfoQueryResult> call, @NonNull Throwable t) {
                showHideErrorMassage(t.getMessage(), true);
            }
        });
    }

    private void queryDataFromDb() {
        //TODO query data from DB
        // (1) update the result from the db to mResult
        // (2) update the mPosterRelativePath
        // (3) update the recyclerView adapter
        // (4) show hide error message.
    }

    private void initRecyclerView() {
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(this, 400);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(this, mPosterRelativePath);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onRetryButtonClicked(View view) {
        if (view.getId() == R.id.btn_search_again) {
            queryData();
        }
    }
}
