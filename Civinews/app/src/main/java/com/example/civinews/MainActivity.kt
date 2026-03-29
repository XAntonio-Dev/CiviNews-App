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
import com.example.civinews.ui.navigation.AppNavigation
import com.example.civinews.ui.theme.CivinewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Gestionamos el estado del tema aquí para que afecte a toda la app
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            CivinewsTheme(darkTheme = isDarkTheme) {
                // Delegamos el flujo visual al AppNavigation
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}