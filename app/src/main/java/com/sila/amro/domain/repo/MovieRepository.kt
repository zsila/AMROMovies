package com.sila.amro.domain.repo

import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.MovieDetail

interface MovieRepository {
    suspend fun getGenres(): List<Genre>
    suspend fun getTop100ThisWeek(): List<Movie>
    suspend fun getMovieDetail(movieId: Int): MovieDetail
}