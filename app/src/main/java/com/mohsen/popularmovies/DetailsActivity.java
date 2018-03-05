package com.mohsen.popularmovies;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mohsen.popularmovies.common.ExpandableTextView;
import com.mohsen.popularmovies.common.TrailerButton;
import com.mohsen.popularmovies.common.Utils;
import com.mohsen.popularmovies.database.MovieDetailsContract;
import com.mohsen.popularmovies.model.MovieApi;
import com.mohsen.popularmovies.model.MovieDetails;
import com.mohsen.popularmovies.model.MovieInfo;
import com.mohsen.popularmovies.model.MovieReviewsQueryResult;
import com.mohsen.popularmovies.model.MovieVideo;
import com.mohsen.popularmovies.model.MovieVideosQueryResult;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mohsen.popularmovies.MainActivity.*;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_MOVIE_ID;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_ORIGINAL_TITLE;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_OVERVIEW;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_POSTER_PATH;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_RELEASE_DATE;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_REVIEWS;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_TITLE;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.COLUMN_NAME_VOTE_AVERAGE;
import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.CONTENT_URI_MOVIES;
import static com.mohsen.popularmovies.database.MovieDetailsContract.PATH_MOVIES;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";
    private static final int MOVIE_LOADER_ID = 30;
    private static final int MOVIE_LOADER_IS_CHECKED_ID = 40;
    private static final int MOVIE_LOADER_IS_NOT_CHECKED_ID = 50;

    @BindView(R.id.iv_poster) ImageView mPosterImageView;
    @BindView(R.id.tv_original_title) TextView mOriginalTitleTextView;
    @BindView(R.id.tv_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_reviews) ExpandableTextView mReviewTextView;
    @BindView(R.id.tbtn_favorite) ToggleButton mFavButton;

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_msg_display) TextView mErrorTextView;
    @BindView(R.id.cl_parent) View mParentView;
    @BindView(R.id.ll_trailer_parent) LinearLayout mTrailerLayout;

    private String mMovieId;
    private String mPosterPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Movie Details
        Intent intent = getIntent();
        MovieInfo info = intent.getParcelableExtra(MOVIE_INFO_EXTRA);
        boolean isFav = intent.getBooleanExtra(MOVIE_FAV_EXTRA, false);
        if (info == null) {
            showHideErrorMessage(getString(R.string.no_movie_info), true);
            return;
        }

        if (!Utils.isOnline(this) && !isFav)
            showHideErrorMessage(getString(R.string.no_internet), true);

        initViews(info , isFav);

   }

    private void initViews(MovieInfo info, boolean isFav) {
        mParentView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        mMovieId = info.getId();
        if (mMovieId != null && !mMovieId.isEmpty()) {
            queryData(mMovieId, isFav);
        }
        // Favorite button
        mFavButton.setChecked(isFav);
        if (isFav)
            mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
        else
            mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));

        mFavButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Bundle bundle = new Bundle();
                    bundle.putString(MOVIE_ID_EXTRA, mMovieId);
                    getLoaderManager().restartLoader(MOVIE_LOADER_IS_CHECKED_ID, bundle, DetailsActivity.this);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString(MOVIE_ID_EXTRA, mMovieId);
                    getLoaderManager().restartLoader(MOVIE_LOADER_IS_NOT_CHECKED_ID, bundle, DetailsActivity.this);
                }
            }
        });
    }

    private class ContentProviderAsync extends AsyncQueryHandler {
        public ContentProviderAsync(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
            Toast.makeText(DetailsActivity.this, getString(R.string.toast_fav_added), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
            Toast.makeText(DetailsActivity.this, getString(R.string.toast_fav_removed), Toast.LENGTH_SHORT).show();
        }
    }

    private void queryData(final String movieId, final boolean isFav) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(getString(R.string.api_key_title), BuildConfig.API_KEY);
        queryParams.put(getString(R.string.api_param_language), getString(R.string.api_param_language_value));
        String extraParams = getString(R.string.api_param_videos) + "," + getString(R.string.api_param_reviews);
        queryParams.put(getString(R.string.api_param_append_to_response), extraParams);
        MovieApi movieApi = MovieApi.retrofit.create(MovieApi.class);
        Call<MovieDetails> call = movieApi.getMovieDetails(movieId, queryParams);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                MovieDetails result = response.body();
                if (result == null) {
                    showHideErrorMessage(getString(R.string.no_movie_info), true);
                    return;
                }
                showHideErrorMessage(null, false);
                String title = result.getTitle();
                String originalTitle = result.getOriginalTitle();
                mPosterPath = result.getPosterRelativePath();
                String overview = result.getOverview();
                String voteAverage = result.getVoteAverage();
                String releaseDate = result.getReleaseDate();
                if (releaseDate != null)
                    releaseDate = Utils.timeConverter(releaseDate);
                MovieReviewsQueryResult reviewsList = result.getReviews();
                String reviews = null;
                if (reviewsList != null)
                    reviews = MovieReviewsQueryResult.ReviewConverter(reviewsList);
                List<MovieVideo> trailers = result.getVideos().getResult();

                fillViews(title, originalTitle, mPosterPath, overview, voteAverage, releaseDate, reviews, trailers);
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                if (isFav) {
                    Bundle bundle = new Bundle();
                    bundle.putString(MOVIE_ID_EXTRA, movieId);
                    getLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, DetailsActivity.this);
                } else {
                    showHideErrorMessage(t.getMessage(), true);
                }
            }
        });
    }

    private void showHideErrorMessage(@Nullable String msg, boolean show) {
        if (show) {
            mParentView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(msg);
        } else {
            mParentView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void fillViews(@Nullable String title, @Nullable String originalTitle,
                           @Nullable String posterPath, @Nullable String overview,
                           @Nullable String voteAverage, @Nullable String releaseDate,
                           @Nullable String reviews, @Nullable List<MovieVideo> videos) {
        if (title != null && !title.isEmpty())
            DetailsActivity.this.setTitle(title);
        if (originalTitle != null)
            mOriginalTitleTextView.setText(originalTitle);
        if (posterPath != null)
            Picasso.with(DetailsActivity.this).load(MovieInfo.posterPathConverter(posterPath)).into(mPosterImageView);
        if (overview != null)
            mOverviewTextView.setText(overview);
        if (voteAverage != null)
            mVoteAverageTextView.setText(voteAverage);
        if (releaseDate != null) {
            String releaseDateFormatted = Utils.timeConverter(releaseDate);
            if (releaseDateFormatted != null)
                mReleaseDateTextView.setText(releaseDateFormatted);
        }
        if (reviews != null) {
            mReviewTextView.setText(reviews);
        }
        if (videos != null && !videos.isEmpty()) {
            mTrailerLayout.removeAllViews();
            for (final MovieVideo video : videos) {
                if (video.getYoutubeId() == null || video.getYoutubeId().isEmpty()) continue;
                String name = video.getName();
                if (name != null && name.length() > 20) {
                    name = name.substring(0, 17);
                    name += "...";
                }
                TrailerButton trailerBtn = new TrailerButton(this);
                trailerBtn.setText(name);
                trailerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MovieVideosQueryResult.YOUTUBE_APP_URL + video.getYoutubeId()));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MovieVideosQueryResult.YOUTUBE_URL + video.getYoutubeId()));
                        try {
                            startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            startActivity(webIntent);
                        }
                    }
                });
                mTrailerLayout.addView(trailerBtn);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:
            case MOVIE_LOADER_IS_CHECKED_ID:
            case MOVIE_LOADER_IS_NOT_CHECKED_ID:
                String selectionArg = args.getString(MOVIE_ID_EXTRA);
                return new CursorLoader(this, MovieDetailsContract.MovieInfoEntry.CONTENT_URI_MOVIES, null, COLUMN_NAME_MOVIE_ID + "=?", new String[]{selectionArg}, null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER_ID:
                if (data == null || !data.moveToFirst()) {
                    showHideErrorMessage(loader.toString(), true);
                    return;
                }
                showHideErrorMessage(null, false);
                int titleIdx = data.getColumnIndex(COLUMN_NAME_TITLE);
                int origTitleIdx = data.getColumnIndex(COLUMN_NAME_ORIGINAL_TITLE);
                int posterPathIdx = data.getColumnIndex(COLUMN_NAME_POSTER_PATH);
                int overviewIdx = data.getColumnIndex(COLUMN_NAME_OVERVIEW);
                int votrAvgIdx = data.getColumnIndex(COLUMN_NAME_VOTE_AVERAGE);
                int releaseIdx = data.getColumnIndex(COLUMN_NAME_RELEASE_DATE);
                int reviewsIdx = data.getColumnIndex(COLUMN_NAME_REVIEWS);

                String title = data.getString(titleIdx);
                String origTitle = data.getString(origTitleIdx);
                mPosterPath = data.getString(posterPathIdx);
                String overview = data.getString(overviewIdx);
                String voteAvg = data.getString(votrAvgIdx);
                String releaseDate = data.getString(releaseIdx);
                if (releaseDate != null)
                    releaseDate = Utils.timeConverter(releaseDate);
                String reviews = data.getString(reviewsIdx);

                fillViews(title, origTitle, mPosterPath, overview, voteAvg, releaseDate, reviews, null);
                break;
            case MOVIE_LOADER_IS_CHECKED_ID:
                if (data == null || !data.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_MOVIE_ID, mMovieId);
                    values.put(COLUMN_NAME_TITLE, DetailsActivity.this.getTitle().toString());
                    values.put(COLUMN_NAME_ORIGINAL_TITLE, mOriginalTitleTextView.getText().toString());
                    values.put(COLUMN_NAME_POSTER_PATH, mPosterPath);
                    values.put(COLUMN_NAME_OVERVIEW, mOverviewTextView.getText().toString());
                    values.put(COLUMN_NAME_VOTE_AVERAGE, mVoteAverageTextView.getText().toString());
                    values.put(COLUMN_NAME_RELEASE_DATE, Utils.timeConverterToOriginal(mReleaseDateTextView.getText().toString()));
                    values.put(COLUMN_NAME_REVIEWS, mReviewTextView.getText().toString());
                    ContentProviderAsync cpAsync = new ContentProviderAsync(getContentResolver());
                    cpAsync.startInsert(1, null, MovieDetailsContract.MovieInfoEntry.CONTENT_URI_MOVIES, values);
                }
                break;

            case MOVIE_LOADER_IS_NOT_CHECKED_ID:
                if (data != null && data.moveToFirst()) {
                    Uri removeUri = CONTENT_URI_MOVIES.buildUpon()
                            .appendPath(PATH_MOVIES)
                            .appendPath(mMovieId)
                            .build();
                    ContentProviderAsync cpAsync2 = new ContentProviderAsync(getContentResolver());
                    cpAsync2.startDelete(1, null, removeUri, null, null);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
