package com.mohsen.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohsen on 22.02.2018.
 *
 */

public class MovieInfoQueryResult {

    @SerializedName("results")
    private final List<MovieInfo> results;

    public MovieInfoQueryResult(List<MovieInfo> results) {
        this.results = results;
    }

    public List<String> getPosterRelativePaths() {
        List<String> paths = new ArrayList<>();
        for(MovieInfo info : results) {
            paths.add(info.getPosterRelativePath());
        }
        return paths;
    }

    public MovieInfo getMovieInfo(String posterRelativePath) {
        for (MovieInfo info : results) {
            if (info.getPosterRelativePath().equals(posterRelativePath))
                return info;
        }
        return null;
    }
}
