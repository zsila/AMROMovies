package com.sila.amro

import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.MovieDetail
import com.sila.amro.domain.repo.MovieRepository

class FakeMovieRepository : MovieRepository {

    var moviesToReturn: List<Movie> = listOf(Movie(1, "Movie1"), Movie(2, "Movie2"))
    var genresToReturn: List<Genre> = listOf(Genre(1, "Genre1"), Genre(2, "Genre2"))
    var movieDetailToReturn: MovieDetail? = MovieDetail(1, "Movie1", "Overview1")

    var shouldThrowOnMovies: Boolean = false

    override suspend fun getGenres(): List<Genre> {
        return genresToReturn
    }

    override suspend fun getTop100ThisWeek(): List<Movie> {
        if (shouldThrowOnMovies) {
            throw RuntimeException("Error")
        }
        return moviesToReturn
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail {
        return movieDetailToReturn ?: throw IllegalArgumentException("Movie not found")
    }
}
