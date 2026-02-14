package com.sila.amro.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val genreIds: List<Int>,
    val popularity: Double,
    val releaseDate: String?
)