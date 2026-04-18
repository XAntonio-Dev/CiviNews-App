package com.example.civinews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.civinews.ui.screens.addReport.AddReportScreen
import com.example.civinews.ui.screens.admin.AdminScreen
import com.example.civinews.ui.screens.auth.AuthScreen
import com.example.civinews.ui.screens.home.HomeScreen
import com.example.civinews.utils.Routes

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
        startDestination = Routes.AUTH
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                modifier = modifier,
                onAuthSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate(Routes.ADMIN) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                modifier = modifier,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToAddReport = {
                    navController.navigate(Routes.ADD_REPORT)
                }
            )
        }

        composable(Routes.ADD_REPORT) {
            AddReportScreen(
                modifier = modifier,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN) {
            AdminScreen(
                modifier = modifier,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}