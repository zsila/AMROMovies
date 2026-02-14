package com.sila.amro.data.network.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val tagline: String?,
    @SerializedName(value = "poster_path") val posterPath: String?,
    val genres: List<GenreDto>,
    @SerializedName(value = "overview") val overview: String?,
    @SerializedName(value = "vote_average") val voteAverage: Double,
    @SerializedName(value = "vote_count") val voteCount: Int,
    val budget: Long,
    val revenue: Long,
    val status: String?,
    @SerializedName(value = "imdb_id") val imdbId: String?,
    val runtime: Int?,
    @SerializedName(value = "release_date") val releaseDate: String?
)