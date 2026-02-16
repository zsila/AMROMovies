package com.sila.amro.presentation.movieList

import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.SortOption

data class MovieListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val sort: SortOption = SortOption.Default,
    val allMovies: List<Movie> = emptyList(),
    val visibleMovies: List<Movie> = emptyList()
)