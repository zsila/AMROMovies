package com.sila.amro.data.mapper

import com.sila.amro.data.network.dto.GenreDto
import com.sila.amro.data.network.dto.MovieDetailDto
import com.sila.amro.data.network.dto.MovieDto
import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie
import com.sila.amro.domain.model.MovieDetail

fun GenreDto.toDomainModel(): Genre = Genre(id = id, name = name)

fun MovieDto.toDomainModel(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    genreIds = genreIds,
    popularity = popularity,
    releaseDate = releaseDate
)

fun MovieDetailDto.toDomainModel(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    tagline = tagline,
    posterPath = posterPath,
    genres = genres.map { it.toDomainModel() },
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    budget = budget,
    revenue = revenue,
    status = status,
    imdbId = imdbId,
    runtimeMinutes = runtime,
    releaseDate = releaseDate

)