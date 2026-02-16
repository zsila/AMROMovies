package com.sila.amro

import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.usecase.GetGenresUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetGenresUseCaseTest {

    lateinit var useCase: GetGenresUseCase
    lateinit var fakeRepository: FakeMovieRepository


    @Before
    fun setUp() {
        fakeRepository = FakeMovieRepository()
        useCase = GetGenresUseCase(fakeRepository)
    }



    @Test
    fun `invoke return genres from repository` () = runTest {

        val genres = listOf(
            Genre(id = 1, name = "Genre1"),
            Genre(id = 2, name = "Genre2")
        )

        val result = fakeRepository.getGenres()

        assertEquals(genres, result)

    }


}