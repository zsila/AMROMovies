package com.sila.amro.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sila.amro.R
import com.sila.amro.presentation.movieDetail.MovieDetailScreen
import com.sila.amro.presentation.movieList.MovieListScreen
import com.sila.amro.presentation.navigation.navigateToDetail

object Routes {
    const val MovieList = "movieList"
    const val Detail = "detail"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.movies_title)) }) }
    ) { padding: PaddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.MovieList
        ) {
            composable(Routes.MovieList) {
                MovieListScreen(
                    contentPadding = padding,
                    onMovieClick = navController::navigateToDetail
                )
            }
            composable(
                route = "${Routes.Detail}/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) {
                MovieDetailScreen(
                    contentPadding = padding,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}



