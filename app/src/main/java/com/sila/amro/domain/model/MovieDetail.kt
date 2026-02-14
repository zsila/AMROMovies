package com.sila.amro.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String?,
    val posterPath: String?,
    val genres: List<Genre>,
    val overview: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Long,
    val revenue: Long,
    val status: String?,
    val imdbId: String?,
    val runtimeMinutes: Int?,
    val releaseDate: String?
)