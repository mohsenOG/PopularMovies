package com.mohsen.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import android.widget.ToggleButton;

import com.mohsen.popularmovies.common.ExpandableTextView;
import com.mohsen.popularmovies.common.TrailerButton;
import com.mohsen.popularmovies.common.Utils;
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

public class DetailsActivity extends AppCompatActivity {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (!Utils.isOnline(this))
            showHideErrorMessage(getString(R.string.no_internet), true);

        initViews();

   }

    private void initViews() {
        // Favorite button
        mFavButton.setChecked(false);
        mParentView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
        mFavButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //TODO Add movie to the db here
                    mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                }
                else {
                    //TODO Remove movie from the db
                    mFavButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
                }
            }
        });
        // Movie Details
        MovieInfo info = getIntent().getParcelableExtra(MOVIE_INFO_EXTRA);
        if (info == null) {
            showHideErrorMessage(getString(R.string.no_movie_info), true);
            return;
        }
        String id = info.getId();
        if (id != null && !id.isEmpty()) {
            queryData(id);
        }
    }

    private void queryData(String movieId) {
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
                String posterPath = result.getPosterRelativePath();
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


                fillViews(title, originalTitle, posterPath, overview, voteAverage, releaseDate, reviews, trailers);
            }


            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                showHideErrorMessage(t.getMessage(), true);
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
}
