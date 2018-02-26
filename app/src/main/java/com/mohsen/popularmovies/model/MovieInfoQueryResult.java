package com.mohsen.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohsen on 22.02.2018.
 *
 */

public class MovieInfoQueryResult {

    @SerializedName("page")
    private final String page;
    @SerializedName("total_result")
    private final String totalResult;
    @SerializedName("total_pages")
    private final String totalPages;
    @SerializedName("results")
    private final List<MovieInfo> results;

    public MovieInfoQueryResult(String page, String totalResult, String totalPages, List<MovieInfo> results) {
        this.page = page;
        this.totalResult = totalResult;
        this.totalPages = totalPages;
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

    public String getPage() { return page; }

    public String getTotalResult() { return totalResult; }

    public String getTotalPages() { return totalPages; }
}
