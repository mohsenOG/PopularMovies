package com.mohsen.popularmovies.model;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Mohsen on 21.02.2018.
 *
 */

public interface MovieApi {

    String BASE_URL = "https://api.themoviedb.org";

    @GET("/3/movie/{queryType}")
    Call<MovieQueryResult> getMovies(@Path("queryType") String queryType, @QueryMap Map<String, String> options);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
