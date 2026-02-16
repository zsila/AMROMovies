package com.sila.amro.presentation.movieDetail

import com.sila.amro.domain.model.MovieDetail

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val detail: MovieDetail? = null
)