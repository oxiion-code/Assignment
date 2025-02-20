package com.meow.movieflex.data.repositories

import androidx.paging.PagingSource
import com.meow.movieflex.models.Movie
import com.meow.movieflex.models.MovieDetails
import io.reactivex.Single

interface DefaultRepository {
    fun getMovieDetails(movieId: Int): Single<Result<MovieDetails>>
    fun getMoviesPagingSource(): PagingSource<Int, Movie>
    fun getTvShowsPagingSource(): PagingSource<Int, Movie>
}

