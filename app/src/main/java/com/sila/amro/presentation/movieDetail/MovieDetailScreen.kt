package com.sila.amro.presentation.movieDetail
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sila.amro.BuildConfig
import com.sila.amro.R
import com.sila.amro.presentation.components.ErrorContent
import com.sila.amro.presentation.components.LoadingContent
import java.text.NumberFormat
import androidx.core.net.toUri


@Composable
fun MovieDetailScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    MovieDetailScreenContent(
        contentPadding = contentPadding,
        state = state,
        onBack = onBack,
        onRetry = viewModel::reload,
        onOpenImdb = { imdbId ->
            val url = "https://www.imdb.com/title/$imdbId/"
            ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        }
    )

}

@Composable
private fun KeyValue(key: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = key, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun money(amount: Long): String {
    if (amount <= 0L) return "—"
    val nf = NumberFormat.getCurrencyInstance()
    nf.maximumFractionDigits = 0
    return nf.format(amount)
}


@Composable
internal fun MovieDetailScreenContent(
    contentPadding: PaddingValues,
    state: MovieDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenImdb: (String) -> Unit
) {
    when {
        state.isLoading -> LoadingContent(modifier = Modifier
            .padding(contentPadding)
            .testTag("loading"))

        state.errorMessage != null -> ErrorContent(
            message = state.errorMessage,
            onRetry = onRetry,
            modifier = Modifier
                .padding(contentPadding)
                .testTag("error")
        )

        state.detail == null -> ErrorContent(
            message = stringResource(R.string.no_data),
            onRetry = onRetry,
            modifier = Modifier
                .padding(contentPadding)
                .testTag("no_data")
        )

        else -> {
            val detail = state.detail
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        modifier = Modifier.testTag("back"),
                        onClick = onBack
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text(detail.title, style = MaterialTheme.typography.headlineSmall)
                        detail.tagline?.takeIf { it.isNotBlank() }?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = detail.posterPath?.let { BuildConfig.TMDB_IMAGE_BASE_URL + it },
                    contentDescription = "${detail.title} poster"
                )

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    detail.genres.take(5).forEach { g ->
                        AssistChip(modifier = Modifier.testTag("genre_${g.id}"),
                            onClick = {},
                            label = { Text(g.name) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                detail.overview?.takeIf { it.isNotBlank() }?.let {
                    Text(stringResource(R.string.description), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.statistics), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                KeyValue(stringResource(R.string.vote_average), "%.1f".format(detail.voteAverage))
                KeyValue(stringResource(R.string.vote_count), detail.voteCount.toString())
                KeyValue(stringResource(R.string.runtime), detail.runtimeMinutes?.let { "$it min" } ?: "—")
                KeyValue(stringResource(R.string.release_date), detail.releaseDate ?: "—")
                KeyValue(stringResource(R.string.status), detail.status ?: "—")
                KeyValue(stringResource(R.string.budget), money(detail.budget))
                KeyValue(stringResource(R.string.revenue), money(detail.revenue))

                Spacer(Modifier.height(16.dp))
                val imdbId = detail.imdbId
                if (!imdbId.isNullOrBlank()) {
                    Button(modifier = Modifier.testTag("open_imdb"),
                        onClick = { onOpenImdb(imdbId) }) {
                        Text(stringResource(R.string.open_imdb))
                    }
                } else {
                    Text(modifier = Modifier.testTag("imdb_link_unavailable"),
                        text = stringResource(R.string.imdb_link_unavailable),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

