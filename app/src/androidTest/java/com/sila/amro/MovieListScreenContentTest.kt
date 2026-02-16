package com.sila.amro

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.SortField
import com.sila.amro.domain.model.SortOrder
import com.sila.amro.domain.model.SortOption
import com.sila.amro.presentation.movieList.MovieListScreenContent
import com.sila.amro.presentation.movieList.MovieListUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MovieListScreenContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun errorState_showsMessage_andRetryInvokesCallback() {
        var retryCalls = 0

        val state = MovieListUiState(
            isLoading = false,
            errorMessage = "Network error",
            genres = emptyList(),
            selectedGenreId = null,
            sort = SortOption.Default,
            allMovies = emptyList(),
            visibleMovies = emptyList()
        )

        composeRule.setContent {
            MovieListScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onMovieClick = {},
                onRetry = { retryCalls++ },
                onSelectGenre = {},
                onSelectSortField = {},
                onToggleSortOrder = {}
            )
        }

        composeRule.onNodeWithText("Network error").assertExists()

        composeRule.onNodeWithTag("retry_button").performClick()

        assertEquals(1, retryCalls)
    }

    @Test
    fun emptyVisibleMovies_showsEmptyMessage_andClearFiltersCallsOnSelectGenreNull() {
        val genreCalls = mutableListOf<Int?>()

        val state = MovieListUiState(
            isLoading = false,
            errorMessage = null,
            genres = listOf(Genre(id = 28, name = "Action")),
            selectedGenreId = 28,
            sort = SortOption.Default,
            allMovies = emptyList(),
            visibleMovies = emptyList()
        )

        composeRule.setContent {
            MovieListScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onMovieClick = {},
                onRetry = {},
                onSelectGenre = { genreCalls.add(it) },
                onSelectSortField = {},
                onToggleSortOrder = {}
            )
        }

        composeRule.onNodeWithTag("no_movies_match_filter").assertExists()
        composeRule.onNodeWithTag("clear_filters").performClick()

        assertEquals(listOf<Int?>(null), genreCalls)
    }

    @Test
    fun clickingSortChips_callsOnSelectSortField_andToggleCallsOnToggleSortOrder() {
        val sortCalls = mutableListOf<SortField>()
        var toggleCalls = 0

        val state = MovieListUiState(
            isLoading = false,
            errorMessage = null,
            genres = emptyList(),
            selectedGenreId = null,
            sort = SortOption(field = SortField.POPULARITY, order = SortOrder.DESC),
            allMovies = emptyList(),
            visibleMovies = emptyList()
        )

        composeRule.setContent {
            MovieListScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onMovieClick = {},
                onRetry = {},
                onSelectGenre = {},
                onSelectSortField = { sortCalls.add(it) },
                onToggleSortOrder = { toggleCalls++ }
            )
        }

        composeRule.onNodeWithTag("sort_title").performClick()
        composeRule.onNodeWithTag("sort_release").performClick()
        composeRule.onNodeWithContentDescription("Toggle sort order").performClick()

        assertEquals(listOf(SortField.TITLE, SortField.RELEASE_DATE), sortCalls)
        assertEquals(1, toggleCalls)
    }

    @Test
    fun selectingGenreChip_callsOnSelectGenre_withGenreId_andAllCallsNull() {
        val genreCalls = mutableListOf<Int?>()

        val state = MovieListUiState(
            isLoading = false,
            errorMessage = null,
            genres = listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 35, name = "Comedy")
            ),
            selectedGenreId = null,
            sort = SortOption.Default,
            allMovies = emptyList(),
            visibleMovies = emptyList()
        )

        composeRule.setContent {
            MovieListScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onMovieClick = {},
                onRetry = {},
                onSelectGenre = { genreCalls.add(it) },
                onSelectSortField = {},
                onToggleSortOrder = {}
            )
        }


        composeRule.onNodeWithTag("genre_28").performClick()
        composeRule.onNodeWithTag("genre_all").performClick()

        assertEquals(listOf(28, null), genreCalls)
    }

    @Test
    fun clickingMovie_callsOnMovieClick_withMovieId() {
        var clickedId: Int? = null

        val movieA = Movie(
            id = 101,
            title = "Movie A"
        )

        val state = MovieListUiState(
            isLoading = false,
            errorMessage = null,
            genres = emptyList(),
            selectedGenreId = null,
            sort = SortOption.Default,
            allMovies = listOf(movieA),
            visibleMovies = listOf(movieA)
        )

        composeRule.setContent {
            MovieListScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onMovieClick = { clickedId = it },
                onRetry = {},
                onSelectGenre = {},
                onSelectSortField = {},
                onToggleSortOrder = {}
            )
        }

        composeRule.onNodeWithTag("movie_item_101").performClick()

        assertEquals(101, clickedId)
    }
}
