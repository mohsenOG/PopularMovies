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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mohsen.popularmovies.common.Utils;
import com.mohsen.popularmovies.model.MovieApi;
import com.mohsen.popularmovies.model.MovieInfo;
import com.mohsen.popularmovies.model.MovieQueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, RecyclerViewAdapter.ItemClickListener
{

    public static final String TITLE_BUNDLE_EXTRA          = "TITLE_BUNDLE_EXTRA";
    public static final String ORIGINAL_TITLE_BUNDLE_EXTRA = "ORIGINAL_TITLE_BUNDLE_EXTRA";
    public static final String POSTER_PATH_BUNDLE_EXTRA    = "POSTER_PATH_BUNDLE_EXTRA";
    public static final String OVERVIEW_BUNDLE_EXTRA       = "OVERVIEW_BUNDLE_EXTRA";
    public static final String VOTE_AVERAGE_BUNDLE_EXTRA   = "VOTE_AVERAGE_BUNDLE_EXTRA";
    public static final String RELEASE_DATE_BUNDLE_EXTRA   = "RELEASE_DATE_BUNDLE_EXTRA";

    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mLoadingIndicator;
    private MovieQueryResult mResult = null;
    private List<String> mPosterRelativePath;
    private String mQueryType;
    private Map<String, String> mQueryParams = null;
    private Button mRetryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorTextView = findViewById(R.id.tv_error_msg_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_posters);
        mRetryButton = findViewById(R.id.btn_search_again);


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
        Bundle bundle = new Bundle();
        //title
        String title = info.getTitle();
        if (title != null)
            bundle.putString(TITLE_BUNDLE_EXTRA, title);
        // Original title
        String originalTitle = info.getOriginalTitle();
        if (originalTitle != null)
            bundle.putString(ORIGINAL_TITLE_BUNDLE_EXTRA, originalTitle);
        // Movie Poster rel Path
        bundle.putString(POSTER_PATH_BUNDLE_EXTRA, posterRelPath);
        // Overview
        String overview = info.getOverview();
        if (overview != null)
            bundle.putString(OVERVIEW_BUNDLE_EXTRA, overview);
        // Vote Average
        String voteAverage = info.getVoteAverage();
        if (voteAverage != null)
            bundle.putString(VOTE_AVERAGE_BUNDLE_EXTRA, voteAverage);
        // Release date
        String releaseDate = info.getReleaseDate();
        if (releaseDate != null)
            bundle.putString(RELEASE_DATE_BUNDLE_EXTRA, releaseDate);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtras(bundle);
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
        if (mQueryParams == null)
            mQueryParams = new HashMap<>();
        mQueryParams.clear();
        mQueryParams.put(getString(R.string.api_key_title), BuildConfig.API_KEY);
        mQueryParams.put(getString(R.string.api_param_language), getString(R.string.api_param_language_value));
        MovieApi movieApi = MovieApi.retrofit.create(MovieApi.class);
        Call<MovieQueryResult> call = movieApi.getMovies(mQueryType, mQueryParams);
        call.enqueue(new Callback<MovieQueryResult>() {
            @Override
            public void onResponse(@NonNull Call<MovieQueryResult> call, @NonNull Response<MovieQueryResult> response) {
                mResult = response.body();
                if (mResult == null) return;
                mPosterRelativePath = mResult.getPosterRelativePaths();
                mAdapter.swapData(mPosterRelativePath);
                showHideErrorMassage(null, false);
            }

            @Override
            public void onFailure(@NonNull Call<MovieQueryResult> call, @NonNull Throwable t) {
                showHideErrorMassage(t.getMessage(), true);
            }
        });
    }

    private void initRecyclerView() {
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(this, 360);
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
