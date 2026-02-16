package com.sila.amro.domain.usecase

import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.SortField
import com.sila.amro.domain.model.SortOption
import com.sila.amro.domain.model.SortOrder
import javax.inject.Inject

class ApplyFilterAndSortUseCase @Inject constructor() {

    operator fun invoke(
        movies: List<Movie>,
        selectedGenreId: Int?,
        sort: SortOption
    ): List<Movie> {
        val filtered = if (selectedGenreId == null) movies
        else movies.filter { selectedGenreId in it.genreIds }

        val comparator = when (sort.field) {
            SortField.POPULARITY -> compareBy<Movie> { it.popularity }
            SortField.TITLE -> compareBy<Movie> { it.title.lowercase() }
            SortField.RELEASE_DATE -> compareBy<Movie> { it.releaseDate ?: "" }
        }

        val sorted = filtered.sortedWith(comparator)
        return if (sort.order == SortOrder.ASC) sorted else sorted.asReversed()
    }
}