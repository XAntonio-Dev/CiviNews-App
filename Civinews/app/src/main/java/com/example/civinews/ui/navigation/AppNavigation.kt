package com.example.civinews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.civinews.ui.screens.addReport.AddReportScreen
import com.example.civinews.ui.screens.auth.AuthScreen
import com.example.civinews.ui.screens.main.AdminMainScreen
import com.example.civinews.ui.screens.main.UserMainScreen
import com.example.civinews.ui.screens.main.UserMainScreen
import com.example.civinews.utils.Routes

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.AUTH
    ) {
        // Pantalla de Auth: Solo contenido, sin barras ni tema
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = { isAdmin ->
                    val destination = if (isAdmin) Routes.ADMIN else Routes.MAIN_APP
                    navController.navigate(destination) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // El Jefe de Ciudadanos: Tiene el Switch y la BottomBar
        composable(Routes.MAIN_APP) {
            UserMainScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToAddReport = { navController.navigate(Routes.ADD_REPORT) },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.MAIN_APP) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Acción: Solo contenido
        composable(Routes.ADD_REPORT) {
            AddReportScreen(
                onNavigateBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        // El Jefe de Admins: Su propio contenedor (AdminMainScreen)
        composable(Routes.ADMIN) {
            AdminMainScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.ADMIN) { inclusive = true }
                    }
                }
            )
        }
    }
}