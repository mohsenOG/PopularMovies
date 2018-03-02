package com.mohsen.popularmovies;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import com.mohsen.popularmovies.database.MovieDetailsContract;
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

import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_MOVIE_ID;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_POSTER_PATH;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, RecyclerViewAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
{

    // Used for querying data
    private static final int ID_MOVIE_PATH_LOADER = 10;
    private static final int ID_MOVIE_INFO_LOADER = 20;
    // Used in intents extras
    public static final String MOVIE_INFO_EXTRA = "MOVIE_INFO_EXTRA";
    public static final String MOVIE_FAV_EXTRA = "MOVIE_FAV_EXTRA";
    public static final String BUNDLE_MOVIE_INFO = "BUNDLE_MOVIE_INFO";

    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapter mAdapter;
    @BindView(R.id.rv_posters) RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_msg_display) TextView mErrorTextView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.btn_search_again) Button mRetryButton;
    private MovieInfoQueryResult mResult = null;
    private List<String> mPosterRelativePath;
    private List<String> mPosterRelativePathFavorite;
    private String mQueryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPosterRelativePath = new ArrayList<>();
        mPosterRelativePathFavorite = new ArrayList<>();
        // Set the preferences to default for each cold start of app.
        PreferenceManager.setDefaultValues(this, R.xml.pref_popular_movies, true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mQueryType = mSharedPreferences.getString(getString(R.string.pref_key_sorting), getString(R.string.pref_value_popularity));
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        getLoaderManager().initLoader(ID_MOVIE_PATH_LOADER, null, this);
        //getLoaderManager().initLoader(ID_MOVIE_INFO_LOADER, null, this);

        // Check if there is internet connection.
        if (!Utils.isOnline(this) && !mQueryType.equals(getString(R.string.pref_value_favorites)))
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
        // Find firstly in mResult.
        MovieInfo info;
        if (mResult != null && (info = mResult.getMovieInfo(posterRelPath)) != null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(MOVIE_INFO_EXTRA, info);
            boolean isFav = false;
            for (String path : mPosterRelativePathFavorite) {
                if (path.equals(posterRelPath)) {
                    isFav = true;
                    break;
                }
            }
            intent.putExtra(MOVIE_FAV_EXTRA, isFav);
            startActivity(intent);
        } else { // Search the database
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_MOVIE_INFO, posterRelPath);
            getLoaderManager().restartLoader(ID_MOVIE_INFO_LOADER, bundle, this);
        }
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
            getLoaderManager().restartLoader(ID_MOVIE_PATH_LOADER, null, this);
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
                mPosterRelativePath.clear();
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

    private void initRecyclerView() {
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(this, 400);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(this, (mPosterRelativePath != null && mPosterRelativePath.isEmpty())
                ? mPosterRelativePath : mPosterRelativePathFavorite);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onRetryButtonClicked(View view) {
        if (view.getId() == R.id.btn_search_again) {
            queryData();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri = MovieDetailsContract.MovieInfoEntry.CONTENT_URI_MOVIES;
        CursorLoader ret;
        switch (id) {
            case ID_MOVIE_PATH_LOADER:
                ret = new CursorLoader(this, queryUri, null, null, null, null);
                break;
            case ID_MOVIE_INFO_LOADER:
                String selectionArg = args.getString(BUNDLE_MOVIE_INFO);
                ret = new CursorLoader(this, queryUri, null, COLUMN_NAME_POSTER_PATH + "=?", new String[]{selectionArg}, null);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }

        return ret;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case ID_MOVIE_PATH_LOADER:
                mPosterRelativePathFavorite.clear();
                while (data.moveToNext()) {
                    int idx = data.getColumnIndex(COLUMN_NAME_POSTER_PATH);
                    String path = data.getString(idx);
                    if (path != null && !path.isEmpty())
                        mPosterRelativePathFavorite.add(path);
                }
                if (!mQueryType.equals(getString(R.string.pref_value_favorites))) return;
                mAdapter.swapData(mPosterRelativePathFavorite);
                showHideErrorMassage(null, false);
                break;
            case ID_MOVIE_INFO_LOADER:
                // Each poster path can belong to just one movie.
                boolean checkData = data.moveToFirst();
                if (checkData) {
                    MovieInfo info = new MovieInfo();
                    int idIdx = data.getColumnIndex(COLUMN_NAME_MOVIE_ID);
                    int posterPathIdx = data.getColumnIndex(COLUMN_NAME_POSTER_PATH);
                    info.setId(data.getString(idIdx));
                    info.setPosterRelativePath(data.getString(posterPathIdx));
                    Intent intent = new Intent(this, DetailsActivity.class);
                    intent.putExtra(MOVIE_INFO_EXTRA, info);
                    intent.putExtra(MOVIE_FAV_EXTRA, true);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        switch (id) {
            case ID_MOVIE_PATH_LOADER:
                mPosterRelativePathFavorite.clear();
                break;
            case ID_MOVIE_INFO_LOADER:
                break;
            default:
                throw new RuntimeException("Loader Did not reset properly: " + id);
        }
    }
}
