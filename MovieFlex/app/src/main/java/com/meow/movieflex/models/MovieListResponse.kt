package com.meow.movieflex.models



data class MovieResponse(
    val titles: List<Movie>
)
data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val imdb_id: String?,
    val tmdb_id: Int?,
    val tmdb_type: String,
    val type: String,
    var posterPath: String? = null
)

data class MovieDetails(
    val id: Int,
    val title: String? = null,
    val release_date: String? = null,
    val user_rating: Double? = null,
    val plot_overview: String? = null  ,
    val type: String? = null,
    var posterLarge: String? = null,
    var backdrop: String? = null,
)


