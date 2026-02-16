package com.sila.amro

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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

        // IMPORTANT: Adjust this to your ErrorContent button text
        // e.g. "Retry" / "Try again"
        composeRule.onNodeWithText("Retry", ignoreCase = true).performClick()

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

        composeRule.onNodeWithText("No movies match your filters.").assertExists()
        composeRule.onNodeWithText("Clear filters").performClick()

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

        // These labels come from string resources in your UI.
        // If your actual strings differ, update them.
        composeRule.onNodeWithText("Title", ignoreCase = true).performClick()
        composeRule.onNodeWithText("Release", ignoreCase = true).performClick()

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

        // Tap "Action" chip -> should send 28
        composeRule.onNodeWithText("Action").performClick()

        // Tap "All" chip -> should send null
        // If your localized string isn't literally "All", adjust this.
        composeRule.onNodeWithText("All", ignoreCase = true).performClick()

        assertEquals(listOf(28, null), genreCalls)
    }

    @Test
    fun clickingMovie_callsOnMovieClick_withMovieId() {
        var clickedId: Int? = null

        // NOTE: This test requires that MovieRow exposes something clickable that we can find:
        // - either the movie title text (recommended), OR
        // - a testTag like "movie_row_${movie.id}".
        //
        // If MovieRow does NOT show title text, add a testTag in MovieRow.
        val movieA = Movie(
            id = 101,
            title = "Movie A"
            // fill other required fields in your Movie data class if any
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

        // Option A: click by visible title text (works only if MovieRow displays it)
        composeRule.onNodeWithText("Movie A").performClick()

        // Option B (recommended): if you add Modifier.testTag("movie_row_${movie.id}") in MovieRow:
        // composeRule.onNodeWithTag("movie_row_101").performClick()

        assertEquals(101, clickedId)
    }
}
