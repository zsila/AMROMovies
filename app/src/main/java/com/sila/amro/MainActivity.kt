package com.sila.amro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sila.amro.presentation.AppRoot
import com.sila.amro.presentation.theme.AMROTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AMROTheme {
                AppRoot()
            }
        }
    }
}
