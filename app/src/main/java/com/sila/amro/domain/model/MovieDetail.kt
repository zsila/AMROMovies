package com.sila.amro.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String? = null,
    val posterPath: String? = null,
    val genres: List<Genre> = emptyList(),
    val overview: String? = null,
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0,
    val budget: Long = 0,
    val revenue: Long = 0,
    val status: String? = null,
    val imdbId: String? = null,
    val runtimeMinutes: Int? = null,
    val releaseDate: String? = null
)