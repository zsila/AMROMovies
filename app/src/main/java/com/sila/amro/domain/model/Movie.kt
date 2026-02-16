package com.sila.amro.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String? = null,
    val genreIds: List<Int> = listOf(),
    val popularity: Double = 0.0,
    val releaseDate: String? = null
)