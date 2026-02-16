package com.sila.amro

import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.usecase.GetTop100MoviesUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetTop100MoviesUseCaseTest {

    private lateinit var useCase: GetTop100MoviesUseCase
    private lateinit var fakeRepository: FakeMovieRepository

    @Before
    fun setup() {
        fakeRepository = FakeMovieRepository()
        useCase = GetTop100MoviesUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns top 100 movies from repository` () = runTest {

        val fakeMovies: List<Movie> = listOf(
            Movie(id = 1, title = "Movie1"),
            Movie(id = 2, title = "Movie2")
        )

        val result = useCase()

        assertEquals(fakeMovies, result)

    }
}