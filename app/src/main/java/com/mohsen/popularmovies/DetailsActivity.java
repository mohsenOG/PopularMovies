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
        MovieInfo info = getIntent().getParcelableExtra(MOVIE_INFO_EXTRA);
        if (info == null) return;
        String title = info.getTitle();
        if (title != null && !title.isEmpty())
            this.setTitle(title);
        String originalTitle = info.getOriginalTitle();
        if (originalTitle != null)
            originalTitleTextView.setText(originalTitle);
        String posterPath = info.getPosterRelativePath();
        if (posterPath != null)
            Picasso.with(this).load(MovieInfo.posterPathConverter(posterPath)).into(posterImageView);
        String overview = info.getOverview();
        if (overview != null)
            overviewTextView.setText(overview);
        String voteAverage = info.getVoteAverage();
        if (voteAverage != null)
            voteAverageTextView.setText(voteAverage);
        String releaseDate = info.getReleaseDate();
        if (releaseDate != null) {
            String releaseDateFormatted = Utils.timeConverter(releaseDate);
            if (releaseDateFormatted != null)
                releaseDateTextView.setText(releaseDateFormatted);
        }
    }
}
