package com.meow.movieflex.data.implementaion
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.meow.movieflex.api.WatchmodeApiService
import com.meow.movieflex.api.TmdbApiService
import com.meow.movieflex.models.Movie

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MoviesPagingSource(
    private val watchmodeApiService: WatchmodeApiService,
    private val tmdbApiService: TmdbApiService,
    private val watchmodeApiKey: String,
    private val tmdbApiKey: String,
    private val type: String // "movie" or "tv"
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentPage = params.key ?: 1

            val (movies, tvShows) = fetchMoviesAndTvShows(currentPage)

            val result = if (type == "movie") movies else tvShows

            LoadResult.Page(
                data = result,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (result.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
    }

    private suspend fun fetchMoviesAndTvShows(page: Int): Pair<List<Movie>, List<Movie>> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                io.reactivex.Single.zip(
                    watchmodeApiService.getMovies(watchmodeApiKey, page = page)
                        .flatMap { response ->
                            io.reactivex.Single.zip(response.titles.map { movie ->
                                if (movie.tmdb_id != null) {
                                    fetchTmdbImages(movie.tmdb_id, movie.tmdb_type)
                                        .map { (poster) -> movie.copy(posterPath = poster) }
                                        .onErrorReturnItem(movie)
                                } else {
                                    io.reactivex.Single.just(movie)
                                }
                            }) { updatedMovies -> updatedMovies.filterIsInstance<Movie>() }
                        }
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io()),

                    watchmodeApiService.getTvShows(watchmodeApiKey, page = page)
                        .flatMap { response ->
                            io.reactivex.Single.zip(response.titles.map { tvShow ->
                                if (tvShow.tmdb_id != null) {
                                    fetchTmdbImages(tvShow.tmdb_id, tvShow.tmdb_type)
                                        .map { (poster) -> tvShow.copy(posterPath = poster) }
                                        .onErrorReturnItem(tvShow)
                                } else {
                                    io.reactivex.Single.just(tvShow)
                                }
                            }) { updatedTvShows -> updatedTvShows.filterIsInstance<Movie>() }
                        }
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())

                ) { movies, tvShows ->
                    continuation.resume(Pair(movies, tvShows))
                }
                    .onErrorReturn {
                        continuation.resume(Pair(emptyList(), emptyList()))
                    }
                    .subscribe()
            }
        }
    }

    private fun fetchTmdbImages(tmdbId: Int, type: String): io.reactivex.Single<Pair<String?, String?>> {
        return if (type == "movie") {
            tmdbApiService.getMovieDetails(tmdbId, tmdbApiKey)
                .map { response ->
                    Pair(
                        response.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                        response.backdropPath?.let { "https://image.tmdb.org/t/p/w500$it" }
                    )
                }
        } else {
            tmdbApiService.getTvDetails(tmdbId, tmdbApiKey)
                .map { response ->
                    Pair(
                        response.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                        response.backdropPath?.let { "https://image.tmdb.org/t/p/w500$it" }
                    )
                }
        }
    }
}


