package com.sila.amro

import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.SortField
import com.sila.amro.domain.model.SortOption
import com.sila.amro.domain.model.SortOrder
import com.sila.amro.domain.usecase.ApplyFilterAndSortUseCase
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ApplyFilterAndSortUseCaseTest {

    private val useCase = ApplyFilterAndSortUseCase()

    private val movies = listOf(
        Movie(
            id = 1,
            title = "Zebra",
            popularity = 80.0,
            releaseDate = "2020-01-01",
            genreIds = listOf(10, 20)
        ),
        Movie(
            id = 2,
            title = "alpha", // lowercase on purpose to test case-insensitive sort
            popularity = 10.0,
            releaseDate = null, // to test null handling
            genreIds = listOf(20)
        ),
        Movie(
            id = 3,
            title = "Bravo",
            popularity = 50.0,
            releaseDate = "2019-06-01",
            genreIds = listOf(30)
        )
    )

    @Test
    fun `when selectedGenreId is null it does not filter`() {
        val result = useCase(
            movies = movies,
            selectedGenreId = null,
            sort = SortOption(SortField.POPULARITY, SortOrder.ASC)
        )

        // popularity ASC: id 2 (10), id 3 (50), id 1 (80)
        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `when selectedGenreId is set it filters movies containing that genre`() {
        val result = useCase(
            movies = movies,
            selectedGenreId = 20,
            sort = SortOption(SortField.TITLE, SortOrder.ASC)
        )

        // filtered: id 1 (Zebra), id 2 (alpha)
        // title ASC (case-insensitive): alpha, zebra
        assertEquals(listOf(2, 1), result.map { it.id })
    }

    @Test
    fun `sort by title is case-insensitive`() {
        val result = useCase(
            movies = movies,
            selectedGenreId = null,
            sort = SortOption(SortField.TITLE, SortOrder.ASC)
        )

        // titles lowercased: "alpha", "bravo", "zebra"
        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `sort by release date treats null as empty string and comes first in ASC`() {
        val result = useCase(
            movies = movies,
            selectedGenreId = null,
            sort = SortOption(SortField.RELEASE_DATE, SortOrder.ASC)
        )

        // releaseDate ASC with null -> "":
        // id 2: "" (null) comes first, then 2019..., then 2020...
        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `DESC order reverses the ASC sorted result`() {
        val asc = useCase(
            movies = movies,
            selectedGenreId = null,
            sort = SortOption(SortField.POPULARITY, SortOrder.ASC)
        )
        val desc = useCase(
            movies = movies,
            selectedGenreId = null,
            sort = SortOption(SortField.POPULARITY, SortOrder.DESC)
        )

        assertEquals(asc.map { it.id }.reversed(), desc.map { it.id })
    }
}