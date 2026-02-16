package com.sila.amro.data.network

import com.sila.amro.data.network.dto.GenresResponseDto
import com.sila.amro.data.network.dto.MovieDetailDto
import com.sila.amro.data.network.dto.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("trending/movie/week")
    suspend fun getTrendingMoviesWeek(@Query("page") page: Int): MovieListResponseDto

    @GET("genre/movie/list")
    suspend fun getGenres(): GenresResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): MovieDetailDto
}