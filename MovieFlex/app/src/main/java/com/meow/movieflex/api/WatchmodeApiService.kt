package com.meow.movieflex.api

import com.meow.movieflex.models.MovieDetails
import com.meow.movieflex.models.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WatchmodeApiService {
    @GET("list-titles/")
    fun getMovies(
        @Query("apiKey") apiKey: String,
        @Query("titleTypes") titleTypes: String = "movie",
        @Query("sort_by") sortBy: String = "release_date_desc",
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 10 // Fetch 20 items per page
    ): Single<MovieResponse>

    @GET("list-titles/")
    fun getTvShows(
        @Query("apiKey") apiKey: String,
        @Query("titleTypes") titleTypes: String = "tv_series,tv_miniseries,tv_movie",
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 10
    ): Single<MovieResponse>

    @GET("title/{id}/details/")
    fun getMovieDetails(
        @Path("id") movieId: Int,
        @Query("apiKey") apiKey: String
    ): Single<MovieDetails>
}


