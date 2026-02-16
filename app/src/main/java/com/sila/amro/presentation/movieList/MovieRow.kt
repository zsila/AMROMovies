package com.sila.amro.presentation.movieList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sila.amro.BuildConfig
import com.sila.amro.domain.model.Genre
import com.sila.amro.domain.model.Movie

@Composable
fun MovieRow(
    movie: Movie,
    genreMap: Map<Int, Genre>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = movie.posterPath?.let { BuildConfig.TMDB_IMAGE_BASE_URL + it },
            contentDescription = "${movie.title} poster",
            modifier = Modifier.size(92.dp).aspectRatio(2f / 3f)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            val genres = movie.genreIds.mapNotNull { genreMap[it]?.name }.take(3)
            Text(
                text = if (genres.isEmpty()) "—" else genres.joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium
            )
            movie.releaseDate?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(text = "Release: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}