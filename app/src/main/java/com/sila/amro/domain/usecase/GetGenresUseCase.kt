package com.sila.amro.domain.usecase

import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.repo.MovieRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repo: MovieRepository
) {
    suspend operator fun invoke(): List<Genre> = repo.getGenres()
}