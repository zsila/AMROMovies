package com.sila.amro.data.network.dto

data class MovieListResponseDto(
    val page: Int,
    val results: List<MovieDto>
)