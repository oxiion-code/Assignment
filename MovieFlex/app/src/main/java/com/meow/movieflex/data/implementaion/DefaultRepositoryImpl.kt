package com.meow.movieflex.data.implementaion

import androidx.paging.PagingSource
import com.meow.movieflex.api.TmdbApiService
import com.meow.movieflex.api.WatchmodeApiService
import com.meow.movieflex.data.repositories.DefaultRepository
import com.meow.movieflex.models.Movie
import com.meow.movieflex.models.MovieDetails
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRepositoryImpl @Inject constructor(
    private val watchmodeApiService: WatchmodeApiService,
    private val tmdbApiService: TmdbApiService
) : DefaultRepository {

    private val watchmodeApiKey = "9WS1YFyVbvL2qTqnxvIPTnIb6GAzRoHeSRmocpiz"
    private val tmdbApiKey = "ea42c3e0799fa25e7b1cbd4cf3f513dd"
    private val tmdbImageBaseUrl = "https://image.tmdb.org/t/p/w500"

    override fun getMoviesPagingSource(): PagingSource<Int, Movie> {
        return MoviesPagingSource(watchmodeApiService, tmdbApiService, watchmodeApiKey, tmdbApiKey, "movie")
    }

    override fun getTvShowsPagingSource(): PagingSource<Int, Movie> {
        return MoviesPagingSource(watchmodeApiService, tmdbApiService, watchmodeApiKey, tmdbApiKey, "tv")
    }
    override fun getMovieDetails(movieId: Int): Single<Result<MovieDetails>> {
        return watchmodeApiService.getMovieDetails(movieId, watchmodeApiKey)
            .map { movieDetails -> Result.success(movieDetails) }
            .subscribeOn(Schedulers.io())
            .onErrorReturn { e -> Result.failure(e) }
    }
}
