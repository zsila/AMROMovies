package com.sila.amro

import androidx.lifecycle.SavedStateHandle
import com.sila.amro.domain.model.MovieDetail
import com.sila.amro.domain.usecase.GetMovieDetailUseCase
import com.sila.amro.presentation.movieDetail.MovieDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init reload loads movie detail`() = runTest {
        val repo = FakeMovieRepository().apply {
            movieDetailToReturn = MovieDetail(id = 7, title = "Seven", overview = "Overview")
        }

        val vm = MovieDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("movieId" to 7)),
            getMovieDetail = GetMovieDetailUseCase(repo)
        )

        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNotNull(state.detail)
        assertEquals(7, state.detail?.id)
        assertEquals("Seven", state.detail?.title)
    }

    @Test
    fun `when repository throws, state becomes error`() = runTest {
        val repo = FakeMovieRepository().apply {
            movieDetailToReturn = null // Fake repo throws IllegalArgumentException
        }

        val vm = MovieDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("movieId" to 99)),
            getMovieDetail = GetMovieDetailUseCase(repo)
        )

        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertNull(state.detail)
    }

    @Test(expected = IllegalStateException::class)
    fun `missing movieId in SavedStateHandle throws`() {
        val repo = FakeMovieRepository()
        MovieDetailViewModel(
            savedStateHandle = SavedStateHandle(), // no movieId => checkNotNull throws IllegalStateException
            getMovieDetail = GetMovieDetailUseCase(repo)
        )
    }
}
