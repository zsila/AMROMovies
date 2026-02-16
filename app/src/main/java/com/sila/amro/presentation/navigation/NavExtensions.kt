package com.sila.amro.presentation.navigation

import androidx.navigation.NavController
import com.sila.amro.presentation.Routes

fun NavController.navigateToDetail(movieId: Int) {
    navigate("${Routes.Detail}/$movieId")
}