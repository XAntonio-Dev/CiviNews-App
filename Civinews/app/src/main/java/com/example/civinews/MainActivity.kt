package com.example.civinews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.civinews.ui.navigation.AppNavigation
import com.example.civinews.ui.theme.CivinewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            val mainViewModel: MainViewModel = hiltViewModel()
            val startDestination = mainViewModel.startDestination
            val isLoading = mainViewModel.isLoading

            CivinewsTheme(darkTheme = isDarkTheme) {
                // Solo pintamos la navegación cuando el disco nos dice la ruta correcta
                if (!isLoading) {
                    AppNavigation(
                        startDestination = startDestination,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}