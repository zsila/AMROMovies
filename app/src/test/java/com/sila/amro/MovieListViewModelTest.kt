package com.sila.amro

import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.SortField
import com.sila.amro.domain.model.SortOrder
import com.sila.amro.domain.usecase.ApplyFilterAndSortUseCase
import com.sila.amro.domain.usecase.GetGenresUseCase
import com.sila.amro.domain.usecase.GetTop100MoviesUseCase
import com.sila.amro.presentation.movieList.MovieListViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun makeViewModel(repo: FakeMovieRepository): MovieListViewModel {
        val getGenres = GetGenresUseCase(repo)
        val getTopMovies = GetTop100MoviesUseCase(repo)
        val applyFilterAndSort = ApplyFilterAndSortUseCase()

        return MovieListViewModel(getGenres, getTopMovies, applyFilterAndSort)
    }


    @Test
    fun `init reload loads genres sorted by name and movies` () = runTest {
        val repo = FakeMovieRepository().apply {
            genresToReturn = listOf(
                Genre(2, "zAction"),
                Genre(1, "Comedy")
            )
            moviesToReturn = listOf(
                Movie(id = 1, title = "A", popularity = 10.0, genreIds = listOf(1)),
                Movie(id = 2, title = "B", popularity = 50.0, genreIds = listOf(2))
            )
        }

        val vm = makeViewModel(repo)

        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)

        // Genres sorted case-insensitively
        assertEquals(listOf("Comedy", "zAction"), state.genres.map { it.name })

        // allMovies set
        assertEquals(listOf(1, 2), state.allMovies.map { it.id })

        // Default sort = popularity DESC => movie 2 then 1
        assertEquals(listOf(2, 1), state.visibleMovies.map { it.id })

    }


    @Test
    fun `reload success emits loading then success`() = runTest {
        val repo = FakeMovieRepository()

        val vm = makeViewModel(repo)

        advanceUntilIdle()

        val state = vm.state.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertTrue(state.allMovies.isNotEmpty())

    }


    @Test
    fun `setGenre filters visibleMovies`() = runTest {
        val repo = FakeMovieRepository().apply {
            moviesToReturn = listOf(
                Movie(id = 1, title = "A", popularity = 10.0, genreIds = listOf(1, 2)),
                Movie(id = 2, title = "B", popularity = 50.0, genreIds = listOf(2)),
                Movie(id = 3, title = "C", popularity = 20.0, genreIds = listOf(3)),
            )
        }

        val vm = makeViewModel(repo)
        advanceUntilIdle()

        vm.setGenre(2)

        val state = vm.state.value
        assertEquals(2, state.selectedGenreId)
        // Only movies with genreId 2 remain; keep default sort popularity DESC => id 2 then 1
        assertEquals(listOf(2, 1), state.visibleMovies.map { it.id })
    }

    @Test
    fun `setSort changes sorting field`() = runTest {
        val repo = FakeMovieRepository().apply {
            moviesToReturn = listOf(
                Movie(id = 1, title = "Zebra", popularity = 10.0),
                Movie(id = 2, title = "alpha", popularity = 50.0),
                Movie(id = 3, title = "Bravo", popularity = 20.0),
            )
        }

        val vm = makeViewModel(repo)
        advanceUntilIdle()

        vm.setSort(SortField.TITLE)

        val state = vm.state.value
        assertEquals(SortField.TITLE, state.sort.field)
        // Default order is DESC; title sort is case-insensitive => Zebra > Bravo > alpha
        assertEquals(listOf(1, 3, 2), state.visibleMovies.map { it.id })
    }

    @Test
    fun `toggleSortOrder flips asc-desc and updates visibleMovies`() = runTest {
        val repo = FakeMovieRepository().apply {
            moviesToReturn = listOf(
                Movie(id = 1, title = "A", popularity = 10.0),
                Movie(id = 2, title = "B", popularity = 50.0),
                Movie(id = 3, title = "C", popularity = 20.0),
            )
        }

        val vm = makeViewModel(repo)
        advanceUntilIdle()

        // Default: popularity DESC => 2,3,1
        assertEquals(listOf(2, 3, 1), vm.state.value.visibleMovies.map { it.id })
        assertEquals(SortOrder.DESC, vm.state.value.sort.order)

        vm.toggleSortOrder()

        val state = vm.state.value
        assertEquals(SortOrder.ASC, state.sort.order)
        // popularity ASC => 1,3,2
        assertEquals(listOf(1, 3, 2), state.visibleMovies.map { it.id })
    }


}