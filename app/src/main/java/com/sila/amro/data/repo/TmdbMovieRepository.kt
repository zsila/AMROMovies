package com.sila.amro.data.repo

import com.sila.amro.data.mapper.toDomainModel
import com.sila.amro.data.network.TmdbApi
import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.MovieDetail
import com.sila.amro.domain.repo.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class TmdbMovieRepository @Inject constructor(
    private val api: TmdbApi
) : MovieRepository {

    override suspend fun getGenres(): List<Genre> =
        api.getGenres().genres.map { it.toDomainModel() }

    override suspend fun getTop100ThisWeek(): List<Movie> = coroutineScope {
        val results = (1..5).map { page -> async { api.getTrendingMoviesWeek(page).results } }
            .flatMap { it.await() }

        results.distinctBy { it.id }.take(100).map { it.toDomainModel() }
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail =
        api.getMovieDetail(movieId).toDomainModel()
}