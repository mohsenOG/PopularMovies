package com.mohsen.popularmovies;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohsen.popularmovies.common.Utils;
import com.mohsen.popularmovies.model.MovieInfo;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mohsen.popularmovies.MainActivity.*;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_poster) ImageView posterImageView;
    @BindView(R.id.tv_original_title) TextView originalTitleTextView;
    @BindView(R.id.tv_vote_average) TextView voteAverageTextView;
    @BindView(R.id.tv_release_date) TextView releaseDateTextView;
    @BindView(R.id.tv_overview) TextView overviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        initViews();

   }

    private void initViews() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString(TITLE_BUNDLE_EXTRA);
            if (title != null && !title.isEmpty())
                this.setTitle(title);
            String originalTitle = extras.getString(ORIGINAL_TITLE_BUNDLE_EXTRA);
            if (originalTitle != null)
                originalTitleTextView.setText(originalTitle);
            String posterPath = extras.getString(POSTER_PATH_BUNDLE_EXTRA);
            if (posterPath != null)
                Picasso.with(this).load(MovieInfo.posterPathConverter(posterPath)).into(posterImageView);
            String overview = extras.getString(OVERVIEW_BUNDLE_EXTRA);
            if (overview != null)
                overviewTextView.setText(overview);
            String voteAverage = extras.getString(VOTE_AVERAGE_BUNDLE_EXTRA);
            if (voteAverage != null)
                voteAverageTextView.setText(voteAverage);
            String releaseDate = extras.getString(RELEASE_DATE_BUNDLE_EXTRA);
            if (releaseDate != null) {
                String releaseDateFormatted = Utils.timeConverter(releaseDate);
                if (releaseDateFormatted != null)
                    releaseDateTextView.setText(releaseDateFormatted);
            }
        }
    }
}
