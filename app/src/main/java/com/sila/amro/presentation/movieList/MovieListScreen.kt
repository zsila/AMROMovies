package com.sila.amro.presentation.movieList

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sila.amro.R
import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.SortField
import com.sila.amro.domain.model.SortOrder
import com.sila.amro.presentation.components.ErrorContent
import com.sila.amro.presentation.components.LoadingContent

@Composable
fun MovieListScreen(
    contentPadding: PaddingValues,
    onMovieClick: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.sort, state.selectedGenreId) {
        listState.scrollToItem(0)
    }


    MovieListScreenContent(
        contentPadding = contentPadding,
        state = state,
        listState = listState,
        onMovieClick = onMovieClick,
        onRetry = viewModel::reload,
        onSelectGenre = viewModel::setGenre,
        onSelectSortField = viewModel::setSort,
        onToggleSortOrder = viewModel::toggleSortOrder
    )
}

@Composable
private fun ControlsRow(
    genres: List<Genre>,
    selectedGenreId: Int?,
    sortField: SortField,
    sortOrder: SortOrder,
    onSelectGenre: (Int?) -> Unit,
    onSelectSortField: (SortField) -> Unit,
    onToggleSortOrder: () -> Unit
) {
    Column(modifier = Modifier.padding(12.dp)) {
        Text(stringResource(R.string.filter_by_genre), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                modifier = Modifier.testTag("genre_all"),
                onClick = { onSelectGenre(null) },
                label = { Text(stringResource(R.string.all)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedGenreId == null) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            )
            genres.forEach { genre ->
                AssistChip(
                    modifier = Modifier.testTag("genre_${genre.id}"),
                    onClick = { onSelectGenre(genre.id) },
                    label = { Text(genre.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedGenreId == genre.id) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.sort), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 10.dp))
            SortChip(stringResource(R.string.popularity), sortField == SortField.POPULARITY) { onSelectSortField(SortField.POPULARITY) }
            SortChip(stringResource(R.string.title), sortField == SortField.TITLE) { onSelectSortField(SortField.TITLE) }
            SortChip(stringResource(R.string.release), sortField == SortField.RELEASE_DATE) { onSelectSortField(SortField.RELEASE_DATE) }

            IconButton(modifier = Modifier.testTag("sort_order"), onClick = onToggleSortOrder) {
                Icon(
                    imageVector = if (sortOrder == SortOrder.DESC) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                    contentDescription = "Toggle sort order"
                )
            }
        }
    }
}

@Composable
private fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        modifier = Modifier.testTag("sort_${text.lowercase()}"),
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}


@Composable
internal fun MovieListScreenContent(
    contentPadding: PaddingValues,
    state: MovieListUiState,
    listState: LazyListState = rememberLazyListState(),
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onSelectGenre: (Int?) -> Unit,
    onSelectSortField: (SortField) -> Unit,
    onToggleSortOrder: () -> Unit
) {
    when {
        state.isLoading -> LoadingContent(modifier = Modifier.padding(contentPadding))
        state.errorMessage != null -> ErrorContent(
            message = state.errorMessage,
            onRetry = onRetry,
            modifier = Modifier.padding(contentPadding)
        )
        else -> {
            val genreMap: Map<Int, Genre> = state.genres.associateBy { it.id }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                ControlsRow(
                    genres = state.genres,
                    selectedGenreId = state.selectedGenreId,
                    sortField = state.sort.field,
                    sortOrder = state.sort.order,
                    onSelectGenre = onSelectGenre,
                    onSelectSortField = onSelectSortField,
                    onToggleSortOrder = onToggleSortOrder
                )

                if (state.visibleMovies.isEmpty()) {
                    Column(modifier = Modifier
                        .padding(24.dp)
                        .testTag("no_movies_match_filter")) {
                        Text(stringResource(R.string.no_movies_match_filter), style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { onSelectGenre(null) },
                            modifier = Modifier.testTag("clear_filters")) {
                            Text(stringResource(R.string.clear_filters))
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.testTag("movie_list")
                    ) {
                        items(state.visibleMovies, key = { it.id }) { movie ->
                            MovieRow(
                                movie = movie,
                                genreMap = genreMap,
                                onClick = { onMovieClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
