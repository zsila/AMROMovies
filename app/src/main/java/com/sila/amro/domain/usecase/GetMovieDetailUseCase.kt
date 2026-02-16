package com.sila.amro.domain.usecase

import com.sila.amro.domain.model.MovieDetail
import com.sila.amro.domain.repo.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val repo: MovieRepository) {
    suspend operator fun invoke(id: Int): MovieDetail = repo.getMovieDetail(id)
}