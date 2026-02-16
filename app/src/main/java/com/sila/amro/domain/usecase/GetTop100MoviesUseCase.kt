package com.sila.amro.domain.usecase

import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.repo.MovieRepository
import javax.inject.Inject

class GetTop100MoviesUseCase @Inject constructor(private val repo: MovieRepository) {
    suspend operator fun invoke(): List<Movie> = repo.getTop100ThisWeek()
}