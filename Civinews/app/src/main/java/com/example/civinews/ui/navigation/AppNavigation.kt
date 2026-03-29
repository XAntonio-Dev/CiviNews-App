package com.example.civinews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.civinews.ui.screens.home.HomeScreen
import com.example.civinews.ui.screens.login.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
}

// Encapsulamos el NavHost para dejar el MainActivity limpio
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                modifier = modifier,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                modifier = modifier,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}