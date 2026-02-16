package com.sila.amro.presentation.movieList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sila.amro.domain.model.SortField
import com.sila.amro.domain.model.SortOption
import com.sila.amro.domain.model.SortOrder
import com.sila.amro.domain.usecase.ApplyFilterAndSortUseCase
import com.sila.amro.domain.usecase.GetGenresUseCase
import com.sila.amro.domain.usecase.GetTop100MoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getGenres: GetGenresUseCase,
    private val getTopMovies: GetTop100MoviesUseCase,
    private val applyFilterAndSort: ApplyFilterAndSortUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MovieListUiState(isLoading = true))
    val state: StateFlow<MovieListUiState> = _state

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val genresDeferred = async { getGenres().sortedBy { it.name.lowercase() } }
                val moviesDeferred = async { getTopMovies() }

                val genres = genresDeferred.await()
                val movies = moviesDeferred.await()

                _state.update { s ->
                    val visible = applyFilterAndSort(movies, s.selectedGenreId, s.sort)
                    s.copy(
                        isLoading = false,
                        genres = genres,
                        allMovies = movies,
                        visibleMovies = visible
                    )
                }
            } catch (t: Throwable) {
                _state.update { it.copy(isLoading = false, errorMessage = t.message ?: "Something went wrong") }
            }
        }
    }

    fun setGenre(genreId: Int?) {
        _state.update { s ->
            val visible = applyFilterAndSort(s.allMovies, genreId, s.sort)
            s.copy(selectedGenreId = genreId, visibleMovies = visible)
        }
    }

    fun setSort(field: SortField) {
        _state.update { s ->
            val newSort = s.sort.copy(field = field)
            val visible = applyFilterAndSort(s.allMovies, s.selectedGenreId, newSort)
            s.copy(sort = newSort, visibleMovies = visible)
        }
    }

    fun toggleSortOrder() {
        _state.update { s ->
            val newOrder = if (s.sort.order == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC
            val newSort = SortOption(s.sort.field, newOrder)
            val visible = applyFilterAndSort(s.allMovies, s.selectedGenreId, newSort)
            s.copy(sort = newSort, visibleMovies = visible)
        }
    }
}
