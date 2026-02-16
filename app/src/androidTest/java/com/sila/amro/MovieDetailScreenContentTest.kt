package com.sila.amro

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.MovieDetail
import com.sila.amro.presentation.movieDetail.MovieDetailScreenContent
import com.sila.amro.presentation.movieDetail.MovieDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MovieDetailScreenContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loading_showsLoading() {
        val state = MovieDetailUiState(
            isLoading = true,
            errorMessage = null,
            detail = null
        )

        composeRule.setContent {
            MovieDetailScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onBack = {},
                onRetry = {},
                onOpenImdb = {}
            )
        }

        composeRule.onNodeWithTag("loading").assertExists()

    }

    @Test
    fun error_showsMessage_andRetryCallsCallback() {
        var retryCalls = 0

        val state = MovieDetailUiState(
            isLoading = false,
            errorMessage = "error",
            detail = null
        )

        composeRule.setContent {
            MovieDetailScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onBack = {},
                onRetry = { retryCalls++ },
                onOpenImdb = {}
            )
        }

        composeRule.onNodeWithText("error").assertExists()

        composeRule.onNodeWithTag("retry_button").performClick()

        assertEquals(1, retryCalls)
    }

    @Test
    fun noData_showsNoDataMessage_andRetryCallsCallback() {
        var retryCalls = 0

        val state = MovieDetailUiState(
            isLoading = false,
            errorMessage = null,
            detail = null
        )

        composeRule.setContent {
            MovieDetailScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onBack = {},
                onRetry = { retryCalls++ },
                onOpenImdb = {}
            )
        }

        composeRule.onNodeWithTag("no_data").assertExists()

        composeRule.onNodeWithTag("retry_button").performClick()
        assertEquals(1, retryCalls)
    }

    @Test
    fun content_backButton_callsOnBack() {
        var backCalls = 0

        val detail = MovieDetail(
            id = 1,
            title = "Interstellar",
            tagline = "Mankind was born on Earth...",
            posterPath = "/poster.jpg",
            genres = listOf(Genre(1, "Sci-Fi")),
            overview = "A team travels through a wormhole...",
            voteAverage = 8.6,
            voteCount = 1000,
            runtimeMinutes = 169,
            releaseDate = "2014-11-07",
            status = "Released",
            budget = 165_000_000,
            revenue = 677_000_000,
            imdbId = null
        )

        val state = MovieDetailUiState(
            isLoading = false,
            errorMessage = null,
            detail = detail
        )

        composeRule.setContent {
            MovieDetailScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onBack = { backCalls++ },
                onRetry = {},
                onOpenImdb = {}
            )
        }

        composeRule.onNodeWithTag("back").performClick()
        assertEquals(1, backCalls)
    }

    @Test
    fun content_withImdbId_showsOpenImdbButton_andCallsOnOpenImdb() {
        var openedImdb: String? = null

        val detail = MovieDetail(
            id = 1,
            title = "Interstellar",
            tagline = null,
            posterPath = null,
            genres = listOf(Genre(1, "Sci-Fi")),
            overview = null,
            voteAverage = 8.6,
            voteCount = 1000,
            runtimeMinutes = 169,
            releaseDate = "2014-11-07",
            status = "Released",
            budget = 0,
            revenue = 0,
            imdbId = "tt0816692"
        )

        val state = MovieDetailUiState(
            isLoading = false,
            errorMessage = null,
            detail = detail
        )

        composeRule.setContent {
            MovieDetailScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onBack = {},
                onRetry = {},
                onOpenImdb = { openedImdb = it }
            )
        }

        composeRule.onNodeWithTag("open_imdb").assertExists()
        composeRule.onNodeWithTag("open_imdb").performClick()

        assertEquals("tt0816692", openedImdb)
    }

    @Test
    fun content_withoutImdbId_showsImdbUnavailableText() {
        val detail = MovieDetail(
            id = 1,
            title = "Interstellar",
            tagline = null,
            posterPath = null,
            genres = listOf(Genre(1, "Sci-Fi")),
            overview = null,
            voteAverage = 8.6,
            voteCount = 1000,
            runtimeMinutes = 169,
            releaseDate = "2014-11-07",
            status = "Released",
            budget = 0,
            revenue = 0,
            imdbId = null
        )

        val state = MovieDetailUiState(
            isLoading = false,
            errorMessage = null,
            detail = detail
        )

        composeRule.setContent {
            MovieDetailScreenContent(
                contentPadding = PaddingValues(0.dp),
                state = state,
                onBack = {},
                onRetry = {},
                onOpenImdb = {}
            )
        }


        composeRule.onNodeWithTag("imdb_link_unavailable").assertExists()
    }
}
