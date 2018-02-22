package com.mohsen.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohsen on 22.02.2018.
 */

public class MovieQueryResult {

    @SerializedName("page")
    private String page;
    @SerializedName("total_result")
    private String totalResult;
    @SerializedName("total_pages")
    private String totalPages;
    @SerializedName("results")
    private List<MovieInfo> results;

    public MovieQueryResult(String page, String totalResult, String totalPages, List<MovieInfo> results) {
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

}
